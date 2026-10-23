package com.wabulk.sender.ui.screens.queue

import com.wabulk.sender.data.locale.LocalizationManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import com.wabulk.sender.data.io.CampaignCsvExporter
import com.wabulk.sender.data.local.db.AppDatabase
import com.wabulk.sender.data.local.db.CampaignEntity
import com.wabulk.sender.data.local.db.CampaignItemEntity
import com.wabulk.sender.engine.queue.DispatchQueueManager
import com.wabulk.sender.engine.queue.model.QueueItem
import com.wabulk.sender.engine.queue.model.QueueTaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QueueViewModel(application: Application) : AndroidViewModel(application) {

    private val queueManager = DispatchQueueManager.getInstance()

    val isPaused: StateFlow<Boolean> = queueManager.isPaused
    val isRunning: StateFlow<Boolean> = queueManager.isRunning
    val jitterCountdown: StateFlow<Int> = queueManager.jitterCountdown
    val isCoolingDown: StateFlow<Boolean> = queueManager.isCoolingDown
    val currentTypingContact: StateFlow<String?> = queueManager.currentTypingContact
    val queueItems: StateFlow<List<QueueItem>> = queueManager.queueItems

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
            QueueStatusFilter.FAILED -> items.filter { it.status == QueueTaskStatus.FAILED }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCount: StateFlow<Int> = combine(queueManager.queueItems) { items ->
        items.first().size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), queueManager.totalCount)

    val sentCount: StateFlow<Int> = combine(queueManager.queueItems) { items ->
        items.first().count { it.status == QueueTaskStatus.DELIVERED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), queueManager.sentCount)

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

    fun exportCampaignReport(context: Context, onExportSuccess: ((Int) -> String)? = null) {
        viewModelScope.launch {
            try {
                val db = AppDatabase.getInstance(context)
                val campaignDao = db.campaignDao()

                val (campaign, itemsToExport, reportFile, csvContent) = withContext(Dispatchers.IO) {
                    val recentCampaigns = campaignDao.getRecentCampaigns(1)
                    val camp = recentCampaigns.firstOrNull() ?: CampaignEntity(
                        id = 1L,
                        title = LocalizationManager.activeStrings.campaignDefaultTitle,
                        totalRecipients = totalCount.value,
                        sentCount = sentCount.value,
                        failedCount = 0,
                        status = if (sentCount.value == totalCount.value && totalCount.value > 0) "COMPLETED" else "IN_PROGRESS",
                        createdAt = System.currentTimeMillis()
                    )

                    val dbItems = campaignDao.getItemsForCampaign(camp.id)
                    val items = if (dbItems.isNotEmpty()) {
                        dbItems
                    } else {
                        val currentQueue = queueManager.queueItems.value
                        currentQueue.mapIndexed { index, qItem ->
                            val msgText = qItem.bubbleTexts.joinToString("\n\n").ifBlank { "${LocalizationManager.activeStrings.greetingWord} ${qItem.recipientName}" }
                            CampaignItemEntity(
                                id = (index + 1).toLong(),
                                campaignId = camp.id,
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
                                errorMessage = qItem.errorMessage ?: if (qItem.status == QueueTaskStatus.FAILED) LocalizationManager.activeStrings.sendFailed else null,
                                sentAt = qItem.sentTimestamp ?: System.currentTimeMillis()
                            )
                        }
                    }

                    val csv = CampaignCsvExporter.generateCsv(camp, items)

                    val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
                    val file = File(reportsDir, "campaign_report_${camp.id}.csv")
                    file.writeText(csv, Charsets.UTF_8)

                    listOf(camp, items, file, csv)
                }

                @Suppress("UNCHECKED_CAST")
                val typedCamp = campaign as CampaignEntity
                @Suppress("UNCHECKED_CAST")
                val typedItems = itemsToExport as List<CampaignItemEntity>
                val typedFile = reportFile as File
                val typedCsv = csvContent as String

                var reportUri: android.net.Uri? = null
                try {
                    reportUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        typedFile
                    )
                } catch (_: Exception) {
                    reportUri = null
                }

                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_SUBJECT, LocalizationManager.activeStrings.reportSubject(typedCamp.title))
                    putExtra(Intent.EXTRA_TEXT, typedCsv)
                    if (reportUri != null) {
                        putExtra(Intent.EXTRA_STREAM, reportUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                val chooser = Intent.createChooser(sendIntent, LocalizationManager.activeStrings.reportExportChooser).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    if (reportUri != null) {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }

                if (reportUri != null) {
                    val resInfoList = context.packageManager.queryIntentActivities(
                        chooser,
                        android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
                    )
                    for (resolveInfo in resInfoList) {
                        val packageName = resolveInfo.activityInfo.packageName
                        context.grantUriPermission(packageName, reportUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }

                context.startActivity(chooser)
                val successMsg = onExportSuccess?.invoke(typedItems.size)
                    ?: LocalizationManager.activeStrings.reportExported(typedItems.size)
                Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, LocalizationManager.activeStrings.reportExportFailed(e.message.orEmpty()), Toast.LENGTH_LONG).show()
            }
        }
    }
}
