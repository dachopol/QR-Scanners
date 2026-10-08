package com.anakinyoo.qrscanners.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.anakinyoo.qrscanners.R
import com.anakinyoo.qrscanners.model.HistoryRecord
import com.anakinyoo.qrscanners.model.QrType
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ShareUtils {

    fun shareText(context: Context, text: String, title: String? = null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(
            intent,
            title ?: context.getString(R.string.share_content_title)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun shareQrBitmap(context: Context, bitmap: Bitmap, title: String? = null) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "qr_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(
                shareIntent,
                title ?: context.getString(R.string.share_qr_title)
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (_: Exception) {
            Toast.makeText(
                context,
                context.getString(R.string.share_qr_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    internal fun historyContentForExport(record: HistoryRecord): String {
        if (record.qrType != QrType.WIFI) return record.content

        val wifi = ScanActionResolver.parseWifi(record.content)
            ?: return "[Wi-Fi credential redacted]"

        return "SSID=${wifi.ssid};Security=${wifi.securityType};Password=[REDACTED];Hidden=${wifi.isHidden}"
    }

    internal fun escapeCsvCellForExport(value: String): String {
        val safeValue = if (value.firstOrNull() in setOf('=', '+', '-', '@')) {
            "'$value"
        } else {
            value
        }
        return safeValue.replace("\"", "\"\"")
    }

    internal fun historyCsvForExport(records: List<HistoryRecord>): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("ID,Type,Format,Title,Content,Timestamp,IsFavorite,IsGenerated\n")
        for (r in records) {
            val escapedTitle = escapeCsvCellForExport(r.displayTitle)
            val escapedContent = escapeCsvCellForExport(historyContentForExport(r))
            val timeStr = dateFormat.format(Date(r.timestamp))
            sb.append(
                "\"\${r.id}\",\"\${r.qrType}\",\"\${r.barcodeFormat}\",\"$escapedTitle\",\"$escapedContent\",\"$timeStr\",\${r.isFavorite},\${r.isGenerated}\n"
            )
        }
        return sb.toString()
    }

    fun exportHistoryCsv(context: Context, records: List<HistoryRecord>) {
        try {
            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()
            val file = File(exportDir, "qr_scanners_history_\${System.currentTimeMillis()}.csv")
            file.writeText(historyCsvForExport(records), Charsets.UTF_8)

            val contentUri = FileProvider.getUriForFile(
                context,
                "\${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(
                shareIntent,
                context.getString(R.string.export_history_csv_title)
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (_: Exception) {
            Toast.makeText(
                context,
                context.getString(R.string.export_history_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
