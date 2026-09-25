package com.anakinyoo.qrscanners.camera

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.anakinyoo.qrscanners.model.ScanResultData
import com.anakinyoo.qrscanners.util.ScanActionResolver
import com.anakinyoo.qrscanners.util.ScanFormat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeScannerEngine(
    private val onBarcodeDetected: (ScanResultData) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    private var lastScannedValue: String? = null
    private var lastScannedTimestamp: Long = 0L
    private val debounceDelayMs = 1500L

    var isPaused: Boolean = false

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (isPaused) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        if (!rawValue.isNullOrBlank()) {
                            val now = System.currentTimeMillis()
                            if (rawValue != lastScannedValue || now - lastScannedTimestamp > debounceDelayMs) {
                                lastScannedValue = rawValue
                                lastScannedTimestamp = now

                                val formatType = ScanFormat.fromMlKitFormat(barcode.format)
                                val qrType = ScanActionResolver.resolveType(rawValue)
                                val title = buildDisplayTitle(rawValue, qrType)

                                val result = ScanResultData(
                                    rawValue = rawValue,
                                    format = formatType.displayName,
                                    type = qrType,
                                    title = title,
                                    subtitle = rawValue,
                                    timestamp = now
                                )
                                onBarcodeDetected(result)
                                break
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    // ignore scan failures
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    fun scanBitmap(
        bitmap: Bitmap,
        onSuccess: (ScanResultData) -> Unit,
        onNotFound: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val barcode = barcodes.firstOrNull()
                    if (barcode != null && !barcode.rawValue.isNullOrBlank()) {
                        val rawValue = barcode.rawValue!!
                        val formatType = ScanFormat.fromMlKitFormat(barcode.format)
                        val qrType = ScanActionResolver.resolveType(rawValue)
                        val title = buildDisplayTitle(rawValue, qrType)

                        val result = ScanResultData(
                            rawValue = rawValue,
                            format = formatType.displayName,
                            type = qrType,
                            title = title,
                            subtitle = rawValue,
                            timestamp = System.currentTimeMillis()
                        )
                        onSuccess(result)
                    } else {
                        onNotFound()
                    }
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun scanUri(
        context: Context,
        uri: Uri,
        onSuccess: (ScanResultData) -> Unit,
        onNotFound: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(context, uri)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val barcode = barcodes.firstOrNull()
                    if (barcode != null && !barcode.rawValue.isNullOrBlank()) {
                        val rawValue = barcode.rawValue!!
                        val formatType = ScanFormat.fromMlKitFormat(barcode.format)
                        val qrType = ScanActionResolver.resolveType(rawValue)
                        val title = buildDisplayTitle(rawValue, qrType)

                        val result = ScanResultData(
                            rawValue = rawValue,
                            format = formatType.displayName,
                            type = qrType,
                            title = title,
                            subtitle = rawValue,
                            timestamp = System.currentTimeMillis()
                        )
                        onSuccess(result)
                    } else {
                        onNotFound()
                    }
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        } catch (e: Exception) {
            onError(e)
        }
    }
    private fun buildDisplayTitle(
        rawValue: String,
        qrType: com.anakinyoo.qrscanners.model.QrType
    ): String {
        return when (qrType) {
            com.anakinyoo.qrscanners.model.QrType.URL -> rawValue
            com.anakinyoo.qrscanners.model.QrType.WIFI ->
                ScanActionResolver.parseWifi(rawValue).ssid.ifBlank { rawValue.take(50) }
            com.anakinyoo.qrscanners.model.QrType.CONTACT -> {
                val contact = ScanActionResolver.parseContact(rawValue)
                contact.name.ifBlank {
                    contact.phone.ifBlank {
                        contact.email.ifBlank { rawValue.take(50) }
                    }
                }
            }
            com.anakinyoo.qrscanners.model.QrType.PHONE ->
                ScanActionResolver.parsePhone(rawValue)
            com.anakinyoo.qrscanners.model.QrType.EMAIL ->
                ScanActionResolver.parseEmail(rawValue).address.ifBlank { rawValue.take(50) }
            com.anakinyoo.qrscanners.model.QrType.SMS ->
                ScanActionResolver.parseSms(rawValue).number.ifBlank { rawValue.take(50) }
            com.anakinyoo.qrscanners.model.QrType.GEO ->
                ScanActionResolver.parseGeoOrNull(rawValue)
                    ?.let { "${it.latitude}, ${it.longitude}" }
                    ?: rawValue.take(50)
            com.anakinyoo.qrscanners.model.QrType.TEXT -> rawValue.take(50)
        }
    }

}
