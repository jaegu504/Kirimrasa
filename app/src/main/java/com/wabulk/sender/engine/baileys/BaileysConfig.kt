package com.wabulk.sender.engine.baileys

import android.content.Context
import com.wabulk.sender.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

@Serializable
data class BaileysConfig(
    val gatewayUrl: String = "http://10.0.2.2:3000",
    val apiKey: String = "",
    val isEnabled: Boolean = false,
    val timeoutSeconds: Long = 45L
)

object BaileysConfigManager {
    private const val PREFS_NAME = "baileys_config_prefs"
    private const val KEY_URL = "gateway_url"
    private const val KEY_API_KEY = "api_key"
    private const val KEY_ENABLED = "is_enabled"
    private const val KEY_TIMEOUT = "timeout_seconds"

    const val PRODUCTION_RENDER_URL = "https://kirimrasa-gateway.onrender.com"

    private val _config = MutableStateFlow(BaileysConfig())
    val config: StateFlow<BaileysConfig> = _config.asStateFlow()

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawUrl = prefs.getString(KEY_URL, PRODUCTION_RENDER_URL) ?: PRODUCTION_RENDER_URL
        val url = if (rawUrl == "http://10.0.2.2:3000") PRODUCTION_RENDER_URL else rawUrl
        val apiKey = prefs.getString(KEY_API_KEY, "").orEmpty().ifBlank { BuildConfig.GATEWAY_API_KEY }
        val isEnabled = prefs.getBoolean(KEY_ENABLED, true)
        val timeout = prefs.getLong(KEY_TIMEOUT, 45L)
        _config.value = BaileysConfig(
            gatewayUrl = url,
            apiKey = apiKey,
            isEnabled = isEnabled,
            timeoutSeconds = timeout
        )
    }

    fun updateConfig(newConfig: BaileysConfig) {
        _config.value = newConfig
        saveToPrefs()
    }

    fun setGatewayUrl(gatewayUrl: String) {
        val cleanUrl = gatewayUrl.trim()
        _config.value = _config.value.copy(gatewayUrl = cleanUrl)
        saveToPrefs()
    }

    fun setApiKey(apiKey: String) {
        _config.value = _config.value.copy(apiKey = apiKey.trim())
        saveToPrefs()
    }

    fun setEnabled(isEnabled: Boolean) {
        _config.value = _config.value.copy(isEnabled = isEnabled)
        saveToPrefs()
    }

    fun setTimeoutSeconds(timeoutSeconds: Long) {
        _config.value = _config.value.copy(timeoutSeconds = timeoutSeconds)
        saveToPrefs()
    }

    private fun saveToPrefs() {
        val ctx = appContext ?: return
        val current = _config.value
        try {
            ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_URL, current.gatewayUrl)
                .putString(KEY_API_KEY, current.apiKey)
                .putBoolean(KEY_ENABLED, current.isEnabled)
                .putLong(KEY_TIMEOUT, current.timeoutSeconds)
                .apply()
        } catch (_: Exception) {}
    }

    fun reset() {
        _config.value = BaileysConfig()
        saveToPrefs()
    }
}

