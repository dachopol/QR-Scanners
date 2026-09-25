package com.anakinyoo.qrscanners.model

import java.util.UUID

enum class QrType {
    TEXT,
    URL,
    WIFI,
    CONTACT,
    EMAIL,
    PHONE,
    SMS,
    GEO
}

enum class BarcodeType(val displayName: String) {
    QR_CODE("QR Code"),
    DATA_MATRIX("Data Matrix"),
    AZTEC("Aztec"),
    PDF_417("PDF 417"),
    EAN_13("EAN-13"),
    EAN_8("EAN-8"),
    UPC_A("UPC-A"),
    UPC_E("UPC-E"),
    CODE_128("Code 128"),
    CODE_39("Code 39"),
    CODE_93("Code 93"),
    CODABAR("Codabar"),
    ITF("ITF"),
    UNKNOWN("Barcode")
}

data class HistoryRecord(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val displayTitle: String,
    val qrType: QrType,
    val barcodeFormat: String = BarcodeType.QR_CODE.displayName,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val isGenerated: Boolean = false
)

data class WifiData(
    val ssid: String,
    val password: String = "",
    val securityType: String = "WPA", // WPA, WEP, nopass
    val isHidden: Boolean = false
)

data class ContactData(
    val name: String,
    val phone: String = "",
    val email: String = "",
    val organization: String = "",
    val title: String = ""
)

data class EmailData(
    val address: String,
    val subject: String = "",
    val body: String = ""
)

data class SmsData(
    val number: String,
    val message: String = ""
)

data class GeoData(
    val latitude: Double,
    val longitude: Double
)

data class ScanResultData(
    val rawValue: String,
    val format: String,
    val type: QrType,
    val title: String,
    val subtitle: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val isGenerated: Boolean = false
)
