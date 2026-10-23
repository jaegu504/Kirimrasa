package com.wabulk.sender

import com.wabulk.sender.domain.model.MediaAttachment
import com.wabulk.sender.engine.queue.DispatchQueueManager
import com.wabulk.sender.engine.queue.model.QueueItem
import org.junit.Assert.assertEquals
import org.junit.Test

class SendStepsTest {
    private val manager = DispatchQueueManager.getInstance()
    private fun item(media: List<String>, placements: List<Int>) = QueueItem(
        contactId = 1, recipientName = "Budi", phoneNumber = "628",
        bubbleTexts = listOf("Halo", "Promo"), mediaUris = media, mediaPlacements = placements
    )

    @Test
    fun `photo placed on bubble 2 is sent with bubble 2 as caption`() {
        val steps = manager.buildSendSteps(item(listOf("img"), listOf(1)))
        assertEquals(listOf("Halo" to null, "Promo" to "img"), steps.map { it.text to it.mediaUri })
    }

    @Test
    fun `separate file goes alone after all bubbles`() {
        val steps = manager.buildSendSteps(item(listOf("doc"), listOf(MediaAttachment.SEPARATE_AFTER)))
        assertEquals(listOf("Halo" to null, "Promo" to null, "" to "doc"), steps.map { it.text to it.mediaUri })
    }

    @Test
    fun `no placement keeps old behaviour of first bubble as caption`() {
        val steps = manager.buildSendSteps(item(listOf("a", "b"), emptyList()))
        assertEquals(listOf("Halo" to "a", "" to "b", "Promo" to null), steps.map { it.text to it.mediaUri })
    }
}
