package com.wabulk.sender.engine.baileys

import android.content.Context
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Vault for persisting WhatsApp Baileys session credentials on the device.
 * Protects session state against free-tier cloud host ephemeral disk wipes.
 */
object BaileysSessionVault {
    private val memoryStore = ConcurrentHashMap<String, String>()

    fun saveSession(context: Context?, nodeId: String, sessionJson: String) {
        if (sessionJson.isBlank()) return
        memoryStore[nodeId] = sessionJson
        if (context != null) {
            try {
                val dir = File(context.filesDir, "baileys_vault")
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, "vault_$nodeId.json")
                file.writeText(sessionJson)
            } catch (_: Exception) {}
        }
    }

    fun getSession(context: Context?, nodeId: String): String? {
        memoryStore[nodeId]?.let { return it }
        if (context != null) {
            try {
                val file = File(context.filesDir, "baileys_vault/vault_$nodeId.json")
                if (file.exists()) {
                    val content = file.readText()
                    if (content.isNotBlank()) {
                        memoryStore[nodeId] = content
                        return content
                    }
                }
            } catch (_: Exception) {}
        }
        return null
    }

    fun hasSession(context: Context?, nodeId: String): Boolean {
        return getSession(context, nodeId) != null
    }

    fun clearSession(context: Context?, nodeId: String) {
        memoryStore.remove(nodeId)
        if (context != null) {
            try {
                val file = File(context.filesDir, "baileys_vault/vault_$nodeId.json")
                if (file.exists()) {
                    file.delete()
                }
            } catch (_: Exception) {}
        }
    }
}
