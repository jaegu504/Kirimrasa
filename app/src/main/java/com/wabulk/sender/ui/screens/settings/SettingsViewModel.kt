package com.wabulk.sender.ui.screens.settings

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.engine.WhatsAppSessionManager
import com.wabulk.sender.engine.model.NodeSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    val sessionManager = WhatsAppSessionManager.getInstance()
    val node1Session: StateFlow<NodeSession> = sessionManager.node1Session
    val node2Session: StateFlow<NodeSession> = sessionManager.node2Session

    // Anti-ban settings state
    private val _selectedPreset = MutableStateFlow(1) // 0: Conservative, 1: Optimal, 2: Turbo
    val selectedPreset: StateFlow<Int> = _selectedPreset.asStateFlow()

    private val _typingSimEnabled = MutableStateFlow(true)
    val typingSimEnabled: StateFlow<Boolean> = _typingSimEnabled.asStateFlow()

    private val _autoRotateEnabled = MutableStateFlow(true)
    val autoRotateEnabled: StateFlow<Boolean> = _autoRotateEnabled.asStateFlow()

    private val _pauseDurationMin = MutableStateFlow(5)
    val pauseDurationMin: StateFlow<Int> = _pauseDurationMin.asStateFlow()

    private val _batchSizeMsgs = MutableStateFlow(25)
    val batchSizeMsgs: StateFlow<Int> = _batchSizeMsgs.asStateFlow()

    private val _selectedAiTone = MutableStateFlow(1) // 0: Casual, 1: Friendly, 2: Formal
    val selectedAiTone: StateFlow<Int> = _selectedAiTone.asStateFlow()

    fun selectPreset(index: Int) {
        _selectedPreset.value = index
    }

    fun toggleTypingSim(enabled: Boolean) {
        _typingSimEnabled.value = enabled
    }

    fun toggleAutoRotate(enabled: Boolean) {
        _autoRotateEnabled.value = enabled
    }

    fun incrementPauseDuration() {
        if (_pauseDurationMin.value < 60) _pauseDurationMin.value += 1
    }

    fun decrementPauseDuration() {
        if (_pauseDurationMin.value > 1) _pauseDurationMin.value -= 1
    }

    fun incrementBatchSize() {
        if (_batchSizeMsgs.value < 100) _batchSizeMsgs.value += 5
    }

    fun decrementBatchSize() {
        if (_batchSizeMsgs.value > 5) _batchSizeMsgs.value -= 5
    }

    fun selectAiTone(toneIndex: Int) {
        _selectedAiTone.value = toneIndex
    }

    fun requestNewPairingCode(node: AccountNode): String {
        return sessionManager.requestNewPairingCode(node)
    }

    fun disconnectNode(node: AccountNode) {
        sessionManager.disconnectNode(node)
    }

    fun connectNode(node: AccountNode, phone: String = "+62 812-3456-7890") {
        sessionManager.connectNode(node, phone)
    }

    suspend fun pingNode(node: AccountNode): Boolean {
        return sessionManager.pingNode(node)
    }

    fun copyCodeAndOpenWhatsApp(code: String) {
        val context = getApplication<Application>()
        // 1. Copy to clipboard
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("WhatsApp Pairing Code", code)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Kode $code disalin ke clipboard!", Toast.LENGTH_SHORT).show()

        // 2. Open WhatsApp intent
        try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                ?: context.packageManager.getLaunchIntentForPackage("com.whatsapp.w4b")
                ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://web.whatsapp.com"))
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        } catch (_: Exception) {
            Toast.makeText(context, "WhatsApp tidak ditemukan di perangkat ini", Toast.LENGTH_SHORT).show()
        }
    }
}
