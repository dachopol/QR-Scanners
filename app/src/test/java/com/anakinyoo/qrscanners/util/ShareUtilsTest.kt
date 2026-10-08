package com.anakinyoo.qrscanners.util

import com.anakinyoo.qrscanners.model.HistoryRecord
import com.anakinyoo.qrscanners.model.QrType
import org.junit.Assert.assertEquals
import org.junit.Test

class ShareUtilsTest {

    @Test
    fun escapeCsvCellForExportBlocksFormulaPrefixes() {
        assertEquals("'=SUM(A1:A2)", ShareUtils.escapeCsvCellForExport("=SUM(A1:A2)"))
        assertEquals("'+CMD", ShareUtils.escapeCsvCellForExport("+CMD"))
        assertEquals("'-1+1", ShareUtils.escapeCsvCellForExport("-1+1"))
        assertEquals("'@A1", ShareUtils.escapeCsvCellForExport("@A1"))
    }

    @Test
    fun historyCsvForExportRedactsWifiCredentials() {
        val csv = ShareUtils.historyCsvForExport(
            listOf(
                HistoryRecord(
                    id = "wifi-1",
                    content = "WIFI:T:WPA;S:Office;P:SuperSecret;;",
                    displayTitle = "Office",
                    qrType = QrType.WIFI,
                    timestamp = 0L
                )
            )
        )

        assertEquals(false, csv.contains("SuperSecret"))
        assertEquals(true, csv.contains("Password=[REDACTED]"))
        assertEquals(true, csv.startsWith("ID,Type,Format,Title,Content,Timestamp,IsFavorite,IsGenerated"))
    }

    @Test
    fun escapeCsvCellForExportEscapesQuotesWithoutChangingNormalText() {
        assertEquals("hello", ShareUtils.escapeCsvCellForExport("hello"))
        assertEquals("a\"\"b", ShareUtils.escapeCsvCellForExport("a\"b"))
    }
}
