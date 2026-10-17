package com.wabulk.sender.domain.spintax

import kotlin.random.Random

object SpintaxParser {

    // Matches spintax options like {Halo|Hai|Pagi} but ignores variable tokens like {Nickname}
    private val SPINTAX_REGEX = Regex("""\{([^{}]+?\|[^{}]+?)\}""")

    /**
     * Resolves all spintax permutations by picking a random choice for each {opt1|opt2|...} block.
     * Variable tokens without pipe (e.g. {Nickname}, {Company}) are preserved intact.
     */
    fun spin(template: String, random: Random = Random.Default): String {
        var result = template
        while (SPINTAX_REGEX.containsMatchIn(result)) {
            result = SPINTAX_REGEX.replace(result) { matchResult ->
                val content = matchResult.groupValues[1]
                val options = content.split("|").map { it.trim() }
                if (options.isNotEmpty()) {
                    options[random.nextInt(options.size)]
                } else {
                    ""
                }
            }
        }
        return result
    }

    /**
     * Replaces variable tokens like {Nickname} or {Company} with user-specific values.
     */
    fun interpolateVariables(
        text: String,
        nickname: String,
        variables: Map<String, String> = emptyMap()
    ): String {
        val effectiveNickname = nickname.trim().ifEmpty { "Kak" }
        var output = text.replace("{Nickname}", effectiveNickname)

        for ((key, value) in variables) {
            output = output.replace("{$key}", value)
        }

        // Clean any stray variables if needed
        return output
    }

    /**
     * Calculates total possible variations for anti-ban rotation metrics.
     */
    fun countVariations(template: String): Int {
        var count = 1
        val matches = SPINTAX_REGEX.findAll(template)
        for (match in matches) {
            val options = match.groupValues[1].split("|")
            if (options.size > 1) {
                count *= options.size
            }
        }
        return count
    }
}
