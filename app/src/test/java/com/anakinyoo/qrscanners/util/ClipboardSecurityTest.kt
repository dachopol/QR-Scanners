package com.anakinyoo.qrscanners.util

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ClipboardSecurityTest {

    @Test
    fun wifiPasswordClipboardIsMarkedSensitive() {
        val context = RuntimeEnvironment.getApplication()
        ScanActionResolver.copyToClipboard(
            context = context,
            text = "test-password",
            showToast = false,
            isSensitive = true
        )

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip ?: error("clipboard is empty")
        val sensitiveKey = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ClipDescription.EXTRA_IS_SENSITIVE
        } else {
            "android.content.extra.IS_SENSITIVE"
        }

        assertTrue(clip.description.extras?.getBoolean(sensitiveKey) == true)
    }
}
