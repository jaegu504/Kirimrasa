package com.wabulk.sender.engine.model

import com.wabulk.sender.domain.model.AccountNode

data class NodeSession(
    val id: String,
    val nodeType: AccountNode,
    val phoneNumber: String = "",
    val status: NodeStatus = NodeStatus.DISCONNECTED,
    val pairingCode: String = "",
    val codeExpiresAt: Long = 0L,
    val uptimeSeconds: Long = 0L,
    val healthPercent: Float = 100f,
    val lastPingAt: Long = System.currentTimeMillis()
)
