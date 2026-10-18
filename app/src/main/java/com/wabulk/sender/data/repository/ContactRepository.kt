package com.wabulk.sender.data.repository

import com.wabulk.sender.data.local.db.ContactDao
import com.wabulk.sender.data.local.db.ContactEntity
import com.wabulk.sender.data.local.db.TagDao
import com.wabulk.sender.data.local.db.TagEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class ContactRepository(
    private val contactDao: ContactDao,
    private val tagDao: TagDao
) {
    val allContacts: Flow<List<ContactEntity>> = contactDao.observeAll()
    val totalCount: Flow<Int> = contactDao.observeTotalCount()
    val selectedCount: Flow<Int> = contactDao.observeSelectedCount()
    val nicknameCount: Flow<Int> = contactDao.observeNicknameCount()
    val allTags: Flow<List<TagEntity>> = tagDao.observeAll()

    fun searchContacts(query: String): Flow<List<ContactEntity>> = contactDao.search(query)

    fun filterByTag(tag: String): Flow<List<ContactEntity>> = contactDao.filterByTag(tag)

    fun searchWithTag(query: String, tag: String): Flow<List<ContactEntity>> = contactDao.searchWithTag(query, tag)

    suspend fun upsertContact(contact: ContactEntity): Long = contactDao.upsert(contact)

    suspend fun updateNickname(contactId: Long, nickname: String) {
        contactDao.updateNickname(contactId, nickname)
    }

    suspend fun toggleSelection(contactId: Long, selected: Boolean) {
        contactDao.updateSelection(contactId, selected)
    }

    suspend fun selectAllInTag(tag: String, selected: Boolean) {
        contactDao.selectAllInTag(tag, selected)
    }

    suspend fun addTagToContact(contactId: Long, tagName: String) {
        val contact = contactDao.getAll().find { it.id == contactId } ?: return
        val currentTags = parseTagsJson(contact.tagsJson).toMutableList()
        if (tagName !in currentTags) {
            currentTags.add(tagName)
            contactDao.updateTags(contactId, Json.encodeToString(currentTags))
        }
    }

    suspend fun removeTagFromContact(contactId: Long, tagName: String) {
        val contact = contactDao.getAll().find { it.id == contactId } ?: return
        val currentTags = parseTagsJson(contact.tagsJson).toMutableList()
        currentTags.remove(tagName)
        contactDao.updateTags(contactId, Json.encodeToString(currentTags))
    }

    suspend fun createTag(name: String): Long {
        return tagDao.insert(TagEntity(name = name))
    }

    suspend fun deleteTag(tagId: Long) {
        tagDao.delete(tagId)
    }

    suspend fun getSelectedContacts(): List<ContactEntity> = contactDao.getSelected()

    fun observeSelectedContacts(): Flow<List<ContactEntity>> = contactDao.observeSelected()

    suspend fun selectAll(selected: Boolean) {
        contactDao.selectAll(selected)
    }

    suspend fun sanitizeNicknames() {
        contactDao.sanitizeInvalidNicknames()
    }

    /**
     * Applies quick format: extract first name from rawName as nickname.
     */
    suspend fun quickFormatFirstName(contactId: Long) {
        val contact = contactDao.getAll().find { it.id == contactId } ?: return
        val firstName = extractSmartNickname(contact.rawName)
        contactDao.updateNickname(contactId, firstName)
    }

    /**
     * Applies quick format: remove everything in parentheses and clean up name for nickname.
     */
    suspend fun quickFormatRemoveNotes(contactId: Long) {
        val contact = contactDao.getAll().find { it.id == contactId } ?: return
        val smartNick = extractSmartNickname(contact.rawName)
        if (smartNick.isNotBlank()) {
            val cleanName = contact.rawName
                .replace(Regex("\\(.*?\\)"), "")
                .replace(Regex("\\s+"), " ")
                .trim()
            contactDao.updateNickname(contactId, cleanName)
        } else {
            contactDao.updateNickname(contactId, "")
        }
    }

    /**
     * Applies quick format: prepend Hey/Hello/Hai to nickname.
     */
    suspend fun quickFormatAddGreeting(contactId: Long, greeting: String = "Hai") {
        val contact = contactDao.getAll().find { it.id == contactId } ?: return
        val nick = contact.customNickname.ifEmpty { extractSmartNickname(contact.rawName) }
        val targetNick = if (nick.isNotBlank()) "$greeting $nick" else "$greeting Kak"
        contactDao.updateNickname(contactId, targetNick)
    }

    companion object {
        fun parseTagsJson(json: String): List<String> {
            return try {
                Json.decodeFromString<List<String>>(json)
            } catch (_: Exception) {
                emptyList()
            }
        }

        fun extractInitials(name: String): String {
            val lettersOnly = name.filter { it.isLetter() }
            if (lettersOnly.length < 2) {
                return "WA"
            }
            val parts = name.replace(Regex("\\(.*?\\)"), "").trim().split(" ").filter { it.isNotBlank() }
            val letterParts = parts.filter { part -> part.any { it.isLetter() } }
            return when {
                letterParts.size >= 2 -> "${letterParts[0].first { it.isLetter() }.uppercase()}${letterParts[1].first { it.isLetter() }.uppercase()}"
                letterParts.size == 1 -> letterParts[0].filter { it.isLetter() }.take(2).uppercase()
                else -> "WA"
            }
        }

        fun extractSmartNickname(rawName: String): String {
            val lettersOnly = rawName.filter { it.isLetter() }
            if (lettersOnly.length < 2) {
                // If the name is basically a phone number or symbols without a real human name, don't treat "+62" as nickname
                return ""
            }

            val cleaned = rawName
                .replace(Regex("\\(.*?\\)"), "") // Remove parenthetical notes
                .replace(Regex("^(dr\\.|Drs\\.|Ir\\.|Prof\\.?)\\s+", RegexOption.IGNORE_CASE), "") // Remove titles
                .trim()

            val firstWord = cleaned.split(" ").firstOrNull()?.trim() ?: ""
            val firstWordLetters = firstWord.filter { it.isLetter() }
            if (firstWordLetters.length < 2) {
                return ""
            }
            return firstWord
        }
    }
}
