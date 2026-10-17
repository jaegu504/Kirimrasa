package com.wabulk.sender.domain.model

import java.util.UUID

data class MessageBubble(
    val id: String = UUID.randomUUID().toString(),
    val order: Int = 0,
    val text: String = ""
)
