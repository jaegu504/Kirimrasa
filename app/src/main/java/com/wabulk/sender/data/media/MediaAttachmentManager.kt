package com.wabulk.sender.data.media

import com.wabulk.sender.domain.model.MediaAttachment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaAttachmentManager(private val maxAttachments: Int = 5) {

    private val _attachments = MutableStateFlow<List<MediaAttachment>>(emptyList())
    val attachments: StateFlow<List<MediaAttachment>> = _attachments.asStateFlow()

    fun addAttachment(attachment: MediaAttachment): Boolean {
        if (_attachments.value.size >= maxAttachments) {
            return false
        }
        _attachments.value = _attachments.value + attachment
        return true
    }

    fun removeAttachment(attachmentId: String) {
        _attachments.value = _attachments.value.filterNot { it.id == attachmentId }
    }

    fun clearAttachments() {
        _attachments.value = emptyList()
    }
}
