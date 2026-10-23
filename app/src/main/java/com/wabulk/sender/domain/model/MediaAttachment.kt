package com.wabulk.sender.domain.model

import java.util.UUID

enum class MediaType {
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT
}

data class MediaAttachment(
    val id: String = UUID.randomUUID().toString(),
    val uriString: String,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    /** Bubble (0-based) this file is sent with as caption; [SEPARATE_AFTER] = alone after all bubbles. */
    val bubbleIndex: Int = 0
) {
    companion object {
        const val SEPARATE_AFTER = -1
    }

    val mediaType: MediaType
        get() = when {
            mimeType.startsWith("image/") -> MediaType.IMAGE
            mimeType.startsWith("video/") -> MediaType.VIDEO
            mimeType.startsWith("audio/") -> MediaType.AUDIO
            else -> MediaType.DOCUMENT
        }

    val formattedSize: String
        get() {
            if (sizeBytes < 1024L) return "$sizeBytes B"
            val kb = sizeBytes / 1024.0
            if (kb < 1024.0) {
                return if (kb % 1.0 == 0.0) "${kb.toInt()} KB" else String.format(java.util.Locale.US, "%.1f KB", kb)
            }
            val mb = kb / 1024.0
            return String.format(java.util.Locale.US, "%.1f MB", mb)
        }
}
