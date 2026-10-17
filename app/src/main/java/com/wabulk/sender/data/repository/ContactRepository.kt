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

    /**
     * Applies quick format: extract first name from rawName as nickname.
     */
    suspend fun quickFormatFirstName(contactId: Long) {
        val contact = contactDao.getAll().find { it.id == contactId } ?: return
        val firstName = contact.rawName
            .replace(Regex("\\(.*?\\)"), "") // Remove parenthetical notes
            .trim()
            .split(" ")
            .firstOrNull()
            ?.trim() ?: return
        contactDao.updateNickname(contactId, firstName)
    }

    /**
     * Applies quick format: remove everything in parentheses and clean up name for nickname.
     */
    suspend fun quickFormatRemoveNotes(contactId: Long) {
        val contact = contactDao.getAll().find { it.id == contactId } ?: return
        val cleanName = contact.rawName
            .replace(Regex("\\(.*?\\)"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
        contactDao.updateNickname(contactId, cleanName)
    }

    /**
     * Applies quick format: prepend Hey/Hello to nickname.
     */
    suspend fun quickFormatAddGreeting(contactId: Long, greeting: String = "Hey") {
        val contact = contactDao.getAll().find { it.id == contactId } ?: return
        val nick = contact.customNickname.ifEmpty {
            contact.rawName.replace(Regex("\\(.*?\\)"), "").trim().split(" ").firstOrNull() ?: ""
        }
        contactDao.updateNickname(contactId, "$greeting $nick")
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
            val parts = name.replace(Regex("\\(.*?\\)"), "").trim().split(" ").filter { it.isNotBlank() }
            return when {
                parts.size >= 2 -> "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
                parts.size == 1 -> parts[0].take(2).uppercase()
                else -> "??"
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
