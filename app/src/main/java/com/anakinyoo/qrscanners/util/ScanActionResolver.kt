package com.anakinyoo.qrscanners.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.widget.Toast
import com.anakinyoo.qrscanners.R
import com.anakinyoo.qrscanners.model.ContactData
import com.anakinyoo.qrscanners.model.EmailData
import com.anakinyoo.qrscanners.model.GeoData
import com.anakinyoo.qrscanners.model.QrType
import com.anakinyoo.qrscanners.model.SmsData
import com.anakinyoo.qrscanners.model.WifiData
import java.net.URLDecoder
import java.util.regex.Pattern

object ScanActionResolver {

    private val URL_PATTERN = Pattern.compile(
        "^(https?|ftp)://[a-zA-Z0-9+&@#/%?=~_|!:,.;]*[a-zA-Z0-9+&@#/%=~_|]",
        Pattern.CASE_INSENSITIVE
    )

    fun resolveType(raw: String): QrType {
        val trimmed = raw.trim()
        val lower = trimmed.lowercase()

        return when {
            lower.startsWith("wifi:") -> QrType.WIFI
            lower.startsWith("begin:vcard") || lower.startsWith("mecard:") -> QrType.CONTACT
            lower.startsWith("mailto:") -> QrType.EMAIL
            lower.startsWith("tel:") -> QrType.PHONE
            lower.startsWith("smsto:") || lower.startsWith("sms:") || lower.startsWith("mms:") -> QrType.SMS
            parseGeoOrNull(trimmed) != null -> QrType.GEO
            URL_PATTERN.matcher(trimmed).matches() ||
                (trimmed.contains(".") && !trimmed.contains(" ") && (lower.startsWith("www.") || lower.contains(".com") || lower.contains(".org") || lower.contains(".net") || lower.contains(".io") || lower.contains(".th"))) -> QrType.URL
            else -> QrType.TEXT
        }
    }

    fun parseWifi(raw: String): WifiData {
        // WIFI:T:WPA;S:MySSID;P:MyPass;H:false;;
        var ssid = ""
        var password = ""
        var type = "WPA"
        var hidden = false

        val content = if (raw.startsWith("WIFI:", ignoreCase = true)) {
            raw.substring(5)
        } else {
            raw
        }

        for (part in splitEscapedWifiFields(content)) {
            when {
                part.startsWith("S:", ignoreCase = true) -> ssid = part.substring(2)
                part.startsWith("P:", ignoreCase = true) -> password = part.substring(2)
                part.startsWith("T:", ignoreCase = true) -> type = part.substring(2)
                part.startsWith("H:", ignoreCase = true) ->
                    hidden = part.substring(2).equals("true", ignoreCase = true)
            }
        }
        return WifiData(ssid = ssid, password = password, securityType = type, isHidden = hidden)
    }

    private fun splitEscapedWifiFields(content: String): List<String> {
        val fields = mutableListOf<String>()
        val current = StringBuilder()
        var escaped = false

        for (ch in content) {
            when {
                escaped -> {
                    current.append(ch)
                    escaped = false
                }
                ch == '\\' -> escaped = true
                ch == ';' -> {
                    fields.add(current.toString())
                    current.setLength(0)
                }
                else -> current.append(ch)
            }
        }

        if (escaped) current.append('\\')
        if (current.isNotEmpty()) fields.add(current.toString())
        return fields
    }

    fun parseContact(raw: String): ContactData {
        var name = ""
        var phone = ""
        var email = ""
        var org = ""
        var title = ""

        if (raw.contains("BEGIN:VCARD", ignoreCase = true)) {
            val lines = raw.lines()
            for (line in lines) {
                val trimmed = line.trim()
                val upper = trimmed.uppercase()
                when {
                    upper.startsWith("FN:") -> name = trimmed.substring(3).trim()
                    upper.startsWith("N:") && name.isBlank() -> {
                        val parts = trimmed.substring(2).split(";")
                        name = parts.filter { it.isNotBlank() }.reversed().joinToString(" ")
                    }
                    upper.startsWith("TEL") -> {
                        val num = trimmed.substringAfter(":")
                        if (phone.isBlank()) phone = num.trim()
                    }
                    upper.startsWith("EMAIL") -> {
                        val mail = trimmed.substringAfter(":")
                        if (email.isBlank()) email = mail.trim()
                    }
                    upper.startsWith("ORG:") -> org = trimmed.substring(4).trim()
                    upper.startsWith("TITLE:") -> title = trimmed.substring(6).trim()
                }
            }
        } else if (raw.startsWith("mecard:", ignoreCase = true)) {
            val mecardContent = raw.substring(7)
            val parts = mecardContent.split(";")
            for (part in parts) {
                val p = part.trim()
                if (p.startsWith("N:", ignoreCase = true)) name = p.substring(2)
                if (p.startsWith("TEL:", ignoreCase = true)) phone = p.substring(4)
                if (p.startsWith("EMAIL:", ignoreCase = true)) email = p.substring(6)
                if (p.startsWith("ORG:", ignoreCase = true)) org = p.substring(4)
            }
        }
        return ContactData(name = name, phone = phone, email = email, organization = org, title = title)
    }

    fun parseEmail(raw: String): EmailData {
        // mailto:abc@xyz.com?subject=Hello&body=World
        val afterScheme = if (raw.startsWith("mailto:", ignoreCase = true)) raw.substring(7) else raw
        val address = afterScheme.substringBefore("?")
        var subject = ""
        var body = ""
        if (afterScheme.contains("?")) {
            val query = afterScheme.substringAfter("?")
            for (param in query.split("&")) {
                val key = param.substringBefore("=")
                val value = param.substringAfter("=", "")
                val decoded = try {
                    URLDecoder.decode(value, "UTF-8")
                } catch (_: Exception) {
                    value
                }
                if (key.equals("subject", ignoreCase = true)) subject = decoded
                if (key.equals("body", ignoreCase = true)) body = decoded
            }
        }
        return EmailData(address = address.trim(), subject = subject, body = body)
    }

    fun parsePhone(raw: String): String {
        return if (raw.startsWith("tel:", ignoreCase = true)) {
            raw.substring(4).trim()
        } else {
            raw.trim()
        }
    }

    fun parseSms(raw: String): SmsData {
        // smsto:123456:message or sms:123456?body=message
        if (raw.startsWith("smsto:", ignoreCase = true)) {
            val withoutPrefix = raw.substring(6)
            val number = withoutPrefix.substringBefore(":")
            val message = if (withoutPrefix.contains(":")) withoutPrefix.substringAfter(":") else ""
            return SmsData(number = number, message = message)
        } else if (raw.startsWith("sms:", ignoreCase = true)) {
            val uri = Uri.parse(raw)
            val number = uri.schemeSpecificPart?.substringBefore("?") ?: ""
            val message = uri.getQueryParameter("body") ?: ""
            return SmsData(number = number, message = message)
        }
        return SmsData(number = raw)
    }

    fun parseGeoOrNull(raw: String): GeoData? {
        val trimmed = raw.trim()
        if (trimmed.startsWith("geo:", ignoreCase = true)) {
            val uriBody = trimmed.substring(4)
            val base = parseCoordinatePair(uriBody.substringBefore("?").substringBefore(";"))
            val query = uriBody.substringAfter("?", "")
            val queryCoordinates = query.split("&")
                .asSequence()
                .mapNotNull { parameter ->
                    val value = parameter.substringAfter("=", "")
                    decodeUrlComponent(value)
                }
                .mapNotNull(::parseCoordinatePair)
                .firstOrNull()

            // geo:0,0 is a placeholder used when the actual coordinates are in q=.
            return if (base?.let { it.latitude == 0.0 && it.longitude == 0.0 } == true) {
                queryCoordinates
            } else {
                base ?: queryCoordinates
            }
        }

        parseCoordinatePair(trimmed)?.let { return it }
        return parseGoogleMapsGeoOrNull(trimmed)
    }

    private fun parseCoordinatePair(value: String): GeoData? {
        val match = Regex("""^\s*([-+]?\d+(?:\.\d+)?)\s*,\s*([-+]?\d+(?:\.\d+)?)(?:\s*\([^)]*\))?\s*$""")
            .matchEntire(value.trim())
            ?: return null
        val lat = match.groupValues[1].toDoubleOrNull() ?: return null
        val lng = match.groupValues[2].toDoubleOrNull() ?: return null
        if (!lat.isFinite() || !lng.isFinite()) return null
        if (lat !in -90.0..90.0 || lng !in -180.0..180.0) return null
        return GeoData(latitude = lat, longitude = lng)
    }

    private fun parseGoogleMapsGeoOrNull(raw: String): GeoData? {
        val uri = try {
            java.net.URI(raw)
        } catch (_: Exception) {
            return null
        }
        if (!isGoogleMapsHost(uri.host) || !uri.path.orEmpty().startsWith("/maps", ignoreCase = true) &&
            !uri.host.equals("maps.google.com", ignoreCase = true)
        ) return null

        val queryParameters = uri.rawQuery.orEmpty().split("&").map { parameter ->
            decodeUrlComponent(parameter.substringAfter("=", ""))
        }
        for (value in queryParameters) {
            parseCoordinatePair(value)?.let { return it }
        }

        val pathAndFragment = listOfNotNull(uri.rawPath, uri.rawFragment).joinToString("/")
        val atCoordinates = Regex("""@([-+]?\d+(?:\.\d+)?),([-+]?\d+(?:\.\d+)?)""")
            .find(pathAndFragment)
        if (atCoordinates != null) {
            parseCoordinatePair("${atCoordinates.groupValues[1]},${atCoordinates.groupValues[2]}")?.let { return it }
        }
        val placeCoordinates = Regex("""!3d([-+]?\d+(?:\.\d+)?)!4d([-+]?\d+(?:\.\d+)?)""")
            .find(pathAndFragment)
        if (placeCoordinates != null) {
            parseCoordinatePair("${placeCoordinates.groupValues[1]},${placeCoordinates.groupValues[2]}")?.let { return it }
        }
        return null
    }

    fun isGoogleMapsLink(raw: String): Boolean {
        val uri = try {
            java.net.URI(raw.trim())
        } catch (_: Exception) {
            return false
        }
        val host = uri.host?.lowercase() ?: return false
        return isGoogleMapsHost(host) &&
            (uri.path.orEmpty().startsWith("/maps", ignoreCase = true) ||
                host == "maps.google.com" || host == "maps.app.goo.gl" ||
                (host == "goo.gl" && uri.path.orEmpty().startsWith("/maps")))
    }

    private fun isGoogleMapsHost(host: String?): Boolean {
        val normalized = host?.lowercase() ?: return false
        return normalized == "maps.google.com" ||
            normalized == "google.com" ||
            normalized == "www.google.com" ||
            normalized == "maps.app.goo.gl" ||
            normalized == "goo.gl"
    }

    private fun decodeUrlComponent(value: String): String = try {
        URLDecoder.decode(value, "UTF-8")
    } catch (_: Exception) {
        value
    }

    fun parseGeo(raw: String): GeoData {
        return parseGeoOrNull(raw)
            ?: throw IllegalArgumentException("Invalid map coordinates")
    }

    fun copyToClipboard(context: Context, text: String, showToast: Boolean = true) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Scanned QR", text)
        clipboard.setPrimaryClip(clip)
        if (showToast) {
            Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
        }
    }

    fun openBrowser(context: Context, url: String) {
        try {
            val formattedUrl = if (!url.startsWith("http://", ignoreCase = true) && !url.startsWith("https://", ignoreCase = true)) {
                "https://$url"
            } else {
                url
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.error_open_browser, e.localizedMessage ?: ""), Toast.LENGTH_SHORT).show()
        }
    }

    fun dialPhone(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.error_make_call, e.localizedMessage ?: ""), Toast.LENGTH_SHORT).show()
        }
    }

    fun sendSms(context: Context, number: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number")).apply {
                putExtra("sms_body", message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.error_send_sms, e.localizedMessage ?: ""), Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail(context: Context, emailData: EmailData) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${emailData.address}")).apply {
                if (emailData.subject.isNotBlank()) putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
                if (emailData.body.isNotBlank()) putExtra(Intent.EXTRA_TEXT, emailData.body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.error_send_email, e.localizedMessage ?: ""), Toast.LENGTH_SHORT).show()
        }
    }

    fun addContact(context: Context, contact: ContactData) {
        try {
            val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI).apply {
                if (contact.name.isNotBlank()) putExtra(ContactsContract.Intents.Insert.NAME, contact.name)
                if (contact.phone.isNotBlank()) putExtra(ContactsContract.Intents.Insert.PHONE, contact.phone)
                if (contact.email.isNotBlank()) putExtra(ContactsContract.Intents.Insert.EMAIL, contact.email)
                if (contact.organization.isNotBlank()) putExtra(ContactsContract.Intents.Insert.COMPANY, contact.organization)
                if (contact.title.isNotBlank()) putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contact.title)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.error_add_contact, e.localizedMessage ?: ""), Toast.LENGTH_SHORT).show()
        }
    }

    fun openMapLink(context: Context, url: String) {
        if (!isGoogleMapsLink(url)) {
            openBrowser(context, url)
            return
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            openBrowser(context, url)
        }
    }

    fun openMap(context: Context, geo: GeoData) {
        val lat = geo.latitude
        val lng = geo.longitude
        if (!lat.isFinite() || !lng.isFinite() || lat !in -90.0..90.0 || lng !in -180.0..180.0) {
            Toast.makeText(context, context.getString(R.string.invalid_map_coordinates), Toast.LENGTH_SHORT).show()
            return
        }

        val query = "$lat,$lng"
        val geoUri = Uri.parse("geo:$lat,$lng?q=${Uri.encode(query)}")
        val webUri = Uri.parse(
            "https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}"
        )

        val intents = listOf(
            Intent(Intent.ACTION_VIEW, geoUri).apply {
                setPackage("com.google.android.apps.maps")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            Intent(Intent.ACTION_VIEW, geoUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )

        for (intent in intents) {
            try {
                context.startActivity(intent)
                return
            } catch (_: Exception) {
                // Try the next compatible map/browser handler.
            }
        }

        Toast.makeText(context, context.getString(R.string.no_map_or_browser), Toast.LENGTH_SHORT).show()
    }

    fun searchWeb(context: Context, query: String) {
        try {
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(android.app.SearchManager.QUERY, query)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openBrowser(context, "https://www.google.com/search?q=${Uri.encode(query)}")
        }
    }

    private fun openWifiSettingsFallback(context: Context, wifi: WifiData, messageRes: Int) {
        if (wifi.password.isNotBlank()) {
            copyToClipboard(context, wifi.password, showToast = false)
        }
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        val message = if (messageRes == R.string.wifi_open_settings) {
            context.getString(messageRes, wifi.ssid)
        } else {
            context.getString(messageRes)
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun connectWifi(context: Context, wifi: WifiData) {
        if (wifi.ssid.isBlank()) {
            Toast.makeText(context, context.getString(R.string.wifi_missing_ssid), Toast.LENGTH_SHORT).show()
            return
        }

        val security = wifi.securityType.trim().uppercase()
        if (security == "WEP") {
            openWifiSettingsFallback(context, wifi, R.string.wifi_wep_settings)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val specifierBuilder = WifiNetworkSpecifier.Builder()
                    .setSsid(wifi.ssid)

                if (wifi.password.isNotBlank()) {
                    when (security) {
                        "WPA3", "SAE" -> specifierBuilder.setWpa3Passphrase(wifi.password)
                        "NOPASS", "OPEN", "NONE" -> Unit
                        else -> specifierBuilder.setWpa2Passphrase(wifi.password)
                    }
                }

                if (wifi.isHidden) {
                    specifierBuilder.setIsHiddenSsid(true)
                }

                val networkRequest = NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .setNetworkSpecifier(specifierBuilder.build())
                    .build()

                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                connectivityManager.requestNetwork(
                    networkRequest,
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onUnavailable() {
                            super.onUnavailable()
                            Toast.makeText(
                                context,
                                context.getString(R.string.wifi_request_unavailable),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
                Toast.makeText(
                    context,
                    context.getString(R.string.wifi_requesting, wifi.ssid),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (_: Exception) {
                val messageRes = if (wifi.password.isBlank()) {
                    R.string.wifi_open_settings
                } else {
                    R.string.wifi_password_copied_settings
                }
                openWifiSettingsFallback(context, wifi, messageRes)
            }
        } else {
            val messageRes = if (wifi.password.isBlank()) {
                R.string.wifi_open_settings
            } else {
                R.string.wifi_password_copied_settings
            }
            openWifiSettingsFallback(context, wifi, messageRes)
        }
    }
}
