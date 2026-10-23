package com.wabulk.sender.engine

import com.wabulk.sender.data.locale.LocalizationManager
import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.engine.model.NodeSession
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Serializable
data class SendResult(
    val isSuccess: Boolean,
    val messageId: String? = null,
    val errorMessage: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class PingResult(
    val isSuccess: Boolean,
    val latencyMs: Long = 0L,
    val message: String = ""
)

interface WhatsAppEngine {
    val node: NodeSession
    suspend fun initialize() {}
    suspend fun requestPairingCode(phoneNumber: String): String
    suspend fun sendMessage(
        recipientPhone: String,
        messageText: String,
        mediaUrls: List<String> = emptyList()
    ): SendResult
    suspend fun sendTypingPresence(recipientPhone: String, durationMs: Long = 3000L)
    suspend fun ping(): PingResult
    suspend fun disconnect()

    // Backwards compatibility methods
    suspend fun sendTextMessage(
        nodeType: AccountNode,
        recipientPhone: String,
        message: String
    ): Boolean = sendMessage(recipientPhone, message).isSuccess

    suspend fun simulateTyping(
        nodeType: AccountNode,
        recipientPhone: String,
        durationMs: Long
    ) = sendTypingPresence(recipientPhone, durationMs)

    suspend fun ping(nodeType: AccountNode): Boolean = ping().isSuccess
}

class SimulatedWhatsAppEngine(
    override val node: NodeSession = NodeSession(
        id = "simulated-node",
        nodeType = AccountNode.PERSONAL
    )
) : WhatsAppEngine {

    override suspend fun initialize() {}

    override suspend fun requestPairingCode(phoneNumber: String): String {
        delay(300)
        return WhatsAppSessionManager.generatePairingCode()
    }

    override suspend fun sendMessage(
        recipientPhone: String,
        messageText: String,
        mediaUrls: List<String>
    ): SendResult {
        // Simulates protocol transmission delay (300-600ms)
        delay(400)
        return SendResult(
            isSuccess = true,
            messageId = "sim-${System.currentTimeMillis()}",
            errorMessage = null
        )
    }

    override suspend fun sendTypingPresence(recipientPhone: String, durationMs: Long) {
        delay(durationMs)
    }

    override suspend fun ping(): PingResult {
        delay(150)
        return PingResult(
            isSuccess = true,
            latencyMs = 150,
            message = "Simulated Pong"
        )
    }

    override suspend fun disconnect() {
        delay(50)
    }

    override suspend fun sendTextMessage(
        nodeType: AccountNode,
        recipientPhone: String,
        message: String
    ): Boolean {
        delay(400)
        return true
    }

    override suspend fun simulateTyping(
        nodeType: AccountNode,
        recipientPhone: String,
        durationMs: Long
    ) {
        delay(durationMs)
    }

    override suspend fun ping(nodeType: AccountNode): Boolean {
        delay(300)
        return true
    }
}

class OnDeviceWhatsAppEngine(
    private val context: android.content.Context,
    override val node: NodeSession
) : WhatsAppEngine {

    override suspend fun initialize() {}

    override suspend fun requestPairingCode(phoneNumber: String): String {
        return "ON-DEVICE-READY"
    }

    override suspend fun sendMessage(
        recipientPhone: String,
        messageText: String,
        mediaUrls: List<String>
    ): SendResult = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
        try {
            val rawDigits = recipientPhone.replace(Regex("[^0-9]"), "")
            val cleanPhone = when {
                rawDigits.startsWith("0") -> "62" + rawDigits.substring(1)
                rawDigits.startsWith("8") -> "62$rawDigits"
                else -> rawDigits
            }
            val isBusiness = node.nodeType == AccountNode.BUSINESS
            val targetPkg = if (isBusiness) "com.whatsapp.w4b" else "com.whatsapp"

            val firstMedia = mediaUrls.firstOrNull()
            val intent = if (!firstMedia.isNullOrBlank() && !firstMedia.startsWith("dummy://")) {
                val mediaUri = android.net.Uri.parse(firstMedia)
                val mime = try { context.contentResolver.getType(mediaUri) ?: "*/*" } catch (_: Exception) { "*/*" }
                android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = mime
                    putExtra(android.content.Intent.EXTRA_STREAM, mediaUri)
                    if (messageText.isNotBlank()) {
                        putExtra(android.content.Intent.EXTRA_TEXT, messageText)
                    }
                    putExtra("jid", "$cleanPhone@s.whatsapp.net")
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                            android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
            } else {
                val encodedText = android.net.Uri.encode(messageText)
                val uri = android.net.Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedText")
                android.content.Intent(android.content.Intent.ACTION_VIEW, uri).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            }

            if (isPackageInstalled(context, targetPkg)) {
                intent.setPackage(targetPkg)
            } else if (isPackageInstalled(context, "com.whatsapp")) {
                intent.setPackage("com.whatsapp")
            } else if (isPackageInstalled(context, "com.whatsapp.w4b")) {
                intent.setPackage("com.whatsapp.w4b")
            }

            context.startActivity(intent)
            delay(1500L)
            SendResult(
                isSuccess = true,
                messageId = "wa-intent-${System.currentTimeMillis()}"
            )
        } catch (e: Exception) {
            SendResult(
                isSuccess = false,
                errorMessage = LocalizationManager.activeStrings.waOpenFailed(e.message.orEmpty())
            )
        }
    }

    override suspend fun sendTypingPresence(recipientPhone: String, durationMs: Long) {
        delay(durationMs)
    }

    override suspend fun ping(): PingResult {
        val waInstalled = isPackageInstalled(context, "com.whatsapp")
        val wbInstalled = isPackageInstalled(context, "com.whatsapp.w4b")
        val isInstalled = if (node.nodeType == AccountNode.BUSINESS) wbInstalled else waInstalled

        return if (isInstalled) {
            PingResult(
                isSuccess = true,
                latencyMs = 5L,
                message = LocalizationManager.activeStrings.waInstalled
            )
        } else {
            PingResult(
                isSuccess = false,
                latencyMs = 0L,
                message = LocalizationManager.activeStrings.waNotInstalled
            )
        }
    }

    override suspend fun disconnect() {}

    companion object {
        fun isPackageInstalled(context: android.content.Context, packageName: String): Boolean {
            return try {
                context.packageManager.getPackageInfo(packageName, 0)
                true
            } catch (_: Exception) {
                false
            }
        }
    }
}

