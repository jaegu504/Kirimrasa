package com.wabulk.sender

import com.wabulk.sender.data.media.MediaAttachmentManager
import com.wabulk.sender.domain.model.MediaAttachment
import com.wabulk.sender.domain.model.MediaType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaAttachmentTest {

    @Test
    fun `formatFileSize correctly formats bytes into KB and MB`() {
        val attachmentBytes = MediaAttachment(
            uriString = "content://media/1",
            fileName = "flyer.jpg",
            mimeType = "image/jpeg",
            sizeBytes = 1024L * 500 // 500 KB
        )
        assertEquals("500 KB", attachmentBytes.formattedSize)

        val attachmentMb = MediaAttachment(
            uriString = "content://media/2",
            fileName = "video.mp4",
            mimeType = "video/mp4",
            sizeBytes = (1024L * 1024L * 2.4).toLong() // 2.4 MB
        )
        assertEquals("2.4 MB", attachmentMb.formattedSize)
    }

    @Test
    fun `mediaType correctly resolves Image, Video, Audio, Document`() {
        val img = MediaAttachment(uriString = "", fileName = "pic.png", mimeType = "image/png", sizeBytes = 100)
        assertEquals(MediaType.IMAGE, img.mediaType)

        val vid = MediaAttachment(uriString = "", fileName = "promo.mp4", mimeType = "video/mp4", sizeBytes = 100)
        assertEquals(MediaType.VIDEO, vid.mediaType)

        val doc = MediaAttachment(uriString = "", fileName = "catalog.pdf", mimeType = "application/pdf", sizeBytes = 100)
        assertEquals(MediaType.DOCUMENT, doc.mediaType)
    }

    @Test
    fun `mediaAttachmentManager enforces maximum 5 attachments`() {
        val manager = MediaAttachmentManager()
        for (i in 1..5) {
            val success = manager.addAttachment(
                MediaAttachment(uriString = "content://$i", fileName = "file$i.jpg", mimeType = "image/jpeg", sizeBytes = 1000)
            )
            assertTrue(success)
        }
        assertEquals(5, manager.attachments.value.size)

        // 6th attachment must be rejected
        val rejected = manager.addAttachment(
            MediaAttachment(uriString = "content://6", fileName = "file6.jpg", mimeType = "image/jpeg", sizeBytes = 1000)
        )
        assertFalse(rejected)
        assertEquals(5, manager.attachments.value.size)
    }

    @Test
    fun `mediaAttachmentManager removes attachment by id`() {
        val manager = MediaAttachmentManager()
        val item = MediaAttachment(id = "item_1", uriString = "content://1", fileName = "promo.jpg", mimeType = "image/jpeg", sizeBytes = 1000)
        manager.addAttachment(item)
        assertEquals(1, manager.attachments.value.size)

        manager.removeAttachment("item_1")
        assertEquals(0, manager.attachments.value.size)
    }
}
