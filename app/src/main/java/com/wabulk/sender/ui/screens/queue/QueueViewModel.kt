package com.wabulk.sender.ui.screens.queue

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wabulk.sender.engine.queue.DispatchQueueManager
import com.wabulk.sender.engine.queue.model.QueueItem
import com.wabulk.sender.engine.queue.model.QueueTaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class QueueViewModel(application: Application) : AndroidViewModel(application) {

    private val queueManager = DispatchQueueManager.getInstance()

    val isPaused: StateFlow<Boolean> = queueManager.isPaused
    val isRunning: StateFlow<Boolean> = queueManager.isRunning
    val jitterCountdown: StateFlow<Int> = queueManager.jitterCountdown
    val currentTypingContact: StateFlow<String?> = queueManager.currentTypingContact

    private val _selectedFilter = MutableStateFlow(QueueStatusFilter.ALL)
    val selectedFilter: StateFlow<QueueStatusFilter> = _selectedFilter.asStateFlow()

    val filteredItems: StateFlow<List<QueueItem>> = combine(
        queueManager.queueItems,
        _selectedFilter
    ) { items, filter ->
        when (filter) {
            QueueStatusFilter.ALL -> items
            QueueStatusFilter.ACTIVE -> items.filter { it.status == QueueTaskStatus.TYPING }
            QueueStatusFilter.DELIVERED -> items.filter { it.status == QueueTaskStatus.DELIVERED }
            QueueStatusFilter.PENDING -> items.filter { it.status == QueueTaskStatus.PENDING || it.status == QueueTaskStatus.PAUSED }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCount: StateFlow<Int> = combine(queueManager.queueItems) { items ->
        items.first().size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), queueManager.totalCount)

    val sentCount: StateFlow<Int> = combine(queueManager.queueItems) { items ->
        items.first().count { it.status == QueueTaskStatus.DELIVERED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), queueManager.sentCount)

    val percentComplete: StateFlow<Int> = combine(queueManager.queueItems) { items ->
        val list = items.first()
        if (list.isEmpty()) 0 else ((list.count { it.status == QueueTaskStatus.DELIVERED }.toFloat() / list.size.toFloat()) * 100).toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), queueManager.percentComplete)

    val remainingCount: StateFlow<Int> = combine(queueManager.queueItems) { items ->
        items.first().count { it.status == QueueTaskStatus.PENDING || it.status == QueueTaskStatus.TYPING }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), queueManager.remainingCount)

    fun setFilter(filter: QueueStatusFilter) {
        _selectedFilter.value = filter
    }

    fun togglePause() {
        if (isPaused.value) {
            queueManager.resumeDispatch()
        } else {
            queueManager.pauseDispatch()
        }
    }

    fun stopBlast() {
        queueManager.stopDispatch(getApplication())
    }

    fun startQueue() {
        queueManager.startDispatch(getApplication())
    }
}
