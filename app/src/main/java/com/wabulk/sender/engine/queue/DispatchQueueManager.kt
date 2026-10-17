package com.wabulk.sender.engine.queue

import android.content.Context
import android.content.Intent
import com.wabulk.sender.domain.model.AccountNode
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

class DispatchQueueManager private constructor() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    private val _queueItems = MutableStateFlow<List<QueueItem>>(generateInitialDemoQueue())
    val queueItems: StateFlow<List<QueueItem>> = _queueItems.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _jitterCountdown = MutableStateFlow(34)
    val jitterCountdown: StateFlow<Int> = _jitterCountdown.asStateFlow()

    private val _currentTypingContact = MutableStateFlow<String?>(null)
    val currentTypingContact: StateFlow<String?> = _currentTypingContact.asStateFlow()

    val totalCount: Int
        get() = _queueItems.value.size

    val sentCount: Int
        get() = _queueItems.value.count { it.status == QueueTaskStatus.DELIVERED }

    val remainingCount: Int
        get() = _queueItems.value.count { it.status == QueueTaskStatus.PENDING || it.status == QueueTaskStatus.TYPING }

    val percentComplete: Int
        get() = if (totalCount == 0) 0 else ((sentCount.toFloat() / totalCount.toFloat()) * 100).toInt()

    fun enqueueBroadcast(
        items: List<QueueItem>,
        context: Context? = null
    ) {
        _queueItems.value = items
        _isPaused.value = false
        if (context != null) {
            startDispatch(context)
        }
    }

    fun startDispatch(context: Context) {
        if (_isRunning.value) return
        _isRunning.value = true
        _isPaused.value = false

        // Start Foreground Service
        val serviceIntent = Intent(context, WhatsAppForegroundService::class.java).apply {
            action = WhatsAppForegroundService.ACTION_START
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        launchExecutionLoop(context.applicationContext)
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

        if (context != null) {
            val serviceIntent = Intent(context, WhatsAppForegroundService::class.java).apply {
                action = WhatsAppForegroundService.ACTION_STOP
            }
            context.stopService(serviceIntent)
        }
    }

    private fun launchExecutionLoop(context: Context) {
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
                    updateNotification(
                        context,
                        "Pengiriman Selesai!",
                        "Semua $totalCount pesan berhasil terkirim.",
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

                // Jitter delay countdown simulation (5 down to 1s for dynamic responsiveness)
                val assignedJitter = 4
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
                val engine = sessionManager.getEngineForNode(nextItem.nodeUsed)

                // Phase: Typing simulation
                _currentTypingContact.value = nextItem.recipientName
                updateItemStatus(nextItem.id, QueueTaskStatus.TYPING)
                updateNotification(
                    context,
                    "Mengetik Pesan...",
                    "Mengetik pesan untuk ${nextItem.recipientName} (${sentCount + 1}/$totalCount)",
                    totalCount,
                    sentCount
                )

                engine.sendTypingPresence(nextItem.phoneNumber, 2000L)
                while (_isPaused.value && _isRunning.value) {
                    delay(500)
                }
                if (!_isRunning.value) break

                // Phase: Send Message
                val messageText = nextItem.bubbleTexts.joinToString("\n\n")
                val sendResult = engine.sendMessage(nextItem.phoneNumber, messageText)

                if (sendResult.isSuccess) {
                    updateItemStatus(nextItem.id, QueueTaskStatus.DELIVERED, System.currentTimeMillis())
                } else {
                    updateItemStatus(nextItem.id, QueueTaskStatus.FAILED, errorMessage = sendResult.errorMessage)
                }
                _currentTypingContact.value = null

                updateNotification(
                    context,
                    "Mengirim Pesan (${percentComplete}%)",
                    "${sentCount}/$totalCount terkirim - Lanjut ke pesan berikutnya",
                    totalCount,
                    sentCount
                )
            }
        }
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
        context: Context,
        title: String,
        content: String,
        total: Int,
        progress: Int
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val notification = NotificationHelper.buildNotification(
            context = context,
            title = title,
            content = content,
            progressMax = total,
            progressCurrent = progress
        )
        manager.notify(NotificationHelper.NOTIFICATION_ID, notification)
    }

    companion object {
        @Volatile
        private var instance: DispatchQueueManager? = null

        fun getInstance(): DispatchQueueManager {
            return instance ?: synchronized(this) {
                instance ?: DispatchQueueManager().also { instance = it }
            }
        }

        private fun generateInitialDemoQueue(): List<QueueItem> {
            val names = listOf(
                "Sarah Jenkins", "David M. Miller", "Elena Rostova", "Marcus Chen",
                "Aisha Patel", "Budi Santoso", "Dewi Lestari", "Rizki Pratama",
                "Nurul Hidayah", "Farhan Rizki"
            )
            val phones = listOf(
                "+1 (555) 392-8104", "+1 (555) 749-2041", "+44 7911 123456", "+65 9123 4567",
                "+91 98765 43210", "+62 812-3456-7890", "+62 813-9876-5432", "+62 819-0123-4567",
                "+62 821-2345-6789", "+62 856-7890-1234"
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
