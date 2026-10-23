package com.wabulk.sender.data.repository

import com.wabulk.sender.data.locale.LocalizationManager
import com.wabulk.sender.data.remote.CloudflareAiApi
import com.wabulk.sender.data.remote.dto.AiRewriteRequest

class AiRewriteRepository(
    private val api: CloudflareAiApi = CloudflareAiApi()
) {
    var defaultEndpointUrl: String = "https://kirimrasa.jaegu108.workers.dev"

    suspend fun rewriteMessage(
        originalDraft: String,
        language: String = "ID",
        tone: String = "friendly",
        maxBubbles: Int = 3,
        endpointUrl: String? = null
    ): Result<List<String>> {
        val targetUrl = endpointUrl?.trim()?.ifEmpty { null } ?: defaultEndpointUrl

        val request = AiRewriteRequest(
            message = originalDraft,
            language = language,
            tone = tone,
            maxBubbles = maxBubbles
        )

        val result = api.rewrite(targetUrl, request)
        return result.map { response ->
            enforceTokenPreservation(originalDraft, response.bubbles)
        }
    }

    companion object {
        private val VARIABLE_TOKEN_REGEX = Regex("""\{([A-Za-z0-9_]+)\}""")

        /**
         * Strict Guardrail: Guarantees that any variable token (e.g. {Nickname}, {Company})
         * that was present in the user's original draft will be preserved in the output bubbles.
         * If the LLM dropped it or altered it, this guardrail injects it into the first bubble.
         */
        fun enforceTokenPreservation(
            originalPrompt: String,
            generatedBubbles: List<String>
        ): List<String> {
            if (generatedBubbles.isEmpty()) return listOf(originalPrompt.trim())

            val cleanedBubbles = generatedBubbles.map { it.trim() }.toMutableList()

            // Find all variable tokens like {Nickname}, {Company} in the original prompt
            val originalTokens = VARIABLE_TOKEN_REGEX.findAll(originalPrompt)
                .map { it.value }
                .distinct()
                .toList()

            // Verify each original token is present somewhere in the generated bubbles
            for (token in originalTokens) {
                val exists = cleanedBubbles.any { it.contains(token) }
                if (!exists) {
                    // Token was dropped by the AI model! Re-inject into the opening bubble
                    val firstBubble = cleanedBubbles.first()
                    cleanedBubbles[0] = if (token == "{Nickname}") {
                        "${LocalizationManager.activeStrings.greetingWord} {Nickname}! $firstBubble"
                    } else {
                        "$firstBubble $token"
                    }
                }
            }

            return cleanedBubbles
        }
    }
}
