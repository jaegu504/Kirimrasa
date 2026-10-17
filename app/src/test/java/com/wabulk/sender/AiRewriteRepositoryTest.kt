package com.wabulk.sender

import com.wabulk.sender.data.remote.CloudflareAiApi
import com.wabulk.sender.data.repository.AiRewriteRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AiRewriteRepositoryTest {

    private val api = CloudflareAiApi()

    @Test
    fun `guardrail restores Nickname token if LLM accidentally drops it`() {
        val originalPrompt = "Halo {Nickname}! Kami menawarkan diskon 50% hari ini."
        val mockBubblesFromLlm = listOf(
            "Halo kawan! Ada penawaran spesial untukmu.",
            "Dapatkan diskon 50% sekarang."
        )

        // LLM dropped {Nickname} and replaced with "kawan"
        val guardedBubbles = AiRewriteRepository.enforceTokenPreservation(
            originalPrompt = originalPrompt,
            generatedBubbles = mockBubblesFromLlm
        )

        // Guardrail MUST guarantee {Nickname} is present in the first bubble
        assertTrue(guardedBubbles.first().contains("{Nickname}"))
    }

    @Test
    fun `guardrail keeps Nickname intact when LLM properly preserved it`() {
        val originalPrompt = "Halo {Nickname}! Kami dari {Company}."
        val mockBubblesFromLlm = listOf(
            "Hai {Nickname}! 👋",
            "Ada penawaran terbaik dari {Company} khusus hari ini."
        )

        val guardedBubbles = AiRewriteRepository.enforceTokenPreservation(
            originalPrompt = originalPrompt,
            generatedBubbles = mockBubblesFromLlm
        )

        assertEquals(2, guardedBubbles.size)
        assertTrue(guardedBubbles[0].contains("{Nickname}"))
        assertTrue(guardedBubbles[1].contains("{Company}"))
    }

    @Test
    fun `guardrail handles multiple bubbles and trims whitespace`() {
        val originalPrompt = "Halo {Nickname}!"
        val mockBubblesFromLlm = listOf(
            "  Hai {Nickname}!  ",
            "   Cek promo ini: https://example.com   "
        )

        val guarded = AiRewriteRepository.enforceTokenPreservation(
            originalPrompt = originalPrompt,
            generatedBubbles = mockBubblesFromLlm
        )

        assertEquals("Hai {Nickname}!", guarded[0])
        assertEquals("Cek promo ini: https://example.com", guarded[1])
    }

    @Test
    fun `parseBubbles parses Groq response format from Sapa CRM`() {
        val groqResponse = """
            {
              "id": "chatcmpl-123",
              "choices": [
                {
                  "message": {
                    "role": "assistant",
                    "content": "[\"Halo {Nickname}!\", \"Promo spesial hari ini.\"]"
                  }
                }
              ]
            }
        """.trimIndent()

        val bubbles = api.parseBubbles(groqResponse)
        assertEquals(2, bubbles.size)
        assertEquals("Halo {Nickname}!", bubbles[0])
        assertEquals("Promo spesial hari ini.", bubbles[1])
    }

    @Test
    fun `parseBubbles parses Gemini response format from Sapa CRM`() {
        val geminiResponse = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      {
                        "text": "[\"Balon 1 {Nickname}\", \"Balon 2 {Company}\"]"
                      }
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val bubbles = api.parseBubbles(geminiResponse)
        assertEquals(2, bubbles.size)
        assertEquals("Balon 1 {Nickname}", bubbles[0])
        assertEquals("Balon 2 {Company}", bubbles[1])
    }

    @Test
    fun `parseBubbles parses direct JSON array`() {
        val jsonArrayResponse = """["Halo {Nickname}!", "Pesan kedua"]"""
        val bubbles = api.parseBubbles(jsonArrayResponse)
        assertEquals(2, bubbles.size)
        assertEquals("Halo {Nickname}!", bubbles[0])
        assertEquals("Pesan kedua", bubbles[1])
    }
}
