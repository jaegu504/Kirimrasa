package com.wabulk.sender.ui.screens.composer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wabulk.sender.data.media.MediaAttachmentManager
import com.wabulk.sender.data.repository.AiRewriteRepository
import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.domain.model.ComposerState
import com.wabulk.sender.domain.model.MediaAttachment
import com.wabulk.sender.domain.model.MessageBubble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ComposerViewModel(
    private val mediaManager: MediaAttachmentManager = MediaAttachmentManager(),
    private val aiRepository: AiRewriteRepository = AiRewriteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ComposerState(
            bubbles = listOf(
                MessageBubble(
                    order = 0,
                    text = "Halo {Nickname}! 👋\nKami dari {Company} ingin membagikan penawaran spesial minggu ini."
                )
            )
        )
    )
    val uiState: StateFlow<ComposerState> = _uiState.asStateFlow()

    val attachments: StateFlow<List<MediaAttachment>> = mediaManager.attachments

    private var activeBubbleId: String? = null

    fun setActiveBubble(bubbleId: String) {
        activeBubbleId = bubbleId
    }

    fun addBubble(initialText: String = "") {
        _uiState.value = _uiState.value.addBubble(initialText)
    }

    fun removeBubble(bubbleId: String) {
        _uiState.value = _uiState.value.removeBubble(bubbleId)
    }

    fun updateBubbleText(bubbleId: String, text: String) {
        activeBubbleId = bubbleId
        _uiState.value = _uiState.value.updateBubbleText(bubbleId, text)
    }

    fun insertVariable(variable: String, targetBubbleId: String? = null) {
        val targetId = targetBubbleId ?: activeBubbleId ?: _uiState.value.bubbles.firstOrNull()?.id ?: return
        val currentBubble = _uiState.value.bubbles.find { it.id == targetId } ?: return
        val newText = if (currentBubble.text.isEmpty() || currentBubble.text.endsWith(" ") || currentBubble.text.endsWith("\n")) {
            currentBubble.text + variable
        } else {
            currentBubble.text + " " + variable
        }
        updateBubbleText(targetId, newText)
    }

    fun updateCustomVariable(key: String, value: String) {
        _uiState.value = _uiState.value.updateCustomVariable(key, value)
    }

    fun selectAccount(account: AccountNode) {
        _uiState.value = _uiState.value.copy(selectedAccount = account)
    }

    fun selectTone(tone: String) {
        _uiState.value = _uiState.value.copy(selectedTone = tone)
    }

    fun setJitterRange(min: Int, max: Int) {
        _uiState.value = _uiState.value.copy(jitterMinSeconds = min, jitterMaxSeconds = max)
    }

    fun addAttachment(attachment: MediaAttachment): Boolean {
        return mediaManager.addAttachment(attachment)
    }

    fun removeAttachment(attachmentId: String) {
        mediaManager.removeAttachment(attachmentId)
    }

    fun triggerAiRewrite(language: String = "ID", customUrl: String? = null) {
        val allDraftText = _uiState.value.bubbles.joinToString("\n\n") { it.text }.trim()
        if (allDraftText.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAiRewriting = true, aiErrorMessage = null)
            val result = aiRepository.rewriteMessage(
                originalDraft = allDraftText,
                language = language,
                tone = _uiState.value.selectedTone,
                maxBubbles = 3,
                endpointUrl = customUrl
            )

            result.fold(
                onSuccess = { rewrittenBubbles ->
                    val newBubbles = rewrittenBubbles.take(3).mapIndexed { index, text ->
                        MessageBubble(order = index, text = text)
                    }
                    _uiState.value = _uiState.value.copy(
                        bubbles = if (newBubbles.isNotEmpty()) newBubbles else _uiState.value.bubbles,
                        isAiRewriting = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isAiRewriting = false,
                        aiErrorMessage = error.message ?: "Gagal terhubung ke Cloudflare AI"
                    )
                }
            )
        }
    }
}
