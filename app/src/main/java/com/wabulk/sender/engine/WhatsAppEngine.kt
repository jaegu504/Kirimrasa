package com.wabulk.sender.engine

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
