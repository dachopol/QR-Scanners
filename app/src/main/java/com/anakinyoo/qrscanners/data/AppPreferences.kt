package com.anakinyoo.qrscanners.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("qr_scanners_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(prefs.getString(KEY_THEME_MODE, "system") ?: "system")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _appLanguage = MutableStateFlow(prefs.getString(KEY_LANGUAGE, "system") ?: "system")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _vibrateOnScan = MutableStateFlow(prefs.getBoolean(KEY_VIBRATE, true))
    val vibrateOnScan: StateFlow<Boolean> = _vibrateOnScan.asStateFlow()

    private val _soundOnScan = MutableStateFlow(prefs.getBoolean(KEY_SOUND, true))
    val soundOnScan: StateFlow<Boolean> = _soundOnScan.asStateFlow()

    private val _autoCopy = MutableStateFlow(prefs.getBoolean(KEY_AUTO_COPY, false))
    val autoCopy: StateFlow<Boolean> = _autoCopy.asStateFlow()

    private val _autoOpenUrl = MutableStateFlow(prefs.getBoolean(KEY_AUTO_OPEN_URL, false))
    val autoOpenUrl: StateFlow<Boolean> = _autoOpenUrl.asStateFlow()

    private val _defaultCamera = MutableStateFlow(prefs.getString(KEY_DEFAULT_CAMERA, "back") ?: "back")
    val defaultCamera: StateFlow<String> = _defaultCamera.asStateFlow()

    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
        _themeMode.value = mode
    }

    fun setAppLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _appLanguage.value = lang
    }

    fun setVibrateOnScan(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATE, enabled).apply()
        _vibrateOnScan.value = enabled
    }

    fun setSoundOnScan(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        _soundOnScan.value = enabled
    }

    fun setAutoCopy(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_COPY, enabled).apply()
        _autoCopy.value = enabled
    }

    fun setAutoOpenUrl(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_OPEN_URL, enabled).apply()
        _autoOpenUrl.value = enabled
    }

    fun setDefaultCamera(cam: String) {
        prefs.edit().putString(KEY_DEFAULT_CAMERA, cam).apply()
        _defaultCamera.value = cam
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_VIBRATE = "vibrate_on_scan"
        private const val KEY_SOUND = "sound_on_scan"
        private const val KEY_AUTO_COPY = "auto_copy"
        private const val KEY_AUTO_OPEN_URL = "auto_open_url"
        private const val KEY_DEFAULT_CAMERA = "default_camera"
    }
}
