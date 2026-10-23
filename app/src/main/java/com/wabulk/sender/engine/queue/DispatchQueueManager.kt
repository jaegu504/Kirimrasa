package com.wabulk.sender.engine.queue

import com.wabulk.sender.domain.model.MediaAttachment
import com.wabulk.sender.data.locale.LocalizationManager
import android.content.Context
import android.content.Intent
import com.wabulk.sender.data.local.db.AppDatabase
import com.wabulk.sender.data.local.db.CampaignDao
import com.wabulk.sender.data.local.db.CampaignEntity
import com.wabulk.sender.data.local.db.CampaignItemEntity
import com.wabulk.sender.data.local.db.CampaignItemStatus
import com.wabulk.sender.data.local.db.CampaignStatus
import com.wabulk.sender.data.local.db.ContactEntity
import com.wabulk.sender.data.repository.ContactRepository
import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.domain.model.MessageBubble
import com.wabulk.sender.domain.spintax.SpintaxParser
import com.wabulk.sender.engine.WhatsAppEngine
import com.wabulk.sender.engine.WhatsAppSessionManager
import com.wabulk.sender.engine.queue.model.QueueItem
import com.wabulk.sender.engine.queue.model.QueueTaskStatus
import com.wabulk.sender.service.NotificationHelper
import com.wabulk.sender.service.WhatsAppForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class DispatchQueueManager private constructor(
    private var campaignDao: CampaignDao? = null
) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    private val _queueItems = MutableStateFlow<List<QueueItem>>(emptyList())
    val queueItems: StateFlow<List<QueueItem>> = _queueItems.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _jitterCountdown = MutableStateFlow(34)
    val jitterCountdown: StateFlow<Int> = _jitterCountdown.asStateFlow()

    private val _isCoolingDown = MutableStateFlow(false)
    /** True while the current countdown is the batch cooldown rather than the per-contact delay. */
    val isCoolingDown: StateFlow<Boolean> = _isCoolingDown.asStateFlow()

    private val _currentTypingContact = MutableStateFlow<String?>(null)
    val currentTypingContact: StateFlow<String?> = _currentTypingContact.asStateFlow()

    private val _campaignTitle = MutableStateFlow<String>(LocalizationManager.activeStrings.queueReadyTitle)
    val campaignTitle: StateFlow<String> = _campaignTitle.asStateFlow()

    private var currentCampaignId: Long? = null
    private var campaignStartTime: Long = 0L
    private val queueItemToDbId = ConcurrentHashMap<String, Long>()
    private var campaignInitJob: Job? = null

    var skipDelayForTesting: Boolean = false

    val totalCount: Int
        get() = _queueItems.value.size

    val sentCount: Int
        get() = _queueItems.value.count { it.status == QueueTaskStatus.DELIVERED }

    val remainingCount: Int
        get() = _queueItems.value.count { it.status == QueueTaskStatus.PENDING || it.status == QueueTaskStatus.TYPING }

    val percentComplete: Int
        get() = if (totalCount == 0) 0 else ((sentCount.toFloat() / totalCount.toFloat()) * 100).toInt()

    fun setCampaignDao(dao: CampaignDao?) {
        this.campaignDao = dao
    }

    fun getCampaignDao(): CampaignDao? = campaignDao

    fun enqueueBroadcast(
        items: List<QueueItem>,
        context: Context? = null,
        campaignTitle: String = "Broadcast Campaign",
        campaignDao: CampaignDao? = null
    ) {
        if (campaignDao != null) {
            this.campaignDao = campaignDao
        } else if (this.campaignDao == null && context != null) {
            try {
                this.campaignDao = AppDatabase.getInstance(context).campaignDao()
            } catch (_: Exception) {}
        }

        _queueItems.value = items
        _isPaused.value = false
        _campaignTitle.value = campaignTitle
        queueItemToDbId.clear()
        campaignStartTime = System.currentTimeMillis()

        val dao = this.campaignDao
        if (dao != null && items.isNotEmpty()) {
            campaignInitJob = scope.launch(Dispatchers.IO) {
                try {
                    val nodeName = when (items.firstOrNull()?.nodeUsed) {
                        AccountNode.PERSONAL -> "WA Personal"
                        AccountNode.BUSINESS -> "WA Business"
                        AccountNode.DUAL_SYNC -> "Dual Sync"
                        null -> "WA Personal"
                    }
                    val campaign = CampaignEntity(
                        title = campaignTitle,
                        createdAt = campaignStartTime,
                        totalRecipients = items.size,
                        sentCount = 0,
                        failedCount = 0,
                        status = CampaignStatus.RUNNING.name,
                        nodeUsed = nodeName,
                        durationSeconds = 0
                    )
                    val cId = dao.insertCampaign(campaign)
                    currentCampaignId = cId

                    val dbItems = items.map { qItem ->
                        CampaignItemEntity(
                            campaignId = cId,
                            recipientPhone = qItem.phoneNumber,
                            recipientName = qItem.recipientName,
                            messageText = qItem.bubbleTexts.joinToString("\n\n"),
                            status = CampaignItemStatus.PENDING.name,
                            errorMessage = null,
                            sentAt = System.currentTimeMillis()
                        )
                    }
                    val insertedIds = dao.insertItems(dbItems)
                    items.forEachIndexed { index, qItem ->
                        if (index < insertedIds.size) {
                            queueItemToDbId[qItem.id] = insertedIds[index]
                        }
                    }
                } catch (_: Exception) {}
            }
        }

        if (context != null) {
            startDispatch(context)
        }
    }

    fun startDispatch(context: Context? = null) {
        if (_isRunning.value) return
        _isRunning.value = true
        _isPaused.value = false

        if (context != null) {
            try {
                val serviceIntent = Intent(context, WhatsAppForegroundService::class.java).apply {
                    action = WhatsAppForegroundService.ACTION_START
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            } catch (_: Exception) {}
        }

        launchExecutionLoop(context?.applicationContext)
    }

    fun pauseDispatch() {
        _isPaused.value = true
    }

    fun resumeDispatch() {
        _isPaused.value = false
    }

    fun stopDispatch(context: Context? = null) {
        _isRunning.value = false
        _isPaused.value = false
        _currentTypingContact.value = null
        job?.cancel()
        job = null

        val cId = currentCampaignId
        val dao = campaignDao
        if (dao != null && cId != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    campaignInitJob?.join()
                    val existing = dao.getCampaignById(cId)
                    if (existing != null && existing.status == CampaignStatus.RUNNING.name) {
                        val duration = ((System.currentTimeMillis() - campaignStartTime) / 1000L).coerceAtLeast(0L)
                        val successCount = _queueItems.value.count { it.status == QueueTaskStatus.DELIVERED }
                        val failCount = _queueItems.value.count { it.status == QueueTaskStatus.FAILED }
                        dao.updateCampaign(
                            existing.copy(
                                sentCount = successCount,
                                failedCount = failCount,
                                status = CampaignStatus.CANCELLED.name,
                                durationSeconds = duration
                            )
                        )
                    }
                } catch (_: Exception) {}
            }
        }

        if (context != null) {
            try {
                val serviceIntent = Intent(context, WhatsAppForegroundService::class.java).apply {
                    action = WhatsAppForegroundService.ACTION_STOP
                }
                context.stopService(serviceIntent)
            } catch (_: Exception) {}
        }
    }

    private fun launchExecutionLoop(context: Context?) {
        job?.cancel()
        job = scope.launch {
            while (_isRunning.value) {
                // Find next pending item
                val currentList = _queueItems.value
                val nextItem = currentList.firstOrNull { it.status == QueueTaskStatus.PENDING }

                if (nextItem == null) {
                    // All tasks completed!
                    _isRunning.value = false
                    _currentTypingContact.value = null

                    campaignInitJob?.join()
                    val cId = currentCampaignId
                    val dao = campaignDao
                    if (dao != null && cId != null) {
                        try {
                            val duration = ((System.currentTimeMillis() - campaignStartTime) / 1000L).coerceAtLeast(0L)
                            val successCount = _queueItems.value.count { it.status == QueueTaskStatus.DELIVERED }
                            val failCount = _queueItems.value.count { it.status == QueueTaskStatus.FAILED }
                            val existing = dao.getCampaignById(cId)
                            if (existing != null) {
                                dao.updateCampaign(
                                    existing.copy(
                                        sentCount = successCount,
                                        failedCount = failCount,
                                        status = CampaignStatus.COMPLETED.name,
                                        durationSeconds = duration
                                    )
                                )
                            }
                        } catch (_: Exception) {}
                    }

                    updateNotification(
                        context,
                        LocalizationManager.activeStrings.notifDoneTitle,
                        LocalizationManager.activeStrings.notifDoneBody(totalCount),
                        totalCount,
                        sentCount
                    )
                    break
                }

                // Handle Pause State
                while (_isPaused.value && _isRunning.value) {
                    delay(500)
                }
                if (!_isRunning.value) break

                // Anti-ban wait before every contact except the first: random delay from Settings,
                // or the batch cooldown after every `batchSize` processed contacts
                val antiBan = AntiBanSettings.config.value
                val processed = _queueItems.value.count { it.status == QueueTaskStatus.DELIVERED || it.status == QueueTaskStatus.FAILED }
                val assignedJitter = when {
                    skipDelayForTesting || processed == 0 -> 0
                    processed % antiBan.batchSize == 0 -> antiBan.pauseDurationMin * 60
                    else -> AntiBanSettings.nextContactDelaySec(antiBan)
                }
                _isCoolingDown.value = !skipDelayForTesting && processed > 0 && processed % antiBan.batchSize == 0
                for (sec in assignedJitter downTo 1) {
                    _jitterCountdown.value = sec
                    delay(1000)
                    while (_isPaused.value && _isRunning.value) {
                        delay(500)
                    }
                    if (!_isRunning.value) break
                }
                if (!_isRunning.value) break

                // Resolve engine for this node
                val sessionManager = WhatsAppSessionManager.getInstance()
                _isCoolingDown.value = false

                // Dual-account mode: skip an account that is not connected when the other one is
                val nodeForItem = nextItem.fallbackNode
                    ?.takeIf { !sessionManager.isConnected(nextItem.nodeUsed) && sessionManager.isConnected(it) }
                    ?: nextItem.nodeUsed
                val engine = sessionManager.getEngineForNode(nodeForItem, context)

                // Phase: Typing simulation
                _currentTypingContact.value = nextItem.recipientName
                updateItemStatus(nextItem.id, QueueTaskStatus.TYPING)
                updateNotification(
                    context,
                    LocalizationManager.activeStrings.notifTypingTitle,
                    LocalizationManager.activeStrings.notifTypingBody(nextItem.recipientName, sentCount + 1, totalCount),
                    totalCount,
                    sentCount
                )

                if (!skipDelayForTesting && antiBan.typingSimEnabled) {
                    engine.sendTypingPresence(nextItem.phoneNumber, AntiBanSettings.nextBubbleDelayMs())
                } else if (skipDelayForTesting) {
                    engine.sendTypingPresence(nextItem.phoneNumber, 0L)
                }
                while (_isPaused.value && _isRunning.value) {
                    delay(if (skipDelayForTesting) 0 else 500)
                }
                if (!_isRunning.value) break

                // Phase: Send Message. Each file goes out with the bubble it is placed on (bubble text as
                // caption); files placed "separate" go alone after the last bubble.
                var overallSuccess = true
                var lastErrorMessage: String? = null
                val steps = buildSendSteps(nextItem)
                for ((stepIdx, step) in steps.withIndex()) {
                    if (!_isRunning.value) break
                    while (_isPaused.value && _isRunning.value) delay(500)
                    if (stepIdx > 0 && !skipDelayForTesting) bubblePause(engine, nextItem.phoneNumber, antiBan)
                    val res = engine.sendMessage(nextItem.phoneNumber, step.text, listOfNotNull(step.mediaUri))
                    if (!res.isSuccess) {
                        overallSuccess = false
                        lastErrorMessage = res.errorMessage
                    }
                }

                val sentTimestamp = System.currentTimeMillis()

                if (overallSuccess) {
                    updateItemStatus(nextItem.id, QueueTaskStatus.DELIVERED, sentTimestamp)
                } else {
                    updateItemStatus(nextItem.id, QueueTaskStatus.FAILED, errorMessage = lastErrorMessage)
                }
                _currentTypingContact.value = null

                // Update Room SQLite item
                val dao = campaignDao
                if (dao != null) {
                    try {
                        campaignInitJob?.join()
                        val dbId = queueItemToDbId[nextItem.id]
                        if (dbId != null) {
                            if (overallSuccess) {
                                dao.updateCampaignItemStatus(
                                    id = dbId,
                                    status = CampaignItemStatus.SENT.name,
                                    errorMessage = null,
                                    sentAt = sentTimestamp
                                )
                            } else {
                                dao.updateCampaignItemStatus(
                                    id = dbId,
                                    status = CampaignItemStatus.FAILED.name,
                                    errorMessage = lastErrorMessage,
                                    sentAt = sentTimestamp
                                )
                            }
                        }
                    } catch (_: Exception) {}
                }

                updateNotification(
                    context,
                    LocalizationManager.activeStrings.notifSendingTitle(percentComplete),
                    LocalizationManager.activeStrings.notifSendingBody(sentCount, totalCount),
                    totalCount,
                    sentCount
                )
            }
        }
    }

    internal data class SendStep(val text: String, val mediaUri: String?)

    internal fun buildSendSteps(item: QueueItem): List<SendStep> {
        val bubbles = item.bubbleTexts
        // No placement recorded = first bubble (old behaviour); a placement past the last bubble = separate
        val placement = { i: Int ->
            val p = item.mediaPlacements.getOrNull(i) ?: 0
            if (p in bubbles.indices) p else MediaAttachment.SEPARATE_AFTER
        }
        val mediaByBubble = item.mediaUris.indices.groupBy(placement)
        val steps = mutableListOf<SendStep>()
        bubbles.forEachIndexed { b, text ->
            val media = mediaByBubble[b].orEmpty()
            if (media.isEmpty()) {
                steps += SendStep(text, null)
            } else {
                media.forEachIndexed { k, m -> steps += SendStep(if (k == 0) text else "", item.mediaUris[m]) }
            }
        }
        mediaByBubble[MediaAttachment.SEPARATE_AFTER].orEmpty().forEach { m -> steps += SendStep("", item.mediaUris[m]) }
        return steps.ifEmpty { listOf(SendStep("", null)) }
    }

    // 3–6 s between bubbles to one contact, shown as "typing..." when typing simulation is on
    private suspend fun bubblePause(engine: WhatsAppEngine, phone: String, antiBan: AntiBanConfig) {
        val ms = AntiBanSettings.nextBubbleDelayMs()
        val start = System.currentTimeMillis()
        if (antiBan.typingSimEnabled) {
            try { engine.sendTypingPresence(phone, ms) } catch (_: Exception) {}
        }
        // Presence may return early (offline gateway); still wait out the full pause
        val left = ms - (System.currentTimeMillis() - start)
        if (left > 0) delay(left)
    }

    private fun updateItemStatus(
        itemId: String,
        status: QueueTaskStatus,
        timestamp: Long? = null,
        errorMessage: String? = null
    ) {
        _queueItems.value = _queueItems.value.map { item ->
            if (item.id == itemId) {
                item.copy(
                    status = status,
                    sentTimestamp = timestamp ?: item.sentTimestamp,
                    errorMessage = errorMessage ?: item.errorMessage
                )
            } else {
                item
            }
        }
    }

    private fun updateNotification(
        context: Context?,
        title: String,
        content: String,
        total: Int,
        progress: Int
    ) {
        if (context == null) return
        try {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
            val notification = NotificationHelper.buildNotification(
                context = context,
                title = title,
                content = content,
                progressMax = total,
                progressCurrent = progress
            )
            manager?.notify(NotificationHelper.NOTIFICATION_ID, notification)
        } catch (_: Exception) {}
    }

    companion object {
        @Volatile
        private var instance: DispatchQueueManager? = null

        fun getInstance(campaignDao: CampaignDao? = null): DispatchQueueManager {
            return instance ?: synchronized(this) {
                instance ?: DispatchQueueManager(campaignDao).also { instance = it }
            }.also {
                if (campaignDao != null) {
                    it.setCampaignDao(campaignDao)
                }
            }
        }

        fun resetForTesting(campaignDao: CampaignDao? = null): DispatchQueueManager {
            instance?.stopDispatch()
            val newInstance = DispatchQueueManager(campaignDao)
            instance = newInstance
            return newInstance
        }

        fun buildQueueItem(
            contact: ContactEntity,
            bubbles: List<MessageBubble>,
            defaultCompany: String = "",
            defaultCustom1: String = "",
            nodeUsed: AccountNode = AccountNode.PERSONAL,
            mediaUris: List<String> = emptyList(),
            mediaPlacements: List<Int> = emptyList(),
            fallbackNode: AccountNode? = null
        ): QueueItem {
            val nick = contact.customNickname.ifEmpty { ContactRepository.extractSmartNickname(contact.rawName) }
            val effectiveCompany = contact.company.ifBlank { defaultCompany }
            val effectiveCustom1 = contact.custom1.ifBlank { defaultCustom1 }
            val evaluatedBubbles = bubbles.map { b ->
                val spun = SpintaxParser.spin(b.text)
                SpintaxParser.interpolateVariables(
                    text = spun,
                    nickname = nick,
                    variables = mapOf("Company" to effectiveCompany, "Custom_1" to effectiveCustom1)
                )
            }
            return QueueItem(
                contactId = contact.id,
                recipientName = contact.rawName.replace(Regex("\\(.*?\\)"), "").trim(),
                phoneNumber = contact.phoneNumber,
                bubbleTexts = evaluatedBubbles,
                mediaUris = mediaUris,
                mediaPlacements = mediaPlacements,
                nodeUsed = nodeUsed,
                fallbackNode = fallbackNode,
                status = QueueTaskStatus.PENDING
            )
        }

        private fun generateInitialDemoQueue(): List<QueueItem> {
            val names = listOf(
                "Sarah Jenkins", "David M. Miller", "Elena Rostova", "Marcus Chen",
                "Aisha Patel", "Budi Santoso", "Dewi Lestari", "Rizki Pratama",
                "Nurul Hidayah", "Farhan Rizki"
            )
            val phones = listOf(
                "+62 811-2345-6789", "+62 812-3456-7890", "+62 813-9876-5432", "+62 819-0123-4567",
                "+62 821-2345-6789", "+62 856-7890-1234", "+62 877-1234-5678", "+62 878-9012-3456",
                "+62 896-1234-5678", "+62 899-0123-4567"
            )

            return names.mapIndexed { index, name ->
                val status = when {
                    index < 4 -> QueueTaskStatus.DELIVERED
                    index == 4 -> QueueTaskStatus.TYPING
                    else -> QueueTaskStatus.PENDING
                }
                QueueItem(
                    contactId = index.toLong() + 1,
                    recipientName = name,
                    phoneNumber = phones[index % phones.size],
                    bubbleTexts = listOf("Halo $name, penawaran spesial minggu ini dari KirimRasa!"),
                    nodeUsed = if (index % 2 == 0) AccountNode.PERSONAL else AccountNode.BUSINESS,
                    status = status,
                    jitterSecondsAssigned = 25 + (index * 3) % 35,
                    sentTimestamp = if (status == QueueTaskStatus.DELIVERED) System.currentTimeMillis() - (10 - index) * 45_000L else null
                )
            }
        }
    }
}
