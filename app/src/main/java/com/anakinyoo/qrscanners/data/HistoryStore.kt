package com.anakinyoo.qrscanners.data

import android.content.Context
import com.anakinyoo.qrscanners.model.HistoryRecord
import com.anakinyoo.qrscanners.model.QrType
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

class HistoryStore(private val context: Context) {

    private val file = File(context.filesDir, "history_records.json")
    private val mutex = Mutex()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _records = MutableStateFlow<List<HistoryRecord>>(emptyList())
    val records: StateFlow<List<HistoryRecord>> = _records.asStateFlow()

    init {
        scope.launch {
            loadRecords()
        }
    }

    private suspend fun loadRecords() {
        mutex.withLock {
            if (!file.exists()) {
                _records.value = emptyList()
                return
            }
            try {
                val jsonStr = file.readText()
                val jsonArray = JSONArray(jsonStr)
                val list = mutableListOf<HistoryRecord>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val rec = HistoryRecord(
                        id = obj.getString("id"),
                        content = obj.getString("content"),
                        displayTitle = obj.optString("displayTitle", obj.getString("content")),
                        qrType = try {
                            QrType.valueOf(obj.getString("qrType"))
                        } catch (_: Exception) {
                            QrType.TEXT
                        },
                        barcodeFormat = obj.optString("barcodeFormat", "QR Code"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        isFavorite = obj.optBoolean("isFavorite", false),
                        isGenerated = obj.optBoolean("isGenerated", false)
                    )
                    list.add(rec)
                }
                _records.value = list.sortedByDescending { it.timestamp }
            } catch (e: Exception) {
                e.printStackTrace()
                _records.value = emptyList()
            }
        }
    }

    private suspend fun persist() {
        try {
            val jsonArray = JSONArray()
            for (rec in _records.value) {
                val obj = JSONObject().apply {
                    put("id", rec.id)
                    put("content", rec.content)
                    put("displayTitle", rec.displayTitle)
                    put("qrType", rec.qrType.name)
                    put("barcodeFormat", rec.barcodeFormat)
                    put("timestamp", rec.timestamp)
                    put("isFavorite", rec.isFavorite)
                    put("isGenerated", rec.isGenerated)
                }
                jsonArray.put(obj)
            }
            file.writeText(jsonArray.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addRecord(record: HistoryRecord) {
        scope.launch {
            mutex.withLock {
                val updated = _records.value.toMutableList()
                // Avoid exact duplicate at the top
                val existingIndex = updated.indexOfFirst { it.content == record.content && it.isGenerated == record.isGenerated }
                if (existingIndex >= 0) {
                    val existing = updated.removeAt(existingIndex)
                    updated.add(0, existing.copy(timestamp = System.currentTimeMillis()))
                } else {
                    updated.add(0, record)
                }
                _records.value = updated
                persist()
            }
        }
    }

    fun toggleFavorite(id: String) {
        scope.launch {
            mutex.withLock {
                val updated = _records.value.map {
                    if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
                }
                _records.value = updated
                persist()
            }
        }
    }

    fun deleteRecord(id: String) {
        scope.launch {
            mutex.withLock {
                val updated = _records.value.filterNot { it.id == id }
                _records.value = updated
                persist()
            }
        }
    }

    fun clearAll() {
        scope.launch {
            mutex.withLock {
                _records.value = emptyList()
                persist()
            }
        }
    }
}
