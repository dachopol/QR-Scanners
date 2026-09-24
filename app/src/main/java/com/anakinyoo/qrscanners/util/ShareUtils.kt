package com.anakinyoo.qrscanners.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.anakinyoo.qrscanners.model.HistoryRecord
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ShareUtils {

    fun shareText(context: Context, text: String, title: String = "Share Content") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun shareQrBitmap(context: Context, bitmap: Bitmap, title: String = "Share QR Code") {
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
            val chooser = Intent.createChooser(shareIntent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            shareText(context, "Error generating image to share: ${e.message}")
        }
    }

    fun exportHistoryCsv(context: Context, records: List<HistoryRecord>) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("ID,Type,Format,Title,Content,Timestamp,IsFavorite,IsGenerated\n")
        for (r in records) {
            val escapedTitle = r.displayTitle.replace("\"", "\"\"")
            val escapedContent = r.content.replace("\"", "\"\"")
            val timeStr = dateFormat.format(Date(r.timestamp))
            sb.append("\"${r.id}\",\"${r.qrType}\",\"${r.barcodeFormat}\",\"$escapedTitle\",\"$escapedContent\",\"$timeStr\",${r.isFavorite},${r.isGenerated}\n")
        }
        shareText(context, sb.toString(), "Export History (CSV)")
    }
}
