package com.wabulk.sender

import com.wabulk.sender.domain.model.ComposerState
import com.wabulk.sender.domain.model.MessageBubble
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MessageBubbleTest {

    @Test
    fun `default ComposerState contains exactly one empty bubble`() {
        val state = ComposerState()
        assertEquals(1, state.bubbles.size)
        assertEquals(0, state.bubbles.first().order)
        assertTrue(state.bubbles.first().text.isEmpty())
    }

    @Test
    fun `adding bubble increments list up to max limit of 5`() {
        var state = ComposerState()
        state = state.addBubble("Balon 2")
        assertEquals(2, state.bubbles.size)
        assertEquals(1, state.bubbles[1].order)

        repeat(3) { state = state.addBubble("Balon ${it + 3}") }
        assertEquals(5, state.bubbles.size)

        // A 6th bubble is ignored
        state = state.addBubble("Balon 6")
        assertEquals(5, state.bubbles.size)
    }

    @Test
    fun `removing bubble never empties list below 1 bubble`() {
        var state = ComposerState()
        state = state.addBubble("Balon 2")
        assertEquals(2, state.bubbles.size)

        val idToRemove = state.bubbles[1].id
        state = state.removeBubble(idToRemove)
        assertEquals(1, state.bubbles.size)

        // Attempting to remove the only remaining bubble should not remove it
        state = state.removeBubble(state.bubbles[0].id)
        assertEquals(1, state.bubbles.size)
    }

    @Test
    fun `updating bubble text preserves id and order`() {
        val bubble = MessageBubble(id = "bubble_1", order = 0, text = "Hello")
        val updated = bubble.copy(text = "Hello World")
        assertEquals("bubble_1", updated.id)
        assertEquals(0, updated.order)
        assertEquals("Hello World", updated.text)
    }

    @Test
    fun `totalCharCount aggregates all bubble texts`() {
        val state = ComposerState(
            bubbles = listOf(
                MessageBubble(order = 0, text = "Halo {Nickname}!"), // 16 chars
                MessageBubble(order = 1, text = "Ada diskon 50%.")    // 15 chars
            )
        )
        assertEquals(31, state.totalCharCount)
    }
}
