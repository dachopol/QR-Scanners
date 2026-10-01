package com.anakinyoo.qrscanners.data

import com.anakinyoo.qrscanners.model.HistoryRecord
import com.anakinyoo.qrscanners.model.QrType
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File

@RunWith(RobolectricTestRunner::class)
class HistoryStoreTest {

    @Test
    fun persistedHistoryCanBeReloaded() = runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val base = File(context.filesDir, "history_records.json")
        listOf(base, File(base.path + ".bak"), File(base.path + ".new")).forEach { it.delete() }

        val payload = "QRSCANNERS_ATOMIC_HISTORY_TEST"
        val store = HistoryStore(context)
        store.addRecord(
            HistoryRecord(
                content = payload,
                displayTitle = payload,
                qrType = QrType.TEXT
            )
        )

        withTimeout(3_000) {
            while (!base.exists() || !base.readText().contains(payload)) {
                delay(20)
            }
        }

        val reopened = HistoryStore(context)
        val loaded = withTimeout(3_000) {
            while (reopened.records.value.none { it.content == payload }) {
                delay(20)
            }
            reopened.records.value.first { it.content == payload }
        }

        assertEquals(QrType.TEXT, loaded.qrType)
        assertEquals(payload, loaded.content)
    }
}
