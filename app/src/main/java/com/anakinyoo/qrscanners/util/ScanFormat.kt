package com.anakinyoo.qrscanners.util

import com.anakinyoo.qrscanners.model.BarcodeType
import com.google.mlkit.vision.barcode.common.Barcode

object ScanFormat {
    fun fromMlKitFormat(format: Int): BarcodeType {
        return when (format) {
            Barcode.FORMAT_QR_CODE -> BarcodeType.QR_CODE
            Barcode.FORMAT_DATA_MATRIX -> BarcodeType.DATA_MATRIX
            Barcode.FORMAT_AZTEC -> BarcodeType.AZTEC
            Barcode.FORMAT_PDF417 -> BarcodeType.PDF_417
            Barcode.FORMAT_EAN_13 -> BarcodeType.EAN_13
            Barcode.FORMAT_EAN_8 -> BarcodeType.EAN_8
            Barcode.FORMAT_UPC_A -> BarcodeType.UPC_A
            Barcode.FORMAT_UPC_E -> BarcodeType.UPC_E
            Barcode.FORMAT_CODE_128 -> BarcodeType.CODE_128
            Barcode.FORMAT_CODE_39 -> BarcodeType.CODE_39
            Barcode.FORMAT_CODE_93 -> BarcodeType.CODE_93
            Barcode.FORMAT_CODABAR -> BarcodeType.CODABAR
            Barcode.FORMAT_ITF -> BarcodeType.ITF
            else -> BarcodeType.UNKNOWN
        }
    }

    fun fromFormatName(name: String): BarcodeType {
        return BarcodeType.values().firstOrNull { 
            it.displayName.equals(name, ignoreCase = true) || it.name.equals(name, ignoreCase = true) 
        } ?: BarcodeType.UNKNOWN
    }
}
