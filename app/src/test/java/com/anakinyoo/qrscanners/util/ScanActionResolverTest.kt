package com.anakinyoo.qrscanners.util

import com.anakinyoo.qrscanners.model.QrType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScanActionResolverTest {

    @Test
    fun testResolveUrl() {
        assertEquals(QrType.URL, ScanActionResolver.resolveType("https://google.com"))
        assertEquals(QrType.URL, ScanActionResolver.resolveType("http://example.org/test?id=123"))
        assertEquals(QrType.URL, ScanActionResolver.resolveType("www.github.com"))
    }

    @Test
    fun testResolveWifi() {
        val wifiRaw = "WIFI:T:WPA;S:MyHomeWiFi;P:Secret1234;H:false;;"
        assertEquals(QrType.WIFI, ScanActionResolver.resolveType(wifiRaw))

        val parsed = ScanActionResolver.parseWifi(wifiRaw)
        assertEquals("MyHomeWiFi", parsed.ssid)
        assertEquals("Secret1234", parsed.password)
        assertEquals("WPA", parsed.securityType)
        assertFalse(parsed.isHidden)
    }

    @Test
    fun testResolveContactVCard() {
        val vcard = """
            BEGIN:VCARD
            VERSION:3.0
            FN:John Doe
            TEL;TYPE=CELL:+1234567890
            EMAIL:john@example.com
            ORG:Acme Corp
            TITLE:Engineer
            END:VCARD
        """.trimIndent()

        assertEquals(QrType.CONTACT, ScanActionResolver.resolveType(vcard))
        val contact = ScanActionResolver.parseContact(vcard)
        assertEquals("John Doe", contact.name)
        assertEquals("+1234567890", contact.phone)
        assertEquals("john@example.com", contact.email)
        assertEquals("Acme Corp", contact.organization)
        assertEquals("Engineer", contact.title)
    }

    @Test
    fun testResolvePhone() {
        val phoneRaw = "tel:+66812345678"
        assertEquals(QrType.PHONE, ScanActionResolver.resolveType(phoneRaw))
        assertEquals("+66812345678", ScanActionResolver.parsePhone(phoneRaw))
    }

    @Test
    fun testResolveSms() {
        val smsRaw = "smsto:+66812345678:Hello world"
        assertEquals(QrType.SMS, ScanActionResolver.resolveType(smsRaw))
        val parsed = ScanActionResolver.parseSms(smsRaw)
        assertEquals("+66812345678", parsed.number)
        assertEquals("Hello world", parsed.message)
    }

    @Test
    fun testResolveGeo() {
        val geoRaw = "geo:13.7563,100.5018"
        assertEquals(QrType.GEO, ScanActionResolver.resolveType(geoRaw))
        val parsed = ScanActionResolver.parseGeo(geoRaw)
        assertEquals(13.7563, parsed.latitude, 0.0001)
        assertEquals(100.5018, parsed.longitude, 0.0001)

        val plainCoordinates = "13.7563, 100.5018"
        assertEquals(QrType.GEO, ScanActionResolver.resolveType(plainCoordinates))
        val plainParsed = ScanActionResolver.parseGeo(plainCoordinates)
        assertEquals(13.7563, plainParsed.latitude, 0.0001)
        assertEquals(100.5018, plainParsed.longitude, 0.0001)

        val queryCoordinates = ScanActionResolver.parseGeo("geo:0,0?q=13.7563%2C100.5018")
        assertEquals(13.7563, queryCoordinates.latitude, 0.0001)
        assertEquals(100.5018, queryCoordinates.longitude, 0.0001)

        val labeledQuery = ScanActionResolver.parseGeo("geo:0,0?q=13.7563,100.5018(Bangkok)")
        assertEquals(13.7563, labeledQuery.latitude, 0.0001)
        assertEquals(100.5018, labeledQuery.longitude, 0.0001)
        assertEquals(null, ScanActionResolver.parseGeoOrNull("geo:0,0?q=Bangkok"))

        val googleMapsQuery = ScanActionResolver.parseGeo(
            "https://www.google.com/maps/search/?api=1&query=13.7563%2C100.5018"
        )
        assertEquals(13.7563, googleMapsQuery.latitude, 0.0001)
        assertEquals(100.5018, googleMapsQuery.longitude, 0.0001)
        assertEquals(QrType.GEO, ScanActionResolver.resolveType(
            "https://www.google.com/maps/@13.7563,100.5018,15z"
        ))
        assertEquals(true, ScanActionResolver.isGoogleMapsLink("https://maps.app.goo.gl/abc123"))
        assertEquals(false, ScanActionResolver.isGoogleMapsLink("https://example.com/maps?q=13,100"))

        assertEquals(null, ScanActionResolver.parseGeoOrNull("geo:91,100"))
        assertEquals(null, ScanActionResolver.parseGeoOrNull("geo:13,181"))
        assertEquals(null, ScanActionResolver.parseGeoOrNull("geo:not-a-location"))
    }

    @Test
    fun testResolvePlainText() {
        assertEquals(QrType.TEXT, ScanActionResolver.resolveType("Just a random plain text string"))
        assertEquals(QrType.TEXT, ScanActionResolver.resolveType("1234567890123"))
    }
}
