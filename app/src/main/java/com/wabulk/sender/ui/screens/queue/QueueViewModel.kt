package com.wabulk.sender.ui.screens.queue

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wabulk.sender.data.io.CampaignCsvExporter
import com.wabulk.sender.data.local.db.AppDatabase
import com.wabulk.sender.data.local.db.CampaignEntity
import com.wabulk.sender.data.local.db.CampaignItemEntity
import com.wabulk.sender.engine.queue.DispatchQueueManager
import com.wabulk.sender.engine.queue.model.QueueItem
import com.wabulk.sender.engine.queue.model.QueueTaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    fun exportCampaignReport(context: Context) {
        viewModelScope.launch {
            try {
                val db = AppDatabase.getInstance(context)
                val campaignDao = db.campaignDao()
                val recentCampaigns = campaignDao.getRecentCampaigns(1)
                val campaign = recentCampaigns.firstOrNull() ?: CampaignEntity(
                    id = 1L,
                    title = "Kampanye Pesan Massal WhatsApp",
                    totalRecipients = totalCount.value,
                    sentCount = sentCount.value,
                    failedCount = 0,
                    status = if (sentCount.value == totalCount.value && totalCount.value > 0) "COMPLETED" else "IN_PROGRESS",
                    createdAt = System.currentTimeMillis()
                )

                val dbItems = campaignDao.getItemsForCampaign(campaign.id)
                val itemsToExport = if (dbItems.isNotEmpty()) {
                    dbItems
                } else {
                    val currentQueue = queueManager.queueItems.value
                    currentQueue.mapIndexed { index, qItem ->
                        val msgText = qItem.bubbleTexts.joinToString("\n\n").ifBlank { "Halo ${qItem.recipientName}" }
                        CampaignItemEntity(
                            id = (index + 1).toLong(),
                            campaignId = campaign.id,
                            recipientPhone = qItem.phoneNumber,
                            recipientName = qItem.recipientName,
                            messageText = msgText,
                            status = when (qItem.status) {
                                QueueTaskStatus.DELIVERED -> "DELIVERED"
                                QueueTaskStatus.FAILED -> "FAILED"
                                QueueTaskStatus.PAUSED -> "PAUSED"
                                QueueTaskStatus.TYPING -> "TYPING"
                                QueueTaskStatus.PENDING -> "PENDING"
                            },
                            errorMessage = qItem.errorMessage ?: if (qItem.status == QueueTaskStatus.FAILED) "Gagal terkirim" else null,
                            sentAt = qItem.sentTimestamp ?: System.currentTimeMillis()
                        )
                    }
                }

                val csvContent = CampaignCsvExporter.generateCsv(campaign, itemsToExport)

                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_SUBJECT, "Laporan Kampanye ${campaign.title}")
                    putExtra(Intent.EXTRA_TEXT, csvContent)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val chooser = Intent.createChooser(sendIntent, "Ekspor Laporan CSV").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
                Toast.makeText(context, "Laporan CSV berhasil diekspor (${itemsToExport.size} data)", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal mengekspor laporan: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
