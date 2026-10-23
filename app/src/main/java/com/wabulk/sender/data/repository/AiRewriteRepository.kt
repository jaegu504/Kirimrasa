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

        // One retry when the model returns something unusable; the draft is never replaced by bad output
        repeat(2) {
            val result = api.rewrite(targetUrl, request)
            val bubbles = result.getOrElse { return Result.failure(it) }.bubbles
            val cleaned = normalizeBubbles(bubbles, maxBubbles)
            if (isValidOutput(originalDraft, cleaned)) {
                return Result.success(enforceTokenPreservation(originalDraft, cleaned))
            }
        }
        return Result.failure(IllegalStateException(LocalizationManager.activeStrings.aiInvalidOutput))
    }

    companion object {
        private val VARIABLE_TOKEN_REGEX = Regex("""\{([A-Za-z0-9_]+)\}""")
        private val KNOWN_VARIABLES = setOf("{Nickname}", "{Company}", "{Custom_1}")

        /** Drops empty bubbles and folds any extra ones into the last allowed bubble. */
        fun normalizeBubbles(bubbles: List<String>, maxBubbles: Int): List<String> {
            val nonEmpty = bubbles.map { it.trim() }.filter { it.isNotEmpty() }
            val max = maxBubbles.coerceIn(1, 3)
            if (nonEmpty.size <= max) return nonEmpty
            return nonEmpty.take(max - 1) + nonEmpty.drop(max - 1).joinToString("\n")
        }

        /**
         * Rejects output that would reach recipients broken: no bubbles, an invented {Variable}
         * (sent literally), or unbalanced/nested spintax braces.
         */
        fun isValidOutput(originalDraft: String, bubbles: List<String>): Boolean {
            if (bubbles.isEmpty()) return false
            val allowed = KNOWN_VARIABLES + VARIABLE_TOKEN_REGEX.findAll(originalDraft).map { it.value }
            for (bubble in bubbles) {
                if (VARIABLE_TOKEN_REGEX.findAll(bubble).any { it.value !in allowed }) return false
                var depth = 0
                for (c in bubble) {
                    if (c == '{') depth++ else if (c == '}') depth--
                    if (depth < 0 || depth > 1) return false
                }
                if (depth != 0) return false
            }
            return true
        }

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
