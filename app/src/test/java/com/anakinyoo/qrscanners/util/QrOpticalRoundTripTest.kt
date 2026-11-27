package com.anakinyoo.qrscanners.util

import android.graphics.Color
import com.anakinyoo.qrscanners.model.ContactData
import com.anakinyoo.qrscanners.model.EmailData
import com.anakinyoo.qrscanners.model.GeoData
import com.anakinyoo.qrscanners.model.SmsData
import com.anakinyoo.qrscanners.model.WifiData
import com.google.zxing.BinaryBitmap
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class QrOpticalRoundTripTest {
    @Test
    fun allEightPayloadTypesDecodeExactlyInFourPalettes() {
        val payloads = listOf(
            QrPayloadBuilder.buildText("ทดสอบ QR / English"),
            QrPayloadBuilder.buildUrl("example.org/qr?a=1&b=2"),
            QrPayloadBuilder.buildWifi(WifiData("Lab;Guest", "fixture-only", "WPA", true)),
            QrPayloadBuilder.buildContact(ContactData("Test User", organization = "R&D")),
            QrPayloadBuilder.buildEmail(EmailData("qa@example.org", "QR", "ทดสอบ")),
            QrPayloadBuilder.buildPhone("+66812345678"),
            QrPayloadBuilder.buildSms(SmsData("+66812345678", "Hello: QR")),
            QrPayloadBuilder.buildGeo(GeoData(0.0, 1e-9))
        )
        val colors = listOf(Color.BLACK, Color.rgb(2, 132, 199),
            Color.rgb(124, 58, 237), Color.rgb(5, 150, 105))
        for (payload in payloads) {
            for (color in colors) {
                val bitmap = QrCodeGenerator.generateQrBitmap(payload, 512, color, Color.WHITE)
                assertNotNull(bitmap)
                requireNotNull(bitmap)
                try {
                    val pixels = IntArray(bitmap.width * bitmap.height)
                    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                    val binary = BinaryBitmap(HybridBinarizer(
                        RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
                    ))
                    val decoded = QRCodeReader().decode(binary).text
                    assertEquals("QR bitmap did not preserve payload", payload, decoded)
                } finally {
                    bitmap.recycle()
                }
            }
        }
    }

    @Test
    fun blankPayloadCannotGenerateBitmap() {
        assertNull(QrCodeGenerator.generateQrBitmap("   "))
    }
}
