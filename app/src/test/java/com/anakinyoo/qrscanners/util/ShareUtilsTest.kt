package com.anakinyoo.qrscanners.util

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
    fun escapeCsvCellForExportEscapesQuotesWithoutChangingNormalText() {
        assertEquals("hello", ShareUtils.escapeCsvCellForExport("hello"))
        assertEquals("a\"\"b", ShareUtils.escapeCsvCellForExport("a\"b"))
    }
}
