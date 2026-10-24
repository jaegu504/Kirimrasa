package com.wabulk.sender.ui.screens.settings

import com.wabulk.sender.engine.queue.AntiBanConfig
import com.wabulk.sender.engine.queue.AntiBanSettings
import com.wabulk.sender.data.locale.LocalizationManager
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.engine.PingResult
import com.wabulk.sender.engine.WhatsAppSessionManager
import com.wabulk.sender.engine.baileys.BaileysBridgeClient
import com.wabulk.sender.engine.baileys.BaileysConfig
import com.wabulk.sender.engine.baileys.BaileysConfigManager
import com.wabulk.sender.engine.model.NodeSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    val sessionManager = WhatsAppSessionManager.getInstance(application)
    val node1Session: StateFlow<NodeSession> = sessionManager.node1Session
    val node2Session: StateFlow<NodeSession> = sessionManager.node2Session

    // Baileys Bridge Gateway configuration
    val baileysConfig: StateFlow<BaileysConfig> = BaileysConfigManager.config

    private val _gatewayTestResult = MutableStateFlow<PingResult?>(null)
    val gatewayTestResult: StateFlow<PingResult?> = _gatewayTestResult.asStateFlow()

    private val _isTestingGateway = MutableStateFlow(false)
    val isTestingGateway: StateFlow<Boolean> = _isTestingGateway.asStateFlow()

    // Anti-ban settings: persisted in AntiBanSettings and read by the dispatch queue
    val antiBanConfig: StateFlow<AntiBanConfig> = AntiBanSettings.config

    fun selectPreset(index: Int) = AntiBanSettings.update { it.copy(preset = index) }

    fun adjustCustomMin(deltaSec: Int) = AntiBanSettings.update {
        val min = it.customMinSec + deltaSec
        // Raising min may push max up so the spread stays valid
        it.copy(preset = AntiBanConfig.PRESET_CUSTOM, customMinSec = min, customMaxSec = maxOf(it.customMaxSec, min + AntiBanConfig.MIN_SPREAD_SEC))
    }

    fun adjustCustomMax(deltaSec: Int) = AntiBanSettings.update {
        it.copy(preset = AntiBanConfig.PRESET_CUSTOM, customMaxSec = it.customMaxSec + deltaSec)
    }

    fun toggleTypingSim(enabled: Boolean) = AntiBanSettings.update { it.copy(typingSimEnabled = enabled) }

    fun incrementPauseDuration() = AntiBanSettings.update { it.copy(pauseDurationMin = it.pauseDurationMin + 1) }

    fun decrementPauseDuration() = AntiBanSettings.update { it.copy(pauseDurationMin = it.pauseDurationMin - 1) }

    fun incrementBatchSize() = AntiBanSettings.update { it.copy(batchSize = it.batchSize + 5) }

    fun decrementBatchSize() = AntiBanSettings.update { it.copy(batchSize = it.batchSize - 5) }

    fun updateGatewayUrl(url: String) {
        BaileysConfigManager.setGatewayUrl(url)
    }

    fun updateApiKey(key: String) {
        BaileysConfigManager.setApiKey(key)
    }

    fun toggleBaileysEnabled(enabled: Boolean) {
        BaileysConfigManager.setEnabled(enabled)
    }

    fun testGatewayConnection() {
        viewModelScope.launch {
            _isTestingGateway.value = true
            _gatewayTestResult.value = null
            try {
                val client = BaileysBridgeClient(
                    node = node1Session.value,
                    configProvider = { BaileysConfigManager.config.value.copy(isEnabled = true) },
                    fallbackOnException = false
                ).apply {
                    context = getApplication()
                }
                val result = client.ping()
                _gatewayTestResult.value = result
            } catch (e: Exception) {
                _gatewayTestResult.value = PingResult(
                    isSuccess = false,
                    latencyMs = 0L,
                    message = e.message ?: LocalizationManager.activeStrings.gatewayConnectFailed
                )
            } finally {
                _isTestingGateway.value = false
            }
        }
    }

    fun requestGatewayPairingCode(node: AccountNode, phoneNumber: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            try {
                val code = sessionManager.requestPairingCodeRemote(node, phoneNumber)
                onResult(Result.success(code))
                // Poll in background to detect when pairing is accepted in WhatsApp
                launch {
                    var attempts = 0
                    while (attempts < 40) { // 40 * 3s = 2 minutes
                        kotlinx.coroutines.delay(3000)
                        attempts++
                        val isConnected = sessionManager.pingNode(node, getApplication())
                        if (isConnected) {
                            sessionManager.connectNode(node, phoneNumber)
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

    fun disconnectNode(node: AccountNode) {
        sessionManager.disconnectNode(node)
    }

    fun updateNodePhone(node: AccountNode, phone: String) {
        sessionManager.updateNodePhone(node, phone, getApplication())
    }

    suspend fun pingNode(node: AccountNode): Boolean {
        return sessionManager.pingNode(node, getApplication())
    }

    fun copyCodeAndOpenWhatsApp(code: String, node: AccountNode) {
        val context = getApplication<Application>()
        // 1. Copy to clipboard
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("WhatsApp Pairing Code", code)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, LocalizationManager.activeStrings.codeCopied(code), Toast.LENGTH_SHORT).show()

        // 2. Open WhatsApp intent
        try {
            // Open the app that belongs to this account first, then the other one
            val packages = if (node == AccountNode.PERSONAL) listOf("com.whatsapp", "com.whatsapp.w4b") else listOf("com.whatsapp.w4b", "com.whatsapp")
            val launchIntent = packages.firstNotNullOfOrNull { context.packageManager.getLaunchIntentForPackage(it) }
                ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://web.whatsapp.com"))
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        } catch (_: Exception) {
            Toast.makeText(context, LocalizationManager.activeStrings.waNotFound, Toast.LENGTH_SHORT).show()
        }
    }
}
