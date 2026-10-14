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
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction

/** Internal fault seam for deterministic no-device I/O regression tests only. */
internal enum class HistoryIoStage {
    BEFORE_READ, BEFORE_BACKUP_START, BEFORE_BACKUP_WRITE,
    BEFORE_PERSIST_START, BEFORE_PERSIST_WRITE, BEFORE_DELETE
}

internal fun interface HistoryFaultInjector {
    fun before(stage: HistoryIoStage)
}

/** History stays fail-closed when unreadable content cannot be safely preserved. */
enum class HistoryReadStatus { LOADING, READY, RECOVERY_SAVED, BLOCKED }

class HistoryStore internal constructor(
    context: Context,
    private val faultInjector: HistoryFaultInjector
) {
    constructor(context: Context) : this(context, HistoryFaultInjector { _ -> })

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
        // Crash/restart replay of the same corrupt source must not create
        // unbounded, privacy-sensitive backup duplicates.
        val copies = filesDir.listFiles() ?: return false
        for (copy in copies) {
            if (!copy.name.startsWith("history_records_recovery_") ||
                !copy.name.endsWith(".json")) continue
            val matches = try {
                copy.readBytes().contentEquals(original)
            } catch (_: Exception) {
                false
            }
            if (matches) return true
        }
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
                faultInjector.before(HistoryIoStage.BEFORE_BACKUP_START)
                snapshot.startWrite()
            } catch (_: Exception) {
                return false
            }
            try {
                faultInjector.before(HistoryIoStage.BEFORE_BACKUP_WRITE)
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
                faultInjector.before(HistoryIoStage.BEFORE_READ)
                original = atomicFile.openRead().use { it.readBytes() }
                // Android's permissive String(bytes, UTF_8) replaces malformed
                // sequences with U+FFFD. Rewriting that JSON would silently
                // destroy original scan content. Preserve bad bytes instead.
                val strictUtf8 = Charsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(original))
                    .toString()
                val jsonArray = JSONArray(strictUtf8)
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
                        // toLong() on Double/BigDecimal truncates fractions or
                        // overflows silently. Only exact integer JSON numbers
                        // are safe to replay into the canonical history store.
                        val timestampNumber = item.opt("timestamp") as? Number
                            ?: throw IllegalArgumentException("invalid timestamp type")
                        val time = timestampNumber.toString().toLongOrNull()
                            ?: throw IllegalArgumentException("lossy timestamp")
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
                // File.exists() may return false on permission errors. Treat
                // missing as safe only with a readable directory listing.
                val listing = filesDir.listFiles()
                val names = setOf(
                    file.name, file.name + ".bak", file.name + ".new"
                )
                val verifiedAbsent = listing != null &&
                    listing.none { it.name in names }
                writesEnabled = verifiedAbsent
                _readStatus.value = when {
                    !verifiedAbsent -> HistoryReadStatus.BLOCKED
                    listing!!.any {
                        it.name.startsWith("history_records_recovery_") &&
                            it.name.endsWith(".json")
                    } -> HistoryReadStatus.RECOVERY_SAVED
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
            faultInjector.before(HistoryIoStage.BEFORE_PERSIST_START)
            atomicFile.startWrite()
        } catch (_: Exception) {
            return false
        }
        return try {
            faultInjector.before(HistoryIoStage.BEFORE_PERSIST_WRITE)
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

    fun deleteRecord(id: String) {
        scope.launch {
            loaded.await()
            mutex.withLock {
                if (!writesEnabled) return@withLock
                // A recovery snapshot can retain previously deleted entries.
                // Never pretend individual erasure succeeded while it exists.
                if (_readStatus.value != HistoryReadStatus.READY) return@withLock
                val listing = filesDir.listFiles()
                if (listing == null || listing.any {
                        it.name.startsWith("history_records_recovery_")
                    }
                ) {
                    writesEnabled = false
                    _readStatus.value = HistoryReadStatus.BLOCKED
                    return@withLock
                }
                commit(_records.value.filterNot { it.id == id })
            }
        }
    }

    /** A confirmed user deletion removes both history and every private recovery copy. */
    fun clearAll() {
        scope.launch {
            loaded.await()
            mutex.withLock {
                try {
                    // A null listing must never be treated as proof of no
                    // remaining private recovery copy or primary-sidecar.
                    val existing = filesDir.listFiles()
                        ?: throw IllegalStateException("history directory inaccessible")
                    faultInjector.before(HistoryIoStage.BEFORE_DELETE)
                    atomicFile.delete()
                    for (copy in existing) {
                        if (!copy.name.startsWith("history_records_recovery_")) continue
                        if (copy.exists() && !copy.delete()) {
                            throw IllegalStateException("recovery copy deletion failed")
                        }
                    }
                    val remaining = filesDir.listFiles()
                        ?: throw IllegalStateException("history directory inaccessible")
                    val baseFiles = setOf(
                        file.name, file.name + ".bak", file.name + ".new"
                    )
                    val verifiedGone = remaining.none {
                        it.name in baseFiles ||
                            it.name.startsWith("history_records_recovery_")
                    }
                    if (!verifiedGone) {
                        throw IllegalStateException("history deletion not verified")
                    }
                    _records.value = emptyList()
                    writesEnabled = true
                    _readStatus.value = HistoryReadStatus.READY
                } catch (_: Exception) {
                    writesEnabled = false
                    _readStatus.value = HistoryReadStatus.BLOCKED
                }
            }
        }
    }
}
