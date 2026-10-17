package com.wabulk.sender.domain.model

enum class AccountNode {
    PERSONAL,
    BUSINESS,
    DUAL_SYNC
}

data class ComposerState(
    val bubbles: List<MessageBubble> = listOf(MessageBubble(order = 0, text = "")),
    val selectedAccount: AccountNode = AccountNode.PERSONAL,
    val jitterMinSeconds: Int = 25,
    val jitterMaxSeconds: Int = 60,
    val selectedTone: String = "friendly",
    val isAiRewriting: Boolean = false,
    val aiErrorMessage: String? = null,
    val customVariables: Map<String, String> = mapOf(
        "Company" to "KirimRasa",
        "Custom_1" to "PROMO30"
    )
) {
    val totalCharCount: Int
        get() = bubbles.sumOf { it.text.length }

    val whatsappSegments: Int
        get() = bubbles.sumOf { (it.text.length / 160).coerceAtLeast(1) }

    fun addBubble(initialText: String = ""): ComposerState {
        if (bubbles.size >= 3) return this // Max 3 bubbles constraint
        val newOrder = bubbles.size
        val newBubble = MessageBubble(order = newOrder, text = initialText)
        return copy(bubbles = bubbles + newBubble)
    }

    fun removeBubble(bubbleId: String): ComposerState {
        if (bubbles.size <= 1) return this // Cannot delete the only remaining bubble
        val filtered = bubbles.filterNot { it.id == bubbleId }
        val reindexed = filtered.mapIndexed { index, bubble -> bubble.copy(order = index) }
        return copy(bubbles = reindexed)
    }

    fun updateBubbleText(bubbleId: String, text: String): ComposerState {
        val updated = bubbles.map { bubble ->
            if (bubble.id == bubbleId) bubble.copy(text = text) else bubble
        }
        return copy(bubbles = updated)
    }

    fun updateCustomVariable(key: String, value: String): ComposerState {
        val updated = customVariables.toMutableMap()
        updated[key] = value
        return copy(customVariables = updated)
    }
}
