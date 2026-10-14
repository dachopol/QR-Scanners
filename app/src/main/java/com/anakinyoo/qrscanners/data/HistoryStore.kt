package com.anakinyoo.qrscanners.data

import android.content.Context
import android.util.AtomicFile
import com.anakinyoo.qrscanners.model.HistoryRecord
import com.anakinyoo.qrscanners.model.QrType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException

/** History stays fail-closed when unreadable content cannot be safely preserved. */
enum class HistoryReadStatus { LOADING, READY, RECOVERY_SAVED, BLOCKED }

class HistoryStore(context: Context) {

    private val filesDir = context.applicationContext.filesDir
    private val file = File(filesDir, "history_records.json")
    private val atomicFile = AtomicFile(file)
    private val mutex = Mutex()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val loaded = CompletableDeferred<Unit>()
    private var writesEnabled = false

    private val _records = MutableStateFlow<List<HistoryRecord>>(emptyList())
    val records: StateFlow<List<HistoryRecord>> = _records.asStateFlow()

    private val _readStatus = MutableStateFlow(HistoryReadStatus.LOADING)
    val readStatus: StateFlow<HistoryReadStatus> = _readStatus.asStateFlow()

    init {
        scope.launch {
            try {
                loadRecords()
            } finally {
                // All mutations must await the initial read, even if it failed.
                loaded.complete(Unit)
            }
        }
    }

    private fun recoveryFiles(): List<File> =
        filesDir.listFiles()?.filter { it.name.startsWith("history_records_recovery_") }
            ?: emptyList()

    /** Atomic, verified private snapshot before *any* replacement of damaged history. */
    private fun saveOriginal(original: ByteArray): Boolean {
        for (index in 0 until 16) {
            val target = File(
                filesDir,
                "history_records_recovery_" + System.currentTimeMillis() + "_" + index + ".json"
            )
            if (listOf(
                    target,
                    File(target.path + ".bak"),
                    File(target.path + ".new")
                ).any { it.exists() }
            ) continue
            val snapshot = AtomicFile(target)
            val output = try {
                snapshot.startWrite()
            } catch (_: Exception) {
                return false
            }
            try {
                output.write(original)
                snapshot.finishWrite(output)
                return target.exists() && target.readBytes().contentEquals(original)
            } catch (_: Exception) {
                snapshot.failWrite(output)
                return false
            }
        }
        return false
    }

    private suspend fun loadRecords() {
        mutex.withLock {
            var original: ByteArray? = null
            try {
                original = atomicFile.openRead().use { it.readBytes() }
                val jsonArray = JSONArray(String(original, Charsets.UTF_8))
                val recovered = mutableListOf<HistoryRecord>()
                val seenIds = mutableSetOf<String>()
                var discarded = 0
                for (index in 0 until jsonArray.length()) {
                    try {
                        val item = jsonArray.getJSONObject(index)
                        // Never synthesize an ID, content, or timestamp for damaged records.
                        val id = item.opt("id") as? String
                            ?: throw IllegalArgumentException("invalid id")
                        val content = item.opt("content") as? String
                            ?: throw IllegalArgumentException("invalid content")
                        val time = (item.opt("timestamp") as? Number)?.toLong()
                            ?: throw IllegalArgumentException("invalid timestamp")
                        if (id.isBlank() || time <= 0L || seenIds.contains(id)) {
                            throw IllegalArgumentException("invalid or duplicate record")
                        }
                        val type = QrType.valueOf(item.getString("qrType"))
                        recovered.add(
                            HistoryRecord(
                                id = id,
                                content = content,
                                displayTitle = (item.opt("displayTitle") as? String) ?: content,
                                qrType = type,
                                barcodeFormat =
                                    (item.opt("barcodeFormat") as? String) ?: "QR Code",
                                timestamp = time,
                                isFavorite = item.optBoolean("isFavorite", false),
                                isGenerated = item.optBoolean("isGenerated", false)
                            )
                        )
                        seenIds.add(id)
                    } catch (_: Exception) {
                        discarded++
                    }
                }
                _records.value = recovered.sortedByDescending { it.timestamp }
                if (discarded > 0) {
                    writesEnabled = saveOriginal(original)
                    _readStatus.value = if (writesEnabled) {
                        HistoryReadStatus.RECOVERY_SAVED
                    } else {
                        HistoryReadStatus.BLOCKED
                    }
                } else {
                    writesEnabled = true
                    _readStatus.value = if (recoveryFiles().any { it.name.endsWith(".json") }) {
                        HistoryReadStatus.RECOVERY_SAVED
                    } else {
                        HistoryReadStatus.READY
                    }
                }
            } catch (_: FileNotFoundException) {
                // Missing is safe only if neither base nor an atomic sidecar exists.
                // A permission error may also throw FileNotFoundException.
                val existingHistory = listOf(
                    file, File(file.path + ".bak"), File(file.path + ".new")
                ).any { it.exists() }
                writesEnabled = !existingHistory
                _readStatus.value = when {
                    existingHistory -> HistoryReadStatus.BLOCKED
                    recoveryFiles().any { it.name.endsWith(".json") } ->
                        HistoryReadStatus.RECOVERY_SAVED
                    else -> HistoryReadStatus.READY
                }
            } catch (_: Exception) {
                // Do not downgrade a corrupt/unreadable file to normal empty history.
                writesEnabled = original != null && saveOriginal(original)
                _readStatus.value = if (writesEnabled) {
                    HistoryReadStatus.RECOVERY_SAVED
                } else {
                    HistoryReadStatus.BLOCKED
                }
            }
        }
    }

    /** Only commit the UI state after the durable write succeeds. */
    private fun persist(next: List<HistoryRecord>): Boolean {
        if (!writesEnabled) return false
        val array = JSONArray()
        for (record in next) {
            array.put(JSONObject().apply {
                put("id", record.id)
                put("content", record.content)
                put("displayTitle", record.displayTitle)
                put("qrType", record.qrType.name)
                put("barcodeFormat", record.barcodeFormat)
                put("timestamp", record.timestamp)
                put("isFavorite", record.isFavorite)
                put("isGenerated", record.isGenerated)
            })
        }
        val output = try {
            atomicFile.startWrite()
        } catch (_: Exception) {
            return false
        }
        return try {
            output.write(array.toString().toByteArray(Charsets.UTF_8))
            atomicFile.finishWrite(output)
            true
        } catch (_: Exception) {
            atomicFile.failWrite(output)
            false
        }
    }

    private fun commit(next: List<HistoryRecord>) {
        if (persist(next)) {
            _records.value = next
        } else {
            writesEnabled = false
            _readStatus.value = HistoryReadStatus.BLOCKED
        }
    }

    private fun mutate(change: (List<HistoryRecord>) -> List<HistoryRecord>) {
        scope.launch {
            loaded.await()
            mutex.withLock {
                if (writesEnabled) commit(change(_records.value))
            }
        }
    }

    fun addRecord(record: HistoryRecord) = mutate { current ->
        val updated = current.toMutableList()
        val existingIndex = updated.indexOfFirst {
            it.content == record.content && it.isGenerated == record.isGenerated
        }
        if (existingIndex >= 0) {
            val existing = updated.removeAt(existingIndex)
            updated.add(0, existing.copy(timestamp = System.currentTimeMillis()))
        } else {
            updated.add(0, record)
        }
        updated
    }

    fun toggleFavorite(id: String) = mutate { current ->
        current.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
    }

    fun toggleFavoriteForResult(record: HistoryRecord) = mutate { current ->
        val updated = current.toMutableList()
        val index = updated.indexOfFirst {
            it.content == record.content && it.isGenerated == record.isGenerated
        }
        if (index >= 0) {
            updated[index] = updated[index].copy(isFavorite = !updated[index].isFavorite)
        } else {
            updated.add(0, record.copy(isFavorite = true))
        }
        updated
    }

    fun deleteRecord(id: String) = mutate { current ->
        current.filterNot { it.id == id }
    }

    /** A confirmed user deletion removes both history and every private recovery copy. */
    fun clearAll() {
        scope.launch {
            loaded.await()
            mutex.withLock {
                try {
                    atomicFile.delete()
                    val historyGone = listOf(
                        file,
                        File(file.path + ".bak"),
                        File(file.path + ".new")
                    ).none { it.exists() }
                    val recoveryGone = recoveryFiles().all { !it.exists() || it.delete() }
                    if (historyGone && recoveryGone && recoveryFiles().isEmpty()) {
                        _records.value = emptyList()
                        writesEnabled = true
                        _readStatus.value = HistoryReadStatus.READY
                    } else {
                        writesEnabled = false
                        _readStatus.value = HistoryReadStatus.BLOCKED
                    }
                } catch (_: Exception) {
                    writesEnabled = false
                    _readStatus.value = HistoryReadStatus.BLOCKED
                }
            }
        }
    }
}
