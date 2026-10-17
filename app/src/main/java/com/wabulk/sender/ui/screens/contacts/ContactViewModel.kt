package com.wabulk.sender.ui.screens.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wabulk.sender.data.local.db.AppDatabase
import com.wabulk.sender.data.local.db.ContactEntity
import com.wabulk.sender.data.local.db.TagEntity
import com.wabulk.sender.data.repository.ContactRepository
import com.wabulk.sender.data.sync.ContactSyncEngine
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
            repository.selectAllInTag(tag, selected)
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
                _lastSyncResult.value = "+${result.newContacts} baru, ${result.updatedContacts} diperbarui"
            } catch (e: SecurityException) {
                _lastSyncResult.value = "Izin kontak diperlukan"
            } catch (e: Exception) {
                _lastSyncResult.value = "Gagal: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    // Seed default tags if empty
    fun ensureDefaultTags() {
        viewModelScope.launch {
            val existing = tags.value
            if (existing.isEmpty()) {
                repository.createTag("Keluarga")
                repository.createTag("Teman")
                repository.createTag("Bisnis")
            }
        }
    }
}
