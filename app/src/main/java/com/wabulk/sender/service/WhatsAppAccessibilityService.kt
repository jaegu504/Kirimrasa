package com.wabulk.sender.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withTimeoutOrNull

class WhatsAppAccessibilityService : AccessibilityService() {

    companion object {
        private val _isServiceConnected = MutableStateFlow(false)
        val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

        private val _messageSentSignal = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
        val messageSentSignal: SharedFlow<Boolean> = _messageSentSignal.asSharedFlow()

        @Volatile
        var isAutomationRunning: Boolean = false

        @Volatile
        private var lastClickTime = 0L

        /**
         * Checks whether WhatsAppAccessibilityService is enabled in Android System Accessibility Settings.
         */
        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            return try {
                val enabledServices = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                ) ?: return false

                val colonSplitter = TextUtils.SimpleStringSplitter(':')
                colonSplitter.setString(enabledServices)
                while (colonSplitter.hasNext()) {
                    val componentName = colonSplitter.next()
                    if (componentName.contains(context.packageName, ignoreCase = true) &&
                        componentName.contains("WhatsAppAccessibilityService", ignoreCase = true)
                    ) {
                        return true
                    }
                }
                false
            } catch (_: Exception) {
                false
            }
        }

        /**
         * Direct shortcut to Android Accessibility Settings screen.
         */
        fun openAccessibilitySettings(context: Context) {
            try {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }

        /**
         * Waits until accessibility service detects and clicks the Send button, or times out.
         */
        suspend fun awaitSendClick(timeoutMs: Long = 10_000L): Boolean {
            return withTimeoutOrNull(timeoutMs) {
                messageSentSignal.collect { sent ->
                    if (sent) return@collect
                }
                true
            } ?: false
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        _isServiceConnected.value = true
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
            packageNames = arrayOf("com.whatsapp", "com.whatsapp.w4b")
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || !isAutomationRunning) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg != "com.whatsapp" && pkg != "com.whatsapp.w4b") return

        // Prevent double clicks within 2 seconds
        val now = System.currentTimeMillis()
        if (now - lastClickTime < 2000L) return

        val rootNode = rootInActiveWindow ?: return
        try {
            // Find send button by view ID or text/contentDescription
            var sendButton = rootNode.findAccessibilityNodeInfosByViewId("$pkg:id/send")
                .firstOrNull { it.isClickable && it.isEnabled }

            if (sendButton == null) {
                // Multilingual fallback: Send, Kirim, Gönder
                val descriptions = listOf("Send", "Kirim", "Gönder", "send", "kirim")
                for (desc in descriptions) {
                    val matching = rootNode.findAccessibilityNodeInfosByText(desc)
                    sendButton = matching.firstOrNull { it.isClickable && it.isEnabled }
                    if (sendButton != null) break
                }
            }

            if (sendButton != null) {
                lastClickTime = now
                // Brief delay so WhatsApp finishes parsing intent text
                Thread.sleep(300)
                sendButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                _messageSentSignal.tryEmit(true)

                // Wait for send animation then return back to WhatsAppBulk
                Thread.sleep(500)
                performGlobalAction(GLOBAL_ACTION_BACK)
            }
        } catch (_: Exception) {
        } finally {
            try {
                rootNode.recycle()
            } catch (_: Exception) {}
        }
    }

    override fun onInterrupt() {
        _isServiceConnected.value = false
    }

    override fun onDestroy() {
        super.onDestroy()
        _isServiceConnected.value = false
    }
}
