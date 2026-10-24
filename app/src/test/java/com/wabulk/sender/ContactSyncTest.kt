package com.wabulk.sender

import com.wabulk.sender.data.repository.ContactRepository
import com.wabulk.sender.data.sync.ContactSyncEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactSyncTest {

    @Test
    fun `normalizePhone strips formatting`() {
        assertEquals("+15553928104", ContactSyncEngine.normalizePhone("+1 (555) 392-8104"))
        assertEquals("+905551234567", ContactSyncEngine.normalizePhone("+90 555 123 4567"))
        assertEquals("08123456789", ContactSyncEngine.normalizePhone("0812 345 6789"))
    }

    @Test
    fun `extractSmartNickname removes parenthetical notes`() {
        assertEquals("Sarah", ContactRepository.extractSmartNickname("Sarah Jenkins (Apex Fitness Sales Lead)"))
        assertEquals("David", ContactRepository.extractSmartNickname("David M. Miller (Tech Consultant)"))
        assertEquals("Elena", ContactRepository.extractSmartNickname("Elena Rostova (Senior Designer)"))
    }

    @Test
    fun `extractSmartNickname removes title prefixes`() {
        assertEquals("Farhan", ContactRepository.extractSmartNickname("dr. Farhan Rizki"))
        assertEquals("Ahmad", ContactRepository.extractSmartNickname("Prof. Ahmad Fauzi"))
    }

    @Test
    fun `extractSmartNickname returns empty for phone number only names`() {
        assertEquals("", ContactRepository.extractSmartNickname("+62 812-1414-935"))
        assertEquals("", ContactRepository.extractSmartNickname("+628123456789"))
        assertEquals("", ContactRepository.extractSmartNickname("0812 345 6789"))
        assertEquals("", ContactRepository.extractSmartNickname("+1 (555) 019-2834"))
    }

    @Test
    fun `extractInitials generates correct initials`() {
        assertEquals("SJ", ContactRepository.extractInitials("Sarah Jenkins (Apex Fitness Sales Lead)"))
        assertEquals("DM", ContactRepository.extractInitials("David M. Miller"))
        assertEquals("EL", ContactRepository.extractInitials("Elena"))
    }

    @Test
    fun `parseTagsJson handles valid and invalid JSON`() {
        assertEquals(listOf("Keluarga", "Bisnis"), ContactRepository.parseTagsJson("[\"Keluarga\",\"Bisnis\"]"))
        assertEquals(emptyList<String>(), ContactRepository.parseTagsJson(""))
        assertEquals(emptyList<String>(), ContactRepository.parseTagsJson("invalid"))
        assertEquals(emptyList<String>(), ContactRepository.parseTagsJson("[]"))
    }

    @Test
    fun `non-destructive guarantee - update preserves nickname, tags, and selection`() {
        val original = com.wabulk.sender.data.local.db.ContactEntity(
            id = 42,
            systemLookupKey = "lookup_old",
            rawName = "David Miller",
            phoneNumber = "+15557149920",
            customNickname = "Dave Boss",
            tagsJson = "[\"VIP\",\"Bisnis\"]",
            isSelected = true
        )

        // Incoming phonebook change with updated rawName
        val updatedRawName = "David M. Miller (Tech Consultant)"
        val updatedLookupKey = "lookup_new"

        val updated = original.copy(
            rawName = updatedRawName,
            systemLookupKey = updatedLookupKey,
            lastSyncedAt = 999999L
        )

        // Verify customNickname, tagsJson, and isSelected remain exactly intact
        assertEquals("Dave Boss", updated.customNickname)
        assertEquals("[\"VIP\",\"Bisnis\"]", updated.tagsJson)
        assertTrue(updated.isSelected)
        assertEquals(updatedRawName, updated.rawName)
        assertEquals(updatedLookupKey, updated.systemLookupKey)
    }

    @Test
    fun `international phone numbers preserve plus prefix`() {
        assertEquals("+6281234567890", ContactSyncEngine.normalizePhone("+62 812-3456-7890"))
        assertEquals("+905551234567", ContactSyncEngine.normalizePhone("+90 (555) 123-4567"))
        assertEquals("+447911123456", ContactSyncEngine.normalizePhone("+44 7911 123456"))
    }

    @Test
    fun `staleContactIds drops phonebook contacts deleted from phone but keeps CSV imports`() {
        fun c(id: Long, phone: String, key: String) = com.wabulk.sender.data.local.db.ContactEntity(
            id = id, systemLookupKey = key, rawName = "x", phoneNumber = phone
        )
        val stored = listOf(
            c(1, "+111", "k1"),  // still in phonebook
            c(2, "+222", "k2"),  // deleted from phonebook
            c(3, "+333", "")     // CSV import, never in phonebook
        )
        assertEquals(listOf(2L), ContactSyncEngine.staleContactIds(stored, setOf("+111")))
    }
}
