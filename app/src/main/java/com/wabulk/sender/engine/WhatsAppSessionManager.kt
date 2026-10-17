package com.wabulk.sender.engine

import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.engine.baileys.BaileysBridgeClient
import com.wabulk.sender.engine.baileys.BaileysConfigManager
import com.wabulk.sender.engine.model.NodeSession
import com.wabulk.sender.engine.model.NodeStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.SecureRandom

class WhatsAppSessionManager(
    private val engine: WhatsAppEngine = SimulatedWhatsAppEngine()
) {
    private val _node1Session = MutableStateFlow(
        NodeSession(
            id = "node-personal-01",
            nodeType = AccountNode.PERSONAL,
            phoneNumber = "+1 (555) 019-2834",
            status = NodeStatus.CONNECTED,
            uptimeSeconds = 48 * 3600 + 12 * 60,
            healthPercent = 99.4f
        )
    )
    val node1Session: StateFlow<NodeSession> = _node1Session.asStateFlow()

    private val _node2Session = MutableStateFlow(
        NodeSession(
            id = "node-business-02",
            nodeType = AccountNode.BUSINESS,
            phoneNumber = "",
            status = NodeStatus.PAIRING,
            pairingCode = "K97B-42VX",
            codeExpiresAt = System.currentTimeMillis() + 165_000L
        )
    )
    val node2Session: StateFlow<NodeSession> = _node2Session.asStateFlow()

    fun getEngineForNode(nodeType: AccountNode): WhatsAppEngine {
        val session = when (nodeType) {
            AccountNode.PERSONAL -> _node1Session.value
            AccountNode.BUSINESS, AccountNode.DUAL_SYNC -> _node2Session.value
        }
        val config = BaileysConfigManager.config.value
        return if (engine !is SimulatedWhatsAppEngine) {
            engine
        } else if (config.isEnabled) {
            BaileysBridgeClient(
                node = session,
                configProvider = { BaileysConfigManager.config.value }
            )
        } else {
            SimulatedWhatsAppEngine(session)
        }
    }

    fun requestNewPairingCode(nodeType: AccountNode): String {
        val newCode = generatePairingCode()
        val expiresAt = System.currentTimeMillis() + (180 * 1000L) // 3 minutes

        when (nodeType) {
            AccountNode.PERSONAL -> {
                _node1Session.value = _node1Session.value.copy(
                    status = NodeStatus.PAIRING,
                    pairingCode = newCode,
                    codeExpiresAt = expiresAt
                )
            }
            AccountNode.BUSINESS, AccountNode.DUAL_SYNC -> {
                _node2Session.value = _node2Session.value.copy(
                    status = NodeStatus.PAIRING,
                    pairingCode = newCode,
                    codeExpiresAt = expiresAt
                )
            }
        }
        return newCode
    }

    suspend fun requestPairingCodeRemote(nodeType: AccountNode, phoneNumber: String): String {
        val activeEngine = getEngineForNode(nodeType)
        val code = activeEngine.requestPairingCode(phoneNumber)
        val expiresAt = System.currentTimeMillis() + (180 * 1000L) // 3 minutes

        when (nodeType) {
            AccountNode.PERSONAL -> {
                _node1Session.value = _node1Session.value.copy(
                    status = NodeStatus.PAIRING,
                    phoneNumber = phoneNumber,
                    pairingCode = code,
                    codeExpiresAt = expiresAt
                )
            }
            AccountNode.BUSINESS, AccountNode.DUAL_SYNC -> {
                _node2Session.value = _node2Session.value.copy(
                    status = NodeStatus.PAIRING,
                    phoneNumber = phoneNumber,
                    pairingCode = code,
                    codeExpiresAt = expiresAt
                )
            }
        }
        return code
    }

    fun connectNode(nodeType: AccountNode, phoneNumber: String = "+62 812-3456-7890") {
        when (nodeType) {
            AccountNode.PERSONAL -> {
                _node1Session.value = _node1Session.value.copy(
                    status = NodeStatus.CONNECTED,
                    phoneNumber = phoneNumber,
                    uptimeSeconds = 0,
                    healthPercent = 100f
                )
            }
            AccountNode.BUSINESS, AccountNode.DUAL_SYNC -> {
                _node2Session.value = _node2Session.value.copy(
                    status = NodeStatus.CONNECTED,
                    phoneNumber = phoneNumber,
                    uptimeSeconds = 0,
                    healthPercent = 100f
                )
            }
        }
    }

    fun disconnectNode(nodeType: AccountNode) {
        when (nodeType) {
            AccountNode.PERSONAL -> {
                _node1Session.value = _node1Session.value.copy(
                    status = NodeStatus.DISCONNECTED
                )
            }
            AccountNode.BUSINESS, AccountNode.DUAL_SYNC -> {
                _node2Session.value = _node2Session.value.copy(
                    status = NodeStatus.DISCONNECTED
                )
            }
        }
    }

    suspend fun disconnectNodeRemote(nodeType: AccountNode) {
        disconnectNode(nodeType)
        getEngineForNode(nodeType).disconnect()
    }

    suspend fun pingNode(nodeType: AccountNode): Boolean {
        val activeEngine = getEngineForNode(nodeType)
        val result = activeEngine.ping()
        val success = result.isSuccess
        if (success) {
            val now = System.currentTimeMillis()
            when (nodeType) {
                AccountNode.PERSONAL -> _node1Session.value = _node1Session.value.copy(lastPingAt = now)
                AccountNode.BUSINESS, AccountNode.DUAL_SYNC -> _node2Session.value = _node2Session.value.copy(lastPingAt = now)
            }
        }
        return success
    }

    companion object {
        private const val CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        private val random = SecureRandom()

        @Volatile
        private var instance: WhatsAppSessionManager? = null

        fun getInstance(): WhatsAppSessionManager {
            return instance ?: synchronized(this) {
                instance ?: WhatsAppSessionManager().also { instance = it }
            }
        }

        fun generatePairingCode(): String {
            val part1 = StringBuilder(4)
            val part2 = StringBuilder(4)
            repeat(4) {
                part1.append(CODE_CHARS[random.nextInt(CODE_CHARS.length)])
                part2.append(CODE_CHARS[random.nextInt(CODE_CHARS.length)])
            }
            return "$part1-$part2"
        }
    }
}
