package com.wabulk.sender.engine.queue.model

import com.wabulk.sender.domain.model.AccountNode

enum class QueueTaskStatus {
    PENDING,
    TYPING,
    DELIVERED,
    FAILED,
    PAUSED
}

data class QueueItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val contactId: Long,
    val recipientName: String,
    val phoneNumber: String,
    val bubbleTexts: List<String>,
    val nodeUsed: AccountNode = AccountNode.PERSONAL,
    val status: QueueTaskStatus = QueueTaskStatus.PENDING,
    val jitterSecondsAssigned: Int = 34,
    val sentTimestamp: Long? = null,
    val errorMessage: String? = null
)
