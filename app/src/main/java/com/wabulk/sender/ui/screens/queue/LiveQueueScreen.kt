package com.wabulk.sender.ui.screens.queue

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.engine.queue.model.QueueItem
import com.wabulk.sender.engine.queue.model.QueueTaskStatus
import com.wabulk.sender.theme.EmeraldOnPrimaryContainer
import com.wabulk.sender.theme.EmeraldPrimary
import com.wabulk.sender.theme.EmeraldPrimaryContainer
import com.wabulk.sender.theme.EmeraldSecondary
import com.wabulk.sender.theme.EmeraldSecondaryContainer
import com.wabulk.sender.theme.LocalNeumorphicColors
import com.wabulk.sender.theme.StatusError
import com.wabulk.sender.theme.StatusErrorContainer
import com.wabulk.sender.theme.pulseGlow
import com.wabulk.sender.theme.springClickable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class QueueStatusFilter { ALL, ACTIVE, DELIVERED, PENDING }

@Composable
fun LiveQueueScreen(
    viewModel: QueueViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val neuColors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val items by viewModel.filteredItems.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val jitterCountdown by viewModel.jitterCountdown.collectAsState()
    val currentTypingContact by viewModel.currentTypingContact.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val sentCount by viewModel.sentCount.collectAsState()
    val percentComplete by viewModel.percentComplete.collectAsState()
    val remainingCount by viewModel.remainingCount.collectAsState()
    val campaignTitle by viewModel.campaignTitle.collectAsState()

    // Infinite transition for spinning radar and typing bouncing dots
    val infiniteTransition = rememberInfiniteTransition(label = "queueAnimTransition")
    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing)
        ),
        label = "radarRotation"
    )

    val dot1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High-Level Security Status Header
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldSecondaryContainer.copy(alpha = 0.3f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = strings.queueDualNodeActive.uppercase(),
                        color = EmeraldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(neuColors.surfaceRaised)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isRunning) neuColors.primary else neuColors.textSecondary)
                            .then(if (isRunning) Modifier.pulseGlow() else Modifier)
                    )
                    Text(
                        text = if (isPaused) "DIPERJEDA" else if (isRunning) "DISPATCHING" else "STANDBY",
                        color = if (isRunning) neuColors.primary else neuColors.textSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = strings.queueTitle,
                color = neuColors.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = if (campaignTitle.isNotBlank()) campaignTitle else strings.queueCampaignName,
                color = neuColors.textSecondary,
                fontSize = 11.sp
            )
        }

        // Accessibility Service Automation Pill / Alert Banner
        var a11yActive by remember { mutableStateOf(com.wabulk.sender.service.WhatsAppAccessibilityService.isAccessibilityServiceEnabled(context)) }
        LaunchedEffect(Unit) {
            while (true) {
                a11yActive = com.wabulk.sender.service.WhatsAppAccessibilityService.isAccessibilityServiceEnabled(context)
                delay(2000)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (a11yActive) neuColors.surfaceInset else EmeraldSecondaryContainer.copy(alpha = 0.35f))
                .springClickable {
                    if (!a11yActive) {
                        com.wabulk.sender.service.WhatsAppAccessibilityService.openAccessibilitySettings(context)
                    }
                }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (a11yActive) Icons.Default.CheckCircle else Icons.Default.AccessibilityNew,
                    contentDescription = null,
                    tint = if (a11yActive) neuColors.primary else EmeraldSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (a11yActive)
                        "Mode Otomatis Aktif — Tombol kirim WhatsApp akan ditekan otomatis"
                    else
                        "Layanan Aksesibilitas nonaktif — Ketuk untuk aktifkan pengiriman otomatis",
                    color = if (a11yActive) neuColors.primary else neuColors.textPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Primary Campaign Progress Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(neuColors.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Metric Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "$sentCount",
                        color = neuColors.textPrimary,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "/ $totalCount",
                        color = neuColors.textSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldSecondaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$percentComplete%",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // High-Tech Progress Bar
            val progressFraction = if (totalCount == 0) 0f else (sentCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(neuColors.surfaceInset)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary)
                ) {
                    if (progressFraction > 0.05f) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .align(Alignment.CenterEnd)
                                .padding(end = 2.dp)
                                .pulseGlow()
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = strings.queueRecipientsRemaining(remainingCount),
                    color = neuColors.textSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = if (remainingCount > 0) "~${remainingCount * 30}s" else "Selesai",
                    color = neuColors.textSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Live Typing / Jitter Delay Status Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(neuColors.surfaceInset)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(if (isRunning && !isPaused) radarRotation else 0f)
                    )

                    Column {
                        val headerLabel = if (currentTypingContact != null) {
                            "Mengetik: $currentTypingContact"
                        } else if (isPaused) {
                            "Antrean sedang dijeda"
                        } else {
                            "Menunggu jeda aman (${jitterCountdown}d)"
                        }
                        Text(
                            text = headerLabel,
                            color = neuColors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isRunning) strings.queueTypingCadenceActive else "Siap mengirim",
                            color = neuColors.textSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // 3 Bouncing Dots
                if (isRunning && !isPaused) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(neuColors.surfaceRaised)
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(y = dot1Offset.dp)
                                .size(width = 4.dp, height = 10.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                        )
                        Box(
                            modifier = Modifier
                                .offset(y = dot2Offset.dp)
                                .size(width = 4.dp, height = 10.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                        )
                        Box(
                            modifier = Modifier
                                .offset(y = dot3Offset.dp)
                                .size(width = 4.dp, height = 10.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                        )
                    }
                }
            }

            // Mini Telemetry Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Success
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(neuColors.surfaceRaised)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = strings.queueMetricSuccess, color = neuColors.textSecondary, fontSize = 9.sp)
                        Text(text = "100%", color = EmeraldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Avg Delay
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(neuColors.surfaceRaised)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = strings.queueMetricAvgDelay, color = neuColors.textSecondary, fontSize = 9.sp)
                        Text(text = "34.0s", color = neuColors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Flag Alert
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(neuColors.surfaceRaised)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = strings.queueMetricFlagAlert, color = neuColors.textSecondary, fontSize = 9.sp)
                        Text(text = "0 Aman", color = EmeraldSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // High-Visibility Tactical Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Pause / Resume Queue
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(CircleShape)
                        .background(neuColors.surfaceRaised)
                        .springClickable { viewModel.togglePause() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayCircle else Icons.Default.PauseCircle,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isPaused) "Lanjutkan" else strings.queuePauseBtn,
                            color = neuColors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Stop Blast
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(CircleShape)
                        .background(StatusErrorContainer)
                        .springClickable { viewModel.stopBlast() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.StopCircle,
                            contentDescription = null,
                            tint = StatusError,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = strings.queueStopBtn,
                            color = StatusError,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Ekspor Laporan CSV Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(neuColors.surfaceRaised)
                    .springClickable {
                        viewModel.exportCampaignReport(context) { count ->
                            strings.queueExportSuccessToast(count)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = strings.queueExportCsvBtn,
                        color = EmeraldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Filter Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                QueueStatusFilter.ALL to strings.queueFilterAll,
                QueueStatusFilter.ACTIVE to strings.queueFilterActive,
                QueueStatusFilter.DELIVERED to strings.queueFilterDelivered,
                QueueStatusFilter.PENDING to strings.queueFilterPending
            )

            filters.forEach { (filter, label) ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) EmeraldPrimary else neuColors.surfaceRaised)
                        .springClickable { viewModel.setFilter(filter) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color(0xFF003915) else neuColors.textSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        if (filter == QueueStatusFilter.ACTIVE) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFF003915) else EmeraldPrimary)
                            )
                        }
                    }
                }
            }
        }

        // Queue Recipient Cards List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(neuColors.surface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (totalCount == 0)
                            "Belum ada antrean broadcast. Susun pesan di tab 'Buat Pesan' dan tekan 'Mulai Kirim Massal' untuk memulai pengiriman."
                        else
                            "Tidak ada kontak dalam filter ini",
                        color = neuColors.textSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                items.forEach { item ->
                    QueueRecipientItem(item = item, timeFormat = timeFormat, radarRotation = radarRotation)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun QueueRecipientItem(
    item: QueueItem,
    timeFormat: SimpleDateFormat,
    radarRotation: Float
) {
    val neuColors = LocalNeumorphicColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(neuColors.surface)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(neuColors.surfaceRaised),
                contentAlignment = Alignment.Center
            ) {
                when (item.status) {
                    QueueTaskStatus.TYPING -> {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(radarRotation)
                        )
                    }
                    QueueTaskStatus.DELIVERED -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    QueueTaskStatus.PENDING, QueueTaskStatus.PAUSED -> {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = neuColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    QueueTaskStatus.FAILED -> {
                        Icon(
                            imageVector = Icons.Default.StopCircle,
                            contentDescription = null,
                            tint = StatusError,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Column {
                Text(
                    text = item.recipientName,
                    color = neuColors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.phoneNumber,
                    color = neuColors.textSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        when (item.status) {
            QueueTaskStatus.TYPING -> {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldPrimaryContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "MENGETIK (2s)",
                        color = EmeraldOnPrimaryContainer,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            QueueTaskStatus.DELIVERED -> {
                Text(
                    text = item.sentTimestamp?.let { timeFormat.format(Date(it)) } ?: "TERKIRIM",
                    color = EmeraldPrimary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            QueueTaskStatus.PENDING -> {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(neuColors.surfaceRaised)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "MENUNGGU",
                        color = neuColors.textSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            QueueTaskStatus.PAUSED -> {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(neuColors.surfaceRaised)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "DIJEDA",
                        color = EmeraldSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            QueueTaskStatus.FAILED -> {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(StatusErrorContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "GAGAL",
                        color = StatusError,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
