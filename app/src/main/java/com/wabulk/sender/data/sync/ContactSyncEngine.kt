package com.wabulk.sender.data.sync

import android.content.ContentResolver
import android.provider.ContactsContract
import com.wabulk.sender.data.local.db.ContactDao
import com.wabulk.sender.data.local.db.ContactEntity
import com.wabulk.sender.data.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class SyncResult(
    val newContacts: Int,
    val updatedContacts: Int,
    val totalPhonebook: Int
)

class ContactSyncEngine(
    private val contentResolver: ContentResolver,
    private val contactDao: ContactDao
) {
    /**
     * Non-destructive sync: reads native phonebook contacts and upserts into Room.
     * GUARANTEES:
     * - Never writes to ContactsContract (phonebook is read-only)
     * - Preserves existing custom nicknames (does not overwrite non-empty nicknames)
     * - Preserves existing tags
     * - Preserves selection state
     * - Deduplicates by normalized phone number
     */
    suspend fun syncFromPhonebook(): SyncResult = withContext(Dispatchers.IO) {
        val phonebookContacts = readPhonebook()
        var newCount = 0
        var updatedCount = 0

        for (pbContact in phonebookContacts) {
            val normalized = normalizePhone(pbContact.phone)
            val existing = contactDao.findByPhone(normalized)

            if (existing == null) {
                // New contact — insert with smart nickname
                contactDao.upsert(
                    ContactEntity(
                        systemLookupKey = pbContact.lookupKey,
                        rawName = pbContact.displayName,
                        phoneNumber = normalized,
                        customNickname = ContactRepository.extractSmartNickname(pbContact.displayName),
                        tagsJson = "[]",
                        isSelected = false,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                )
                newCount++
            } else {
                // Existing contact — update rawName and lookupKey but PRESERVE nickname, tags, selection
                if (existing.rawName != pbContact.displayName || existing.systemLookupKey != pbContact.lookupKey) {
                    contactDao.update(
                        existing.copy(
                            rawName = pbContact.displayName,
                            systemLookupKey = pbContact.lookupKey,
                            lastSyncedAt = System.currentTimeMillis()
                            // customNickname, tagsJson, isSelected are PRESERVED
                        )
                    )
                    updatedCount++
                }
            }
        }

        SyncResult(
            newContacts = newCount,
            updatedContacts = updatedCount,
            totalPhonebook = phonebookContacts.size
        )
    }

    private data class PhonebookContact(
        val displayName: String,
        val phone: String,
        val lookupKey: String
    )

    private fun readPhonebook(): List<PhonebookContact> {
        val contacts = mutableMapOf<String, PhonebookContact>() // Deduplicate by normalized phone

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
        )

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val lookupIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)

            while (it.moveToNext()) {
                val name = it.getString(nameIdx) ?: continue
                val phone = it.getString(phoneIdx) ?: continue
                val lookupKey = it.getString(lookupIdx) ?: ""

                val normalized = normalizePhone(phone)
                if (normalized.isNotBlank() && normalized !in contacts) {
                    contacts[normalized] = PhonebookContact(
                        displayName = name,
                        phone = normalized,
                        lookupKey = lookupKey
                    )
                }
            }
        }

        return contacts.values.toList()
    }

    companion object {
        /**
         * Normalizes phone numbers by stripping whitespace, dashes, and parentheses.
         * Keeps the + prefix for international numbers.
         */
        fun normalizePhone(phone: String): String {
            return phone.replace(Regex("[\\s\\-()]"), "").trim()
        }
    }
}
