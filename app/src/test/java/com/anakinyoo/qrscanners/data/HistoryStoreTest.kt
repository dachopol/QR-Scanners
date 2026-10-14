package com.anakinyoo.qrscanners.data

import com.anakinyoo.qrscanners.model.HistoryRecord
import com.anakinyoo.qrscanners.model.QrType
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HistoryStoreTest {

    private val context get() = RuntimeEnvironment.getApplication()
    private val base get() = File(context.filesDir, "history_records.json")
    private fun recoveryFiles() =
        context.filesDir.listFiles()?.filter {
            it.name.startsWith("history_records_recovery_")
        } ?: emptyList()

    @Before
    fun resetHistory() {
        listOf(base, File(base.path + ".bak"), File(base.path + ".new"))
            .plus(recoveryFiles()).forEach { it.delete() }
    }

    private suspend fun waitStatus(store: HistoryStore, status: HistoryReadStatus) {
        withTimeout(3_000) {
            while (store.readStatus.value != status) delay(20)
        }
    }

    @Test
    fun persistedHistoryCanBeReloaded() = runBlocking {
        val payload = "QRSCANNERS_ATOMIC_HISTORY_TEST"
        val store = HistoryStore(context)
        waitStatus(store, HistoryReadStatus.READY)
        store.addRecord(HistoryRecord(content = payload, displayTitle = payload, qrType = QrType.TEXT))

        withTimeout(3_000) {
            while (!base.exists() || !base.readText().contains(payload)) delay(20)
        }

        val reopened = HistoryStore(context)
        val loaded = withTimeout(3_000) {
            while (reopened.records.value.none { it.content == payload }) delay(20)
            reopened.records.value.first { it.content == payload }
        }
        assertEquals(QrType.TEXT, loaded.qrType)
        assertEquals(payload, loaded.content)
        assertEquals(HistoryReadStatus.READY, reopened.readStatus.value)
    }

    @Test
    fun malformedJsonIsPreservedBeforeNewWritesAndClearedOnExplicitRequest() = runBlocking {
        val malformed = "{invalid history payload"
        base.writeText(malformed)
        val store = HistoryStore(context)
        waitStatus(store, HistoryReadStatus.RECOVERY_SAVED)

        assertEquals(malformed, base.readText())
        assertEquals(1, recoveryFiles().size)
        assertEquals(malformed, recoveryFiles().single().readText())

        val payload = "RECOVERY_TEST_RECORD"
        store.addRecord(HistoryRecord(content = payload, displayTitle = payload, qrType = QrType.TEXT))
        withTimeout(3_000) {
            while (!base.readText().contains(payload)) delay(20)
        }
        assertEquals(malformed, recoveryFiles().single().readText())

        store.clearAll()
        waitStatus(store, HistoryReadStatus.READY)
        assertTrue(store.records.value.isEmpty())
        assertFalse(base.exists())
        assertTrue(recoveryFiles().isEmpty())
    }

    @Test
    fun damagedEntryPreservesOriginalAndSalvagesOtherValidRecords() = runBlocking {
        val good = JSONObject()
            .put("id", "valid-1")
            .put("content", "SAVED_VALID_CONTENT")
            .put("displayTitle", "Valid")
            .put("qrType", "TEXT")
            .put("timestamp", 1_724_000_000_000L)
            .put("isFavorite", false)
            .put("isGenerated", false)
        val original = JSONArray().put(good).put(JSONObject().put("broken", true)).toString()
        base.writeText(original)
        val store = HistoryStore(context)
        waitStatus(store, HistoryReadStatus.RECOVERY_SAVED)

        assertEquals(1, store.records.value.size)
        assertEquals("SAVED_VALID_CONTENT", store.records.value.single().content)
        assertEquals(original, recoveryFiles().single().readText())
        assertEquals(original, base.readText())

        store.addRecord(
            HistoryRecord(
                content = "NEW_RECORD_AFTER_RECOVERY",
                displayTitle = "New",
                qrType = QrType.TEXT
            )
        )
        withTimeout(3_000) {
            while (!base.readText().contains("NEW_RECORD_AFTER_RECOVERY")) delay(20)
        }
        val persisted = JSONArray(base.readText())
        assertEquals(2, persisted.length())
        assertEquals(original, recoveryFiles().single().readText())
    }

    @Test
    fun mutationsWaitForInitialReadBeforeReplacingStoredHistory() = runBlocking {
        val known = JSONObject()
            .put("id", "existing-record")
            .put("content", "EXISTING_USER_HISTORY")
            .put("displayTitle", "Existing")
            .put("qrType", "TEXT")
            .put("timestamp", 1_724_000_000_000L)
        base.writeText(JSONArray().put(known).toString())
        val store = HistoryStore(context)
        store.addRecord(
            HistoryRecord(content = "AFTER_STARTUP", displayTitle = "After", qrType = QrType.TEXT)
        )
        withTimeout(3_000) {
            while (!base.readText().contains("AFTER_STARTUP")) delay(20)
        }
        assertEquals(2, JSONArray(base.readText()).length())
        assertEquals(HistoryReadStatus.READY, store.readStatus.value)
    }
}
