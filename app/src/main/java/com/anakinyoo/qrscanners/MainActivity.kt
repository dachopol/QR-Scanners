package com.anakinyoo.qrscanners

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.anakinyoo.qrscanners.data.AppPreferences
import com.anakinyoo.qrscanners.data.HistoryStore
import com.anakinyoo.qrscanners.ui.MainApp
import com.anakinyoo.qrscanners.ui.theme.QrScannersTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var preferences: AppPreferences
    private lateinit var historyStore: HistoryStore

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("qr_scanners_prefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("app_language", "system") ?: "system"
        val context = updateBaseContextLocale(newBase, lang)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferences = AppPreferences(this)
        historyStore = HistoryStore(this)

        setContent {
            val themeMode by preferences.themeMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> systemDark
            }

            var currentLang by remember { mutableStateOf(preferences.appLanguage.value) }

            QrScannersTheme(darkTheme = isDark) {
                MainApp(
                    historyStore = historyStore,
                    preferences = preferences,
                    onLocaleChange = { newLang ->
                        currentLang = newLang
                        recreate()
                    }
                )
            }
        }
    }

    private fun updateBaseContextLocale(context: Context, lang: String): Context {
        if (lang == "system") return context
        val locale = Locale.forLanguageTag(lang)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
