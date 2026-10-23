package com.wabulk.sender.data

import android.content.Context
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/** Remembers the bubbles of the last launched broadcast so the composer opens with them. */
object LastMessageStore {
    private const val PREFS = "composer_prefs"
    private const val KEY_BUBBLES = "last_sent_bubbles"
    private val serializer = ListSerializer(String.serializer())

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun load(): List<String> {
        val raw = appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)?.getString(KEY_BUBBLES, null) ?: return emptyList()
        return try {
            Json.decodeFromString(serializer, raw).filter { it.isNotBlank() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun save(bubbles: List<String>) {
        val texts = bubbles.filter { it.isNotBlank() }
        if (texts.isEmpty()) return
        appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)?.edit()
            ?.putString(KEY_BUBBLES, Json.encodeToString(serializer, texts))
            ?.apply()
    }
}
