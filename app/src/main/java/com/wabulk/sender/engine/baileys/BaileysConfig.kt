package com.wabulk.sender.engine.baileys

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

@Serializable
data class BaileysConfig(
    val gatewayUrl: String = "http://10.0.2.2:3000",
    val apiKey: String = "",
    val isEnabled: Boolean = false,
    val timeoutSeconds: Long = 15L
)

object BaileysConfigManager {
    private val _config = MutableStateFlow(BaileysConfig())
    val config: StateFlow<BaileysConfig> = _config.asStateFlow()

    fun updateConfig(newConfig: BaileysConfig) {
        _config.value = newConfig
    }

    fun setGatewayUrl(gatewayUrl: String) {
        _config.value = _config.value.copy(gatewayUrl = gatewayUrl.trim())
    }

    fun setApiKey(apiKey: String) {
        _config.value = _config.value.copy(apiKey = apiKey.trim())
    }

    fun setEnabled(isEnabled: Boolean) {
        _config.value = _config.value.copy(isEnabled = isEnabled)
    }

    fun setTimeoutSeconds(timeoutSeconds: Long) {
        _config.value = _config.value.copy(timeoutSeconds = timeoutSeconds)
    }

    fun reset() {
        _config.value = BaileysConfig()
    }
}
