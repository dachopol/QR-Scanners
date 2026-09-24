package com.anakinyoo.qrscanners.util

import com.anakinyoo.qrscanners.model.ContactData
import com.anakinyoo.qrscanners.model.EmailData
import com.anakinyoo.qrscanners.model.GeoData
import com.anakinyoo.qrscanners.model.SmsData
import com.anakinyoo.qrscanners.model.WifiData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrPayloadBuilderTest {

    @Test
    fun testBuildUrlAddsHttps() {
        val payload = QrPayloadBuilder.buildUrl("example.com/scan")
        assertEquals("https://example.com/scan", payload)

        val preserved = QrPayloadBuilder.buildUrl("http://internal.site")
        assertEquals("http://internal.site", preserved)
    }

    @Test
    fun testBuildWifi() {
        val wifi = WifiData(ssid = "Office-Guest", password = "pass;word:123", securityType = "WPA", isHidden = true)
        val payload = QrPayloadBuilder.buildWifi(wifi)
        assertTrue(payload.startsWith("WIFI:T:WPA;"))
        assertTrue(payload.contains("S:Office-Guest;"))
        assertTrue(payload.contains("H:true;"))
    }

    @Test
    fun testBuildContact() {
        val contact = ContactData(
            name = "Alice Wonder",
            phone = "+1987654321",
            email = "alice@wonder.land",
            organization = "Wonderland LLC"
        )
        val payload = QrPayloadBuilder.buildContact(contact)
        assertTrue(payload.startsWith("BEGIN:VCARD"))
        assertTrue(payload.contains("FN:Alice Wonder"))
        assertTrue(payload.contains("TEL;TYPE=CELL:+1987654321"))
        assertTrue(payload.contains("EMAIL:alice@wonder.land"))
        assertTrue(payload.contains("ORG:Wonderland LLC"))
        assertTrue(payload.endsWith("END:VCARD"))
    }

    @Test
    fun testBuildEmail() {
        val email = EmailData(address = "test@domain.com", subject = "Hi there", body = "Nice QR!")
        val payload = QrPayloadBuilder.buildEmail(email)
        assertTrue(payload.startsWith("mailto:test@domain.com?"))
        assertTrue(payload.contains("subject=Hi%20there") || payload.contains("subject=Hi+there"))
    }

    @Test
    fun testBuildPhone() {
        val payload = QrPayloadBuilder.buildPhone("+1 (555) 123-4567")
        assertEquals("tel:+1(555)1234567", payload)
    }

    @Test
    fun testBuildSms() {
        val sms = SmsData(number = "+15551234", message = "Hello")
        val payload = QrPayloadBuilder.buildSms(sms)
        assertEquals("smsto:+15551234:Hello", payload)
    }

    @Test
    fun testBuildGeo() {
        val geo = GeoData(latitude = 13.7563, longitude = 100.5018)
        val payload = QrPayloadBuilder.buildGeo(geo)
        assertEquals("geo:13.7563,100.5018", payload)
    }
}
