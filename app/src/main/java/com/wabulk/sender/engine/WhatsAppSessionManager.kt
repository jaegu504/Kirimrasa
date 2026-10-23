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
import java.util.UUID

class WhatsAppSessionManager(
    private val engine: WhatsAppEngine = SimulatedWhatsAppEngine()
) {
    private val _node1Session = MutableStateFlow(
        NodeSession(
            id = "node-personal-01",
            nodeType = AccountNode.PERSONAL,
            phoneNumber = "",
            status = NodeStatus.DISCONNECTED,
            uptimeSeconds = 0,
            healthPercent = 100f
        )
    )
    val node1Session: StateFlow<NodeSession> = _node1Session.asStateFlow()

    private val _node2Session = MutableStateFlow(
        NodeSession(
            id = "node-business-02",
            nodeType = AccountNode.BUSINESS,
            phoneNumber = "",
            status = NodeStatus.DISCONNECTED,
            pairingCode = "",
            codeExpiresAt = 0L
        )
    )
    val node2Session: StateFlow<NodeSession> = _node2Session.asStateFlow()

    private var appContext: android.content.Context? = null

    fun initPreferences(context: android.content.Context) {
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences("wa_session_prefs", android.content.Context.MODE_PRIVATE)
        val p1 = prefs.getString("node1_phone", "") ?: ""
        val p2 = prefs.getString("node2_phone", "") ?: ""
        if (p1.isNotEmpty()) {
            _node1Session.value = _node1Session.value.copy(phoneNumber = p1)
        }
        if (p2.isNotEmpty()) {
            _node2Session.value = _node2Session.value.copy(phoneNumber = p2)
        }

        // Gateway node IDs must be unique per install: a shared ID lets every phone see and
        // control the same WhatsApp session on the gateway.
        val id1 = prefs.getString("node1_id", null) ?: "node-personal-${UUID.randomUUID()}"
        val id2 = prefs.getString("node2_id", null) ?: "node-business-${UUID.randomUUID()}"
        prefs.edit().putString("node1_id", id1).putString("node2_id", id2).apply()
        if (_node1Session.value.id != id1 || _node2Session.value.id != id2) {
            _node1Session.value = _node1Session.value.copy(id = id1)
            _node2Session.value = _node2Session.value.copy(id = id2)
            resetBaileysEngines()
        }
    }

    fun updateNodePhone(nodeType: AccountNode, phoneNumber: String, context: android.content.Context? = null) {
        if (context != null) {
            appContext = context.applicationContext
        }
        when (nodeType) {
            AccountNode.PERSONAL -> {
                _node1Session.value = _node1Session.value.copy(phoneNumber = phoneNumber)
                context?.getSharedPreferences("wa_session_prefs", android.content.Context.MODE_PRIVATE)
                    ?.edit()?.putString("node1_phone", phoneNumber)?.apply()
            }
            AccountNode.BUSINESS, AccountNode.DUAL_SYNC -> {
                _node2Session.value = _node2Session.value.copy(phoneNumber = phoneNumber)
                context?.getSharedPreferences("wa_session_prefs", android.content.Context.MODE_PRIVATE)
                    ?.edit()?.putString("node2_phone", phoneNumber)?.apply()
            }
        }
    }

    @Volatile
    private var cachedNode1Engine: BaileysBridgeClient? = null

    @Volatile
    private var cachedNode2Engine: BaileysBridgeClient? = null

    @Synchronized
    fun getOrCreateBaileysEngine(nodeType: AccountNode): BaileysBridgeClient {
        val client = when (nodeType) {
            AccountNode.PERSONAL -> {
                cachedNode1Engine ?: BaileysBridgeClient(
                    node = _node1Session.value,
                    configProvider = { BaileysConfigManager.config.value },
                    autoBackupSession = true,
                    onStatusChanged = { newStatus ->
                        _node1Session.value = _node1Session.value.copy(status = newStatus)
                    }
                ).also { cachedNode1Engine = it }
            }
            AccountNode.BUSINESS, AccountNode.DUAL_SYNC -> {
                cachedNode2Engine ?: BaileysBridgeClient(
                    node = _node2Session.value,
                    configProvider = { BaileysConfigManager.config.value },
                    autoBackupSession = true,
                    onStatusChanged = { newStatus ->
                        _node2Session.value = _node2Session.value.copy(status = newStatus)
                    }
                ).also { cachedNode2Engine = it }
            }
        }
        if (client.context == null && appContext != null) {
            client.context = appContext
        }
        return client
    }

    fun resetBaileysEngines() {
        cachedNode1Engine = null
        cachedNode2Engine = null
    }

    fun getEngineForNode(nodeType: AccountNode, context: android.content.Context? = null): WhatsAppEngine {
        if (context != null) {
            appContext = context.applicationContext
        }
        val session = when (nodeType) {
            AccountNode.PERSONAL -> _node1Session.value
            AccountNode.BUSINESS, AccountNode.DUAL_SYNC -> _node2Session.value
        }
        val config = BaileysConfigManager.config.value
        return if (engine !is SimulatedWhatsAppEngine) {
            engine
        } else if (config.isEnabled) {
            val client = getOrCreateBaileysEngine(nodeType)
            if (context != null) {
                client.context = context.applicationContext
            }
            client
        } else if (context != null) {
            OnDeviceWhatsAppEngine(context.applicationContext, session)
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

    fun connectNode(nodeType: AccountNode, phoneNumber: String = "") {
        val effectivePhone = phoneNumber.ifEmpty {
            when (nodeType) {
                AccountNode.PERSONAL -> _node1Session.value.phoneNumber
                AccountNode.BUSINESS, AccountNode.DUAL_SYNC -> _node2Session.value.phoneNumber
            }
        }
        when (nodeType) {
            AccountNode.PERSONAL -> {
                _node1Session.value = _node1Session.value.copy(
                    status = NodeStatus.CONNECTED,
                    phoneNumber = effectivePhone,
                    uptimeSeconds = 0,
                    healthPercent = 100f
                )
            }
            AccountNode.BUSINESS, AccountNode.DUAL_SYNC -> {
                _node2Session.value = _node2Session.value.copy(
                    status = NodeStatus.CONNECTED,
                    phoneNumber = effectivePhone,
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

    suspend fun pingNode(nodeType: AccountNode, context: android.content.Context? = null): Boolean {
        val activeEngine = getEngineForNode(nodeType, context)
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

        fun getInstance(context: android.content.Context? = null): WhatsAppSessionManager {
            return instance ?: synchronized(this) {
                instance ?: WhatsAppSessionManager().also { instance = it }
            }.also { manager ->
                if (context != null) {
                    manager.initPreferences(context.applicationContext)
                }
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
