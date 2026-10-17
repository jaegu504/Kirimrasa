package com.wabulk.sender.engine

import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.engine.model.NodeSession
import kotlinx.coroutines.delay

interface WhatsAppEngine {
    suspend fun sendTextMessage(nodeType: AccountNode, recipientPhone: String, message: String): Boolean
    suspend fun simulateTyping(nodeType: AccountNode, recipientPhone: String, durationMs: Long)
    suspend fun ping(nodeType: AccountNode): Boolean
}

class SimulatedWhatsAppEngine : WhatsAppEngine {
    override suspend fun sendTextMessage(
        nodeType: AccountNode,
        recipientPhone: String,
        message: String
    ): Boolean {
        // Simulates protocol transmission delay (300-600ms)
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
