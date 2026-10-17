package com.wabulk.sender.data.locale

enum class AppLanguage(val code: String, val displayName: String, val flagEmoji: String) {
    INDONESIAN("id", "Bahasa Indonesia", "🇮🇩"),
    TURKISH("tr", "Türkçe", "🇹🇷");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: INDONESIAN
    }
}
