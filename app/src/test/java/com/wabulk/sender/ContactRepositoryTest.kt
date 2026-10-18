package com.wabulk.sender

import com.wabulk.sender.data.local.db.ContactDao
import com.wabulk.sender.data.local.db.ContactEntity
import com.wabulk.sender.data.local.db.TagDao
import com.wabulk.sender.data.local.db.TagEntity
import com.wabulk.sender.data.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeContactDao : ContactDao {
    private val contactsList = mutableListOf<ContactEntity>()
    private val flow = MutableStateFlow<List<ContactEntity>>(emptyList())

    private fun updateFlow() {
        flow.value = contactsList.sortedBy { it.rawName }.toList()
    }

    override fun observeAll(): Flow<List<ContactEntity>> = flow

    override fun search(query: String): Flow<List<ContactEntity>> = flow.map { list ->
        list.filter {
            it.rawName.contains(query, ignoreCase = true) ||
                    it.phoneNumber.contains(query, ignoreCase = true) ||
                    it.customNickname.contains(query, ignoreCase = true)
        }
    }

    override fun searchSelected(query: String): Flow<List<ContactEntity>> = flow.map { list ->
        list.filter {
            it.isSelected && (
                it.rawName.contains(query, ignoreCase = true) ||
                        it.phoneNumber.contains(query, ignoreCase = true) ||
                        it.customNickname.contains(query, ignoreCase = true)
            )
        }
    }

    override fun filterByTag(tag: String): Flow<List<ContactEntity>> = flow.map { list ->
        list.filter { it.tagsJson.contains(tag) }
    }

    override fun searchWithTag(query: String, tag: String): Flow<List<ContactEntity>> = flow.map { list ->
        list.filter {
            it.tagsJson.contains(tag) && (
                    it.rawName.contains(query, ignoreCase = true) ||
                            it.phoneNumber.contains(query, ignoreCase = true) ||
                            it.customNickname.contains(query, ignoreCase = true)
                    )
        }
    }

    override suspend fun findByPhone(phone: String): ContactEntity? {
        return contactsList.find { it.phoneNumber == phone }
    }

    override fun observeTotalCount(): Flow<Int> = flow.map { it.size }

    override fun observeSelectedCount(): Flow<Int> = flow.map { list -> list.count { it.isSelected } }

    override fun observeNicknameCount(): Flow<Int> = flow.map { list -> list.count { it.customNickname.isNotEmpty() } }

    override suspend fun upsert(contact: ContactEntity): Long {
        val existingIndex = contactsList.indexOfFirst { it.id == contact.id || it.phoneNumber == contact.phoneNumber }
        return if (existingIndex >= 0) {
            contactsList[existingIndex] = contact
            updateFlow()
            contact.id
        } else {
            val newId = (contactsList.maxOfOrNull { it.id } ?: 0L) + 1L
            val newContact = contact.copy(id = newId)
            contactsList.add(newContact)
            updateFlow()
            newId
        }
    }

    override suspend fun insertAll(contacts: List<ContactEntity>): List<Long> {
        return contacts.map { upsert(it) }
    }

    override suspend fun update(contact: ContactEntity) {
        val index = contactsList.indexOfFirst { it.id == contact.id }
        if (index >= 0) {
            contactsList[index] = contact
            updateFlow()
        }
    }

    override suspend fun updateNickname(contactId: Long, nickname: String) {
        val index = contactsList.indexOfFirst { it.id == contactId }
        if (index >= 0) {
            contactsList[index] = contactsList[index].copy(customNickname = nickname)
            updateFlow()
        }
    }

    override suspend fun updateSelection(contactId: Long, selected: Boolean) {
        val index = contactsList.indexOfFirst { it.id == contactId }
        if (index >= 0) {
            contactsList[index] = contactsList[index].copy(isSelected = selected)
            updateFlow()
        }
    }

    override suspend fun selectAllInTag(tag: String, selected: Boolean) {
        for (i in contactsList.indices) {
            if (contactsList[i].tagsJson.contains(tag)) {
                contactsList[i] = contactsList[i].copy(isSelected = selected)
            }
        }
        updateFlow()
    }

    override suspend fun updateTags(contactId: Long, tagsJson: String) {
        val index = contactsList.indexOfFirst { it.id == contactId }
        if (index >= 0) {
            contactsList[index] = contactsList[index].copy(tagsJson = tagsJson)
            updateFlow()
        }
    }

    override fun observeSelected(): Flow<List<ContactEntity>> = flow.map { list -> list.filter { it.isSelected } }

    override suspend fun getSelected(): List<ContactEntity> {
        return contactsList.filter { it.isSelected }
    }

    override suspend fun delete(contactId: Long) {
        contactsList.removeAll { it.id == contactId }
        updateFlow()
    }

    override suspend fun getAll(): List<ContactEntity> {
        return contactsList.toList()
    }

    override suspend fun selectAll(selected: Boolean) {
        for (i in contactsList.indices) {
            contactsList[i] = contactsList[i].copy(isSelected = selected)
        }
        updateFlow()
    }

    override suspend fun sanitizeInvalidNicknames() {
        for (i in contactsList.indices) {
            val nick = contactsList[i].customNickname
            if (nick.startsWith("+") || !nick.any { it.isLetter() }) {
                contactsList[i] = contactsList[i].copy(customNickname = "")
            }
        }
        updateFlow()
    }
}

class FakeTagDao : TagDao {
    private val tagsList = mutableListOf<TagEntity>()
    private val flow = MutableStateFlow<List<TagEntity>>(emptyList())

    private fun updateFlow() {
        flow.value = tagsList.sortedBy { it.name }.toList()
    }

    override fun observeAll(): Flow<List<TagEntity>> = flow

    override suspend fun insert(tag: TagEntity): Long {
        val existing = tagsList.find { it.name == tag.name }
        if (existing != null) return existing.id
        val newId = (tagsList.maxOfOrNull { it.id } ?: 0L) + 1L
        tagsList.add(tag.copy(id = newId))
        updateFlow()
        return newId
    }

    override suspend fun delete(tagId: Long) {
        tagsList.removeAll { it.id == tagId }
        updateFlow()
    }

    override suspend fun findByName(name: String): TagEntity? {
        return tagsList.find { it.name == name }
    }
}

class ContactRepositoryTest {

    private lateinit var contactDao: FakeContactDao
    private lateinit var tagDao: FakeTagDao
    private lateinit var repository: ContactRepository

    @Before
    fun setUp() {
        contactDao = FakeContactDao()
        tagDao = FakeTagDao()
        repository = ContactRepository(contactDao, tagDao)
    }

    @Test
    fun `upsert and retrieve contacts reactively`() = runTest {
        val contact = ContactEntity(
            rawName = "Sarah Jenkins (Lead)",
            phoneNumber = "+15553928104",
            customNickname = "Sarah",
            tagsJson = "[\"Bisnis\"]",
            isSelected = true
        )

        val id = repository.upsertContact(contact)
        assertTrue(id > 0)

        val all = repository.allContacts.first()
        assertEquals(1, all.size)
        assertEquals("Sarah", all[0].customNickname)
        assertEquals(1, repository.totalCount.first())
        assertEquals(1, repository.selectedCount.first())
        assertEquals(1, repository.nicknameCount.first())
    }

    @Test
    fun `search contacts matches name, phone, or nickname`() = runTest {
        repository.upsertContact(
            ContactEntity(rawName = "David Miller", phoneNumber = "+15557149920", customNickname = "Dave")
        )
        repository.upsertContact(
            ContactEntity(rawName = "Emre Yilmaz", phoneNumber = "+905551234567", customNickname = "Emre")
        )

        val searchByName = repository.searchContacts("David").first()
        assertEquals(1, searchByName.size)
        assertEquals("David Miller", searchByName[0].rawName)

        val searchByPhone = repository.searchContacts("90555").first()
        assertEquals(1, searchByPhone.size)
        assertEquals("Emre Yilmaz", searchByPhone[0].rawName)

        val searchByNick = repository.searchContacts("Dave").first()
        assertEquals(1, searchByNick.size)
        assertEquals("David Miller", searchByNick[0].rawName)
    }

    @Test
    fun `tag filtering and tag management`() = runTest {
        val c1Id = repository.upsertContact(
            ContactEntity(rawName = "Contact 1", phoneNumber = "111", tagsJson = "[\"Keluarga\"]")
        )
        val c2Id = repository.upsertContact(
            ContactEntity(rawName = "Contact 2", phoneNumber = "222", tagsJson = "[\"Bisnis\"]")
        )

        val keluarga = repository.filterByTag("Keluarga").first()
        assertEquals(1, keluarga.size)
        assertEquals("Contact 1", keluarga[0].rawName)

        repository.addTagToContact(c1Id, "VIP")
        val updated = repository.filterByTag("VIP").first()
        assertEquals(1, updated.size)
        assertEquals("Contact 1", updated[0].rawName)

        repository.removeTagFromContact(c1Id, "VIP")
        val afterRemove = repository.filterByTag("VIP").first()
        assertEquals(0, afterRemove.size)
    }

    @Test
    fun `selection toggle and select all in tag`() = runTest {
        val id1 = repository.upsertContact(
            ContactEntity(rawName = "Alice", phoneNumber = "101", tagsJson = "[\"Team\"]", isSelected = false)
        )
        val id2 = repository.upsertContact(
            ContactEntity(rawName = "Bob", phoneNumber = "102", tagsJson = "[\"Team\"]", isSelected = false)
        )

        assertEquals(0, repository.selectedCount.first())

        repository.toggleSelection(id1, true)
        assertEquals(1, repository.selectedCount.first())

        repository.selectAllInTag("Team", true)
        assertEquals(2, repository.selectedCount.first())

        val selected = repository.getSelectedContacts()
        assertEquals(2, selected.size)
    }

    @Test
    fun `quickFormatFirstName extracts first name from raw name`() = runTest {
        val id = repository.upsertContact(
            ContactEntity(rawName = "Sarah Jenkins (Lead)", phoneNumber = "301", customNickname = "")
        )

        repository.quickFormatFirstName(id)
        val contact = repository.allContacts.first().find { it.id == id }!!
        assertEquals("Sarah", contact.customNickname)
    }

    @Test
    fun `quickFormatRemoveNotes strips parenthetical notes`() = runTest {
        val id = repository.upsertContact(
            ContactEntity(rawName = "David M. Miller (Tech Consultant)", phoneNumber = "302", customNickname = "")
        )

        repository.quickFormatRemoveNotes(id)
        val contact = repository.allContacts.first().find { it.id == id }!!
        assertEquals("David M. Miller", contact.customNickname)
    }

    @Test
    fun `quickFormatAddGreeting prepends greeting to nickname`() = runTest {
        val id = repository.upsertContact(
            ContactEntity(rawName = "Elena Rostova", phoneNumber = "303", customNickname = "Elena")
        )

        repository.quickFormatAddGreeting(id, "Halo")
        val contact = repository.allContacts.first().find { it.id == id }!!
        assertEquals("Halo Elena", contact.customNickname)
    }

    @Test
    fun `tag entity creation and deletion`() = runTest {
        val tagId = repository.createTag("Prioritas")
        assertTrue(tagId > 0)

        val tags = repository.allTags.first()
        assertEquals(1, tags.size)
        assertEquals("Prioritas", tags[0].name)

        repository.deleteTag(tagId)
        val tagsAfter = repository.allTags.first()
        assertEquals(0, tagsAfter.size)
    }
}
