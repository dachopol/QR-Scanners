package com.anakinyoo.qrscanners.util

import com.anakinyoo.qrscanners.model.ContactData
import com.anakinyoo.qrscanners.model.EmailData
import com.anakinyoo.qrscanners.model.GeoData
import com.anakinyoo.qrscanners.model.SmsData
import com.anakinyoo.qrscanners.model.WifiData
import java.net.URLEncoder

object QrPayloadBuilder {

    fun buildText(text: String): String {
        return text.trim()
    }

    fun buildUrl(url: String): String {
        val trimmed = url.trim()
        return if (!trimmed.startsWith("http://", ignoreCase = true) &&
            !trimmed.startsWith("https://", ignoreCase = true)
        ) {
            "https://$trimmed"
        } else {
            trimmed
        }
    }

    fun buildWifi(wifi: WifiData): String {
        val type = when (wifi.securityType.uppercase()) {
            "WEP" -> "WEP"
            "NOPASS", "OPEN", "NONE" -> "nopass"
            else -> "WPA"
        }
        val escapedSsid = escapeWifiString(wifi.ssid)
        val escapedPassword = escapeWifiString(wifi.password)
        val hiddenStr = if (wifi.isHidden) "H:true;" else ""
        return "WIFI:T:$type;S:$escapedSsid;P:$escapedPassword;$hiddenStr;"
    }

    private fun escapeWifiString(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace(":", "\\:")
            .replace("\"", "\\\"")
    }

    fun buildContact(contact: ContactData): String {
        val sb = StringBuilder()
        sb.append("BEGIN:VCARD\n")
        sb.append("VERSION:3.0\n")
        if (contact.name.isNotBlank()) {
            sb.append("FN:${contact.name.trim()}\n")
            sb.append("N:;${contact.name.trim()};;;\n")
        }
        if (contact.phone.isNotBlank()) {
            sb.append("TEL;TYPE=CELL:${contact.phone.trim()}\n")
        }
        if (contact.email.isNotBlank()) {
            sb.append("EMAIL:${contact.email.trim()}\n")
        }
        if (contact.organization.isNotBlank()) {
            sb.append("ORG:${contact.organization.trim()}\n")
        }
        if (contact.title.isNotBlank()) {
            sb.append("TITLE:${contact.title.trim()}\n")
        }
        sb.append("END:VCARD")
        return sb.toString()
    }

    fun buildEmail(email: EmailData): String {
        val encodedSubject = try {
            URLEncoder.encode(email.subject, "UTF-8").replace("+", "%20")
        } catch (_: Exception) {
            email.subject
        }
        val encodedBody = try {
            URLEncoder.encode(email.body, "UTF-8").replace("+", "%20")
        } catch (_: Exception) {
            email.body
        }
        val params = mutableListOf<String>()
        if (email.subject.isNotBlank()) params.add("subject=$encodedSubject")
        if (email.body.isNotBlank()) params.add("body=$encodedBody")

        val queryString = if (params.isNotEmpty()) "?" + params.joinToString("&") else ""
        return "mailto:${email.address.trim()}$queryString"
    }

    fun buildPhone(phone: String): String {
        val cleaned = phone.trim().replace(" ", "").replace("-", "")
        return "tel:$cleaned"
    }

    fun buildSms(sms: SmsData): String {
        val cleanedNumber = sms.number.trim().replace(" ", "").replace("-", "")
        return if (sms.message.isNotBlank()) {
            "smsto:$cleanedNumber:${sms.message}"
        } else {
            "smsto:$cleanedNumber"
        }
    }

    fun buildGeo(geo: GeoData): String {
        if (!geo.latitude.isFinite() || !geo.longitude.isFinite()) return ""
        if (geo.latitude !in -90.0..90.0 || geo.longitude !in -180.0..180.0) return ""
        return "geo:${geo.latitude},${geo.longitude}"
    }

    fun buildGeo(latitude: String, longitude: String): String {
        val lat = latitude.trim().toDoubleOrNull() ?: return ""
        val lng = longitude.trim().toDoubleOrNull() ?: return ""
        return buildGeo(GeoData(latitude = lat, longitude = lng))
    }
}
