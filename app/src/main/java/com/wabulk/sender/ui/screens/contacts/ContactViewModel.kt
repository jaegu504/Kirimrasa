package com.wabulk.sender.ui.screens.contacts

import com.wabulk.sender.data.locale.LocalizationManager
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wabulk.sender.data.io.CsvContactParser
import com.wabulk.sender.data.io.CsvParseResult
import com.wabulk.sender.data.local.db.AppDatabase
import com.wabulk.sender.data.local.db.ContactEntity
import com.wabulk.sender.data.local.db.TagEntity
import com.wabulk.sender.data.repository.ContactRepository
import com.wabulk.sender.data.sync.ContactSyncEngine
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = ContactRepository(db.contactDao(), db.tagDao())
    private val syncEngine = ContactSyncEngine(application.contentResolver, db.contactDao())

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active tag filter (null means "All")
    private val _activeTag = MutableStateFlow<String?>(null)
    val activeTag: StateFlow<String?> = _activeTag.asStateFlow()

    // Sync state
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncResult = MutableStateFlow<String?>(null)
    val lastSyncResult: StateFlow<String?> = _lastSyncResult.asStateFlow()

    // Telemetry counters
    val totalCount: StateFlow<Int> = repository.totalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val selectedCount: StateFlow<Int> = repository.selectedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val nicknameCount: StateFlow<Int> = repository.nicknameCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Tags
    val tags: StateFlow<List<TagEntity>> = repository.allTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered contacts based on search query + tag filter
    val contacts: StateFlow<List<ContactEntity>> = combine(
        _searchQuery.debounce(300),
        _activeTag
    ) { query, tag ->
        Pair(query, tag)
    }.flatMapLatest { (query, tag) ->
        when {
            tag == SELECTED_FILTER_TAG && query.isBlank() -> repository.observeSelectedContacts()
            tag == SELECTED_FILTER_TAG && query.isNotBlank() -> repository.searchSelected(query)
            query.isBlank() && tag == null -> repository.allContacts
            query.isBlank() && tag != null -> repository.filterByTag(tag)
            query.isNotBlank() && tag == null -> repository.searchContacts(query)
            else -> repository.searchWithTag(query, tag!!)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTagFilterChange(tag: String?) {
        _activeTag.value = tag
    }

    fun toggleContactSelection(contactId: Long, selected: Boolean) {
        viewModelScope.launch {
            repository.toggleSelection(contactId, selected)
        }
    }

    fun selectAllInTag(selected: Boolean) {
        val tag = _activeTag.value ?: return
        viewModelScope.launch {
            if (tag == SELECTED_FILTER_TAG) {
                repository.selectAll(selected)
            } else {
                repository.selectAllInTag(tag, selected)
            }
        }
    }

    fun selectAllContacts(selected: Boolean) {
        viewModelScope.launch {
            if (_activeTag.value != null && _activeTag.value != SELECTED_FILTER_TAG) {
                repository.selectAllInTag(_activeTag.value!!, selected)
            } else {
                repository.selectAll(selected)
            }
        }
    }

    fun addTagToSelectedContacts(tagName: String) {
        viewModelScope.launch {
            val selected = repository.getSelectedContacts()
            if (selected.isEmpty()) return@launch
            repository.createTag(tagName)
            repository.addTagToContacts(selected.map { it.id }, tagName)
        }
    }

    suspend fun getTargetContactsForCampaign(): List<ContactEntity> = withContext(Dispatchers.IO) {
        val selected = repository.getSelectedContacts()
        if (selected.isNotEmpty()) {
            selected
        } else {
            repository.getAllContactsList()
        }
    }

    fun updateNickname(contactId: Long, nickname: String) {
        viewModelScope.launch {
            repository.updateNickname(contactId, nickname)
        }
    }

    fun quickFormatFirstName(contactId: Long) {
        viewModelScope.launch {
            repository.quickFormatFirstName(contactId)
        }
    }

    fun quickFormatRemoveNotes(contactId: Long) {
        viewModelScope.launch {
            repository.quickFormatRemoveNotes(contactId)
        }
    }

    fun quickFormatAddGreeting(contactId: Long) {
        viewModelScope.launch {
            repository.quickFormatAddGreeting(contactId)
        }
    }

    fun createTag(name: String) {
        viewModelScope.launch {
            repository.createTag(name)
        }
    }

    fun deleteTag(tagId: Long) {
        viewModelScope.launch {
            repository.deleteTag(tagId)
        }
    }

    fun addTagToContact(contactId: Long, tagName: String) {
        viewModelScope.launch {
            repository.addTagToContact(contactId, tagName)
        }
    }

    fun removeTagFromContact(contactId: Long, tagName: String) {
        viewModelScope.launch {
            repository.removeTagFromContact(contactId, tagName)
        }
    }

    fun syncContacts() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                val result = syncEngine.syncFromPhonebook()
                _lastSyncResult.value = LocalizationManager.activeStrings.syncResult(result.newContacts, result.updatedContacts)
            } catch (e: SecurityException) {
                _lastSyncResult.value = LocalizationManager.activeStrings.contactsPermissionNeeded
            } catch (e: Exception) {
                _lastSyncResult.value = LocalizationManager.activeStrings.failedWith(e.message.orEmpty())
            } finally {
                _isSyncing.value = false
            }
        }
    }

    suspend fun importContactsFromCsv(csvText: String): CsvParseResult = withContext(Dispatchers.IO) {
        val result = CsvContactParser.parseCsv(csvText)
        if (result is CsvParseResult.Success) {
            val entities = result.contacts
                .distinctBy { it.phoneNumber }
                .map { it.toContactEntity() }
            db.contactDao().insertAll(entities)

            result.contacts.flatMap { it.tags }.distinct().forEach { tag ->
                if (tag.isNotBlank()) {
                    repository.createTag(tag)
                }
            }
            _lastSyncResult.value = LocalizationManager.activeStrings.csvImported(result.contacts.size)
        }
        result
    }

    suspend fun importContactsFromStream(inputStream: InputStream): CsvParseResult = withContext(Dispatchers.IO) {
        val result = CsvContactParser.parseCsv(inputStream)
        if (result is CsvParseResult.Success) {
            val entities = result.contacts
                .distinctBy { it.phoneNumber }
                .map { it.toContactEntity() }
            db.contactDao().insertAll(entities)

            result.contacts.flatMap { it.tags }.distinct().forEach { tag ->
                if (tag.isNotBlank()) {
                    repository.createTag(tag)
                }
            }
            _lastSyncResult.value = LocalizationManager.activeStrings.csvImported(result.contacts.size)
        }
        result
    }

    suspend fun importSampleCsv(): CsvParseResult = withContext(Dispatchers.IO) {
        val sampleCsv = """
            Nama,No Telepon,Panggilan,Perusahaan,Custom_1,Tag
            Budi Santoso,+6281234567801,Budi,PT Maju Jaya,DISKON50,VIP
            Siti Nurhaliza,+6281234567802,Siti,Karya Abadi,FREEONGKIR,Bisnis
            Ahmad Fauzi,+6281234567803,Ahmad,Fauzi Tech,CASHBACK10,Klien
            Dewi Lestari,+6281234567804,Dewi,Bintang Mulia,MEMBERVIP,Keluarga
            Eko Prasetyo,+6281234567805,Eko,Prasetyo Store,HEMAT20,Teman
        """.trimIndent()
        importContactsFromCsv(sampleCsv)
    }

    // Seed default tags and sanitize legacy bad nicknames if any
    fun ensureDefaultTags() {
        viewModelScope.launch {
            try {
                repository.sanitizeNicknames()
            } catch (_: Exception) {}
            val existing = tags.value
            if (existing.isEmpty()) {
                repository.createTag("Keluarga")
                repository.createTag("Teman")
                repository.createTag("Bisnis")
            }
        }
    }

    companion object {
        const val SELECTED_FILTER_TAG = "__SELECTED__"
    }
}
