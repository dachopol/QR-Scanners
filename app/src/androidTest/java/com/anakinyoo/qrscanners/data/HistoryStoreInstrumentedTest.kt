package com.anakinyoo.qrscanners.data

import android.content.Context
import android.content.res.Configuration
import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.anakinyoo.qrscanners.R
import com.anakinyoo.qrscanners.model.HistoryRecord
import com.anakinyoo.qrscanners.model.QrType
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Locale

/**
 * On-device Android filesystem checks. Only synthetic fixture strings are used;
 * no real user's scan content, screenshots, logs or credentials are collected.
 */
@RunWith(AndroidJUnit4::class)
class HistoryStoreInstrumentedTest {
    private val context: Context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val base: File get() = File(context.filesDir, "history_records.json")

    private fun relatedFiles(): List<File> =
        context.filesDir.listFiles()?.filter {
            it.name == "history_records.json" ||
                it.name == "history_records.json.bak" ||
                it.name == "history_records.json.new" ||
                it.name.startsWith("history_records_recovery_")
        } ?: emptyList()

    @Before
    fun setUp() = removeFixtures()

    @After
    fun tearDown() = removeFixtures()

    private fun removeFixtures() {
        // Only the isolated emulator's synthetic test files. Never run against user data.
        relatedFiles().forEach { check(it.delete() || !it.exists()) { "fixture cleanup failed" } }
    }

    private fun waitUntil(label: String, deadlineMillis: Long = 10_000L, check: () -> Boolean) {
        val limit = SystemClock.uptimeMillis() + deadlineMillis
        while (!check()) {
            if (SystemClock.uptimeMillis() >= limit) {
                throw AssertionError("Timed out in Android runtime test: $label")
            }
            SystemClock.sleep(25L)
        }
    }

    private fun fixture(id: String, content: String): JSONObject =
        JSONObject()
            .put("id", id)
            .put("content", content)
            .put("displayTitle", content)
            .put("qrType", "TEXT")
            .put("timestamp", 1_724_000_000_000L)

    @Test
    fun validFileLoadsOnAndroidAndRemainsAfterReinstantiation() {
        base.writeText(JSONArray().put(fixture("existing-1", "SYNTHETIC_KEEP_ME")).toString())
        val first = HistoryStore(context)
        waitUntil("read valid file") { first.readStatus.value == HistoryReadStatus.READY }
        assertEquals("SYNTHETIC_KEEP_ME", first.records.value.single().content)

        first.addRecord(
            HistoryRecord(
                content = "SYNTHETIC_NEXT",
                displayTitle = "Synthetic next",
                qrType = QrType.TEXT
            )
        )
        waitUntil("persist second record") {
            base.exists() && base.readText().contains("SYNTHETIC_NEXT")
        }
        val reopened = HistoryStore(context)
        waitUntil("reload Android file") {
            reopened.readStatus.value == HistoryReadStatus.READY &&
                reopened.records.value.size == 2
        }
        assertTrue(reopened.records.value.any { it.content == "SYNTHETIC_KEEP_ME" })
    }

    @Test
    fun malformedFileIsBackedUpAndWarningSurvivesReopenUntilClear() {
        val corruptBytes = "{SYNTHETIC_MALFORMED_HISTORY".toByteArray(Charsets.UTF_8)
        base.writeBytes(corruptBytes)
        val store = HistoryStore(context)
        waitUntil("backup status") {
            store.readStatus.value == HistoryReadStatus.RECOVERY_SAVED
        }
        assertTrue(base.readBytes().contentEquals(corruptBytes))
        val copies = relatedFiles().filter { it.name.startsWith("history_records_recovery_") }
        assertEquals(1, copies.size)
        assertTrue(copies.single().readBytes().contentEquals(corruptBytes))

        store.addRecord(
            HistoryRecord(
                content = "SYNTHETIC_REPLACEMENT",
                displayTitle = "Synthetic replacement",
                qrType = QrType.TEXT
            )
        )
        waitUntil("save after private snapshot") {
            base.readText().contains("SYNTHETIC_REPLACEMENT")
        }

        val reopened = HistoryStore(context)
        waitUntil("persisted recovery notice") {
            reopened.readStatus.value == HistoryReadStatus.RECOVERY_SAVED
        }
        assertEquals("SYNTHETIC_REPLACEMENT", reopened.records.value.single().content)
        assertTrue(copies.single().readBytes().contentEquals(corruptBytes))

        reopened.clearAll()
        waitUntil("clear all history and snapshots") {
            reopened.readStatus.value == HistoryReadStatus.READY &&
                relatedFiles().isEmpty()
        }
        assertFalse(base.exists())
    }

    @Test
    fun mixedArrayKeepsOnlyValidRecordsAndPreservesRawOriginal() {
        val raw = JSONArray()
            .put(fixture("valid-record", "SYNTHETIC_VALID"))
            .put(JSONObject().put("broken", true))
            .toString()
        base.writeText(raw)
        val store = HistoryStore(context)
        waitUntil("recover mixed array") {
            store.readStatus.value == HistoryReadStatus.RECOVERY_SAVED
        }
        assertEquals("SYNTHETIC_VALID", store.records.value.single().content)
        assertEquals(raw, base.readText())
        assertEquals(
            raw,
            relatedFiles().single { it.name.startsWith("history_records_recovery_") }.readText()
        )
    }

    @Test
    fun deleteOneRecordCannotPretendRecoverySnapshotWasErased() {
        val original = JSONArray()
            .put(fixture("only-valid-id", "SYNTHETIC_PRIVATE_ITEM"))
            .put(JSONObject().put("invalid", true))
            .toString()
        base.writeText(original)
        val store = HistoryStore(context)
        waitUntil("recovery snapshot created") {
            store.readStatus.value == HistoryReadStatus.RECOVERY_SAVED
        }

        store.deleteRecord("only-valid-id")
        // Deleting in recovery mode must not claim privacy erasure while
        // the raw private snapshot still includes this synthetic record.
        SystemClock.sleep(180)
        assertEquals(original, base.readText())
        assertEquals("SYNTHETIC_PRIVATE_ITEM", store.records.value.single().content)
        assertEquals(
            original,
            relatedFiles().single { it.name.startsWith("history_records_recovery_") }
                .readText()
        )
        store.clearAll()
        waitUntil("user-confirmed erasure") {
            store.readStatus.value == HistoryReadStatus.READY &&
                relatedFiles().isEmpty()
        }
    }

    @Test
    fun historyWarningsResolveForThaiAndEnglishAndroidResources() {
        fun localized(lang: String): String {
            val config = Configuration(context.resources.configuration)
            config.setLocale(Locale.forLanguageTag(lang))
            return context.createConfigurationContext(config)
                .getString(R.string.history_recovery_blocked)
        }
        assertTrue(localized("en").contains("History"))
        assertTrue(localized("th").contains("ประวัติ"))
    }
}
