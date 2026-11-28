package com.wabulk.sender.data.media

import com.wabulk.sender.domain.model.MediaAttachment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaAttachmentManager(private val maxAttachments: Int = 5) {

    companion object {
        // Non-image files travel to the gateway as base64 JSON (server limit 50 MB, phone memory); images are recompressed first.
        const val MAX_FILE_MB = 16
        fun isTooBig(mimeType: String, sizeBytes: Long) =
            !mimeType.startsWith("image/") && sizeBytes > MAX_FILE_MB * 1024L * 1024L
    }

    private val _attachments = MutableStateFlow<List<MediaAttachment>>(emptyList())
    val attachments: StateFlow<List<MediaAttachment>> = _attachments.asStateFlow()

    fun setPlacement(attachmentId: String, bubbleIndex: Int) {
        _attachments.value = _attachments.value.map { if (it.id == attachmentId) it.copy(bubbleIndex = bubbleIndex) else it }
    }

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
