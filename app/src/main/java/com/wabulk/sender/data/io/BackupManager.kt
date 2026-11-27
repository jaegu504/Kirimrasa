package com.wabulk.sender.data.io

import android.content.Context
import android.content.Intent
import androidx.room.withTransaction
import com.wabulk.sender.BuildConfig
import com.wabulk.sender.data.local.db.AppDatabase
import com.wabulk.sender.data.local.db.ContactEntity
import com.wabulk.sender.data.local.db.TagEntity
import com.wabulk.sender.data.repository.ContactRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class BackupContact(
    val name: String,
    val phone: String,
    val nickname: String = "",
    val nickname2: String = "",
    val company: String = "",
    val custom1: String = "",
    val tags: List<String> = emptyList(),
    val lookupKey: String = ""
)

@Serializable
data class BackupTag(val name: String, val color: String = "#25D366")

/** One SharedPreferences entry; [type] is s, b, i, l, f or set (value then holds a JSON string list). */
@Serializable
data class BackupPref(val type: String, val value: String)

@Serializable
data class KirimrasaBackup(
    val app: String = BackupManager.APP_ID,
    val format: Int = 1,
    val createdAt: Long = 0,
    val appVersion: String = "",
    val contacts: List<BackupContact> = emptyList(),
    val tags: List<BackupTag> = emptyList(),
    val prefs: Map<String, Map<String, BackupPref>> = emptyMap()
)

/**
 * Saves contacts, nicknames, labels and settings to one JSON file and restores them,
 * so a reinstall (for example the switch to the release signing key) keeps the user's data.
 * Campaign history is not included.
 */
object BackupManager {
    const val APP_ID = "kirimrasa"

    /** SharedPreferences files worth keeping across a reinstall. */
    val PREF_FILES = listOf(
        "baileys_config_prefs",
        "anti_ban_prefs",
        "wa_session_prefs",
        "composer_prefs",
        "wa_bulk_locale_prefs",
        "kirimrasa_theme_prefs",
        "kirimrasa_onboarding"
    )

    // The server key stays out: a backup file is shared more easily than the phone it came from
    private val SECRET_KEYS = setOf("baileys_config_prefs" to "api_key")

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun encode(backup: KirimrasaBackup): String = json.encodeToString(backup)

    /** Null when the text is not a Kirimrasa backup. */
    fun decode(text: String): KirimrasaBackup? =
        runCatching { json.decodeFromString<KirimrasaBackup>(text) }.getOrNull()?.takeIf { it.app == APP_ID }

    fun toBackup(contact: ContactEntity) = BackupContact(
        name = contact.rawName,
        phone = contact.phoneNumber,
        nickname = contact.customNickname,
        nickname2 = contact.customNickname2,
        company = contact.company,
        custom1 = contact.custom1,
        tags = ContactRepository.parseTagsJson(contact.tagsJson),
        lookupKey = contact.systemLookupKey
    )

    /**
     * The contact to save for one backup entry. An existing contact keeps its id and selection;
     * filled-in backup values win, blank ones keep what the phone already has, and labels are merged.
     */
    fun merge(existing: ContactEntity?, b: BackupContact): ContactEntity {
        if (existing == null) {
            return ContactEntity(
                systemLookupKey = b.lookupKey,
                rawName = b.name,
                phoneNumber = b.phone,
                customNickname = b.nickname,
                customNickname2 = b.nickname2,
                company = b.company,
                custom1 = b.custom1,
                tagsJson = json.encodeToString(b.tags.distinct())
            )
        }
        val tags = (ContactRepository.parseTagsJson(existing.tagsJson) + b.tags).distinct()
        return existing.copy(
            customNickname = b.nickname.ifBlank { existing.customNickname },
            customNickname2 = b.nickname2.ifBlank { existing.customNickname2 },
            company = b.company.ifBlank { existing.company },
            custom1 = b.custom1.ifBlank { existing.custom1 },
            tagsJson = json.encodeToString(tags)
        )
    }

    fun snapshotPrefs(file: String, all: Map<String, *>): Map<String, BackupPref> =
        all.mapNotNull { (key, value) ->
            if ((file to key) in SECRET_KEYS) return@mapNotNull null
            val pref = when (value) {
                is String -> BackupPref("s", value)
                is Boolean -> BackupPref("b", value.toString())
                is Int -> BackupPref("i", value.toString())
                is Long -> BackupPref("l", value.toString())
                is Float -> BackupPref("f", value.toString())
                is Set<*> -> BackupPref("set", json.encodeToString(value.filterIsInstance<String>()))
                else -> null
            }
            pref?.let { key to it }
        }.toMap()

    /** Writes the backup and returns how many contacts it holds. */
    suspend fun export(context: Context, out: OutputStream): Int {
        val db = AppDatabase.getInstance(context)
        val contacts = db.contactDao().getAll()
        val backup = KirimrasaBackup(
            createdAt = System.currentTimeMillis(),
            appVersion = BuildConfig.VERSION_NAME,
            contacts = contacts.map(::toBackup),
            tags = db.tagDao().getAll().map { BackupTag(it.name, it.colorHex) },
            prefs = PREF_FILES.associateWith { snapshotPrefs(it, context.getSharedPreferences(it, Context.MODE_PRIVATE).all) }
        )
        out.bufferedWriter().use { it.write(encode(backup)) }
        return contacts.size
    }

    /** Restores the backup and returns its contact count, or null when the file is not a Kirimrasa backup. */
    suspend fun restore(context: Context, input: InputStream): Int? {
        val backup = decode(input.bufferedReader().use { it.readText() }) ?: return null
        val db = AppDatabase.getInstance(context)
        db.withTransaction {
            backup.tags.forEach { db.tagDao().insert(TagEntity(name = it.name, colorHex = it.color)) }
            backup.contacts.filter { it.phone.isNotBlank() }.forEach { b ->
                val existing = db.contactDao().findByPhone(b.phone)
                val merged = merge(existing, b)
                if (existing == null) db.contactDao().upsert(merged) else db.contactDao().update(merged)
            }
        }
        backup.prefs.filterKeys { it in PREF_FILES }.forEach { (file, entries) ->
            val editor = context.getSharedPreferences(file, Context.MODE_PRIVATE).edit()
            entries.forEach { (key, p) ->
                if ((file to key) in SECRET_KEYS) return@forEach
                runCatching {
                    when (p.type) {
                        "s" -> editor.putString(key, p.value)
                        "b" -> editor.putBoolean(key, p.value.toBooleanStrict())
                        "i" -> editor.putInt(key, p.value.toInt())
                        "l" -> editor.putLong(key, p.value.toLong())
                        "f" -> editor.putFloat(key, p.value.toFloat())
                        "set" -> editor.putStringSet(key, json.decodeFromString<List<String>>(p.value).toSet())
                    }
                }
            }
            editor.commit()
        }
        return backup.contacts.size
    }

    /** Settings live in memory once loaded, so restored ones apply only after a fresh start. */
    fun restartApp(context: Context) {
        val launch = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return
        context.startActivity(Intent.makeRestartActivityTask(launch.component))
        Runtime.getRuntime().exit(0)
    }
}
