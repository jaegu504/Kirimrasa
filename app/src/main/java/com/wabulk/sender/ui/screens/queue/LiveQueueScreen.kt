package com.wabulk.sender.ui.screens.queue

import android.os.Build
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.data.locale.QueueTexts
import com.wabulk.sender.data.locale.queueTexts
import com.wabulk.sender.engine.queue.model.QueueItem
import com.wabulk.sender.engine.queue.model.QueueTaskStatus
import com.wabulk.sender.theme.AppleTheme
import com.wabulk.sender.theme.GroupCard
import com.wabulk.sender.theme.LargeTitle
import com.wabulk.sender.theme.PrimaryCapsuleButton
import com.wabulk.sender.theme.RowSeparator
import com.wabulk.sender.theme.SectionHeader
import com.wabulk.sender.theme.TintPill
import com.wabulk.sender.theme.springClickable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class QueueStatusFilter { ALL, ACTIVE, DELIVERED, PENDING, FAILED }

@Composable
fun LiveQueueScreen(
    viewModel: QueueViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val t = queueTexts()
    val c = AppleTheme.colors
    val context = LocalContext.current
    val allItems by viewModel.queueItems.collectAsState()
    val items by viewModel.filteredItems.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val jitterCountdown by viewModel.jitterCountdown.collectAsState()
    val isCoolingDown by viewModel.isCoolingDown.collectAsState()
    val currentTypingContact by viewModel.currentTypingContact.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val sentCount by viewModel.sentCount.collectAsState()

    val failedCount = allItems.count { it.status == QueueTaskStatus.FAILED }
    val pendingCount = allItems.count { it.status == QueueTaskStatus.PENDING }
    var confirmStop by rememberSaveable { mutableStateOf(false) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)
    ) {
        item {
            LargeTitle(text = t.title) {
                IconButton(onClick = {
                    viewModel.exportCampaignReport(context) { count -> strings.queueExportSuccessToast(count) }
                }) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = t.exportReport, tint = c.blue)
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (totalCount == 0) {
            item {
                GroupCard(padding = 24.dp, spacing = 8.dp) {
                    Text(
                        text = t.emptyTitle,
                        color = c.label,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = t.emptyHint(strings.navCompose),
                        color = c.secondaryLabel,
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            return@LazyColumn
        }

        item {
            GroupCard(padding = 20.dp, spacing = 16.dp) {
                Box(Modifier.fillMaxWidth().padding(top = 4.dp), contentAlignment = Alignment.Center) {
                    ProgressRing(sent = sentCount, total = totalCount, texts = t)
                }
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PhasePill(
                        texts = t,
                        isRunning = isRunning,
                        isPaused = isPaused,
                        typingName = currentTypingContact,
                        countdown = jitterCountdown,
                        isCoolingDown = isCoolingDown,
                        pendingCount = pendingCount
                    )
                }
                if (isRunning) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ControlButton(
                            text = if (isPaused) t.resume else t.pause,
                            icon = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            background = c.fill,
                            content = c.label,
                            onClick = { viewModel.togglePause() }
                        )
                        ControlButton(
                            text = t.stop,
                            icon = Icons.Default.Stop,
                            background = c.redTint,
                            content = c.redOnTint,
                            onClick = { confirmStop = true }
                        )
                    }
                } else if (pendingCount > 0) {
                    PrimaryCapsuleButton(text = t.resume, icon = Icons.Default.PlayArrow, onClick = { viewModel.startQueue() })
                }
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
            SectionHeader(text = t.recipients) {
                Text(
                    text = t.summary(sentCount, failedCount, totalCount - sentCount - failedCount),
                    color = c.secondaryLabel,
                    fontSize = 13.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    QueueStatusFilter.ALL to strings.queueFilterAll,
                    QueueStatusFilter.ACTIVE to strings.queueFilterActive,
                    QueueStatusFilter.DELIVERED to strings.queueFilterDelivered,
                    QueueStatusFilter.PENDING to strings.queueFilterPending,
                    QueueStatusFilter.FAILED to t.filterFailed
                ).forEach { (filter, label) ->
                    val selected = selectedFilter == filter
                    TintPill(
                        text = label,
                        background = if (selected) c.blueTint else c.card,
                        content = if (selected) c.blueOnTint else c.secondaryLabel,
                        selected = selected,
                        onClick = { viewModel.setFilter(filter) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (items.isEmpty()) {
            item {
                Text(
                    text = strings.queueFilterEmpty,
                    color = c.secondaryLabel,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                )
            }
        } else {
            itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                val last = index == items.lastIndex
                val shape = RoundedCornerShape(
                    topStart = if (index == 0) 20.dp else 0.dp,
                    topEnd = if (index == 0) 20.dp else 0.dp,
                    bottomStart = if (last) 20.dp else 0.dp,
                    bottomEnd = if (last) 20.dp else 0.dp
                )
                Column(Modifier.clip(shape).background(c.card)) {
                    RecipientRow(item = item, isRunning = isRunning, texts = t, timeFormat = timeFormat)
                    if (!last) RowSeparator(startInset = 60.dp)
                }
            }
        }
    }

    if (confirmStop) {
        AlertDialog(
            onDismissRequest = { confirmStop = false },
            title = { Text(t.stopConfirmTitle, fontWeight = FontWeight.Bold) },
            text = { Text(t.stopConfirmBody, fontSize = 15.sp) },
            confirmButton = {
                TextButton(onClick = {
                    confirmStop = false
                    viewModel.stopBlast()
                }) { Text(t.stop, color = c.red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { confirmStop = false }) { Text(t.cancel, color = c.blue) }
            },
            containerColor = c.card,
            titleContentColor = c.label,
            textContentColor = c.secondaryLabel
        )
    }
}

@Composable
private fun ProgressRing(sent: Int, total: Int, texts: QueueTexts) {
    val c = AppleTheme.colors
    val target = if (total == 0) 0f else (sent.toFloat() / total).coerceIn(0f, 1f)
    val progress by animateFloatAsState(targetValue = target, animationSpec = tween(600), label = "ringProgress")
    val ringSize = 168.dp
    val stroke = 14.dp
    // Gradient wraps back to green after the arc end so the round start cap stays green.
    val brush = Brush.sweepGradient(0f to c.green, progress.coerceAtLeast(0.01f) to c.teal, 1f to c.green)
    // Real blur needs Android 12+; older phones get a wider, fainter halo instead.
    val canBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Box(
        modifier = Modifier
            .size(ringSize)
            .clearAndSetSemantics { contentDescription = texts.ringDescription(sent, total) },
        contentAlignment = Alignment.Center
    ) {
        if (progress > 0f) {
            Canvas(
                Modifier
                    .fillMaxSize()
                    .then(if (canBlur) Modifier.blur(12.dp, BlurredEdgeTreatment.Unbounded) else Modifier)
                    .graphicsLayer { alpha = if (canBlur) 0.55f else 0.2f }
            ) {
                drawRingArc(brush, progress, (if (canBlur) stroke else stroke * 2).toPx())
            }
        }
        Canvas(Modifier.fillMaxSize()) {
            val px = stroke.toPx()
            drawArc(
                color = c.fill,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(px / 2, px / 2),
                size = Size(size.width - px, size.height - px),
                style = Stroke(width = px)
            )
            if (progress > 0f) drawRingArc(brush, progress, px)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = buildAnnotatedString {
                    append("$sent")
                    withStyle(SpanStyle(fontSize = 22.sp, color = c.tertiaryLabel, fontWeight = FontWeight.Bold)) {
                        append("/$total")
                    }
                },
                color = c.label,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp,
                style = TextStyle(fontFeatureSettings = "tnum")
            )
            Text(text = texts.sentWord, color = c.secondaryLabel, fontSize = 13.sp)
        }
    }
}

/** Progress arc starting at 12 o'clock, inset so a stroke of [widthPx] (main ring width) stays centred on the track. */
private fun DrawScope.drawRingArc(brush: Brush, progress: Float, widthPx: Float) {
    val inset = 14.dp.toPx() / 2
    rotate(-90f) {
        drawArc(
            brush = brush,
            startAngle = 0f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = Size(size.width - inset * 2, size.height - inset * 2),
            style = Stroke(width = widthPx, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun PhasePill(
    texts: QueueTexts,
    isRunning: Boolean,
    isPaused: Boolean,
    typingName: String?,
    countdown: Int,
    isCoolingDown: Boolean,
    pendingCount: Int
) {
    val c = AppleTheme.colors
    val (text, icon, colors) = when {
        !isRunning && pendingCount == 0 -> Triple(texts.phaseDone, Icons.Default.CheckCircle, c.greenTint to c.greenOnTint)
        !isRunning -> Triple(texts.phaseReady, Icons.Default.Schedule, c.fill to c.secondaryLabel)
        isPaused -> Triple(texts.phasePaused, Icons.Default.Pause, c.orangeTint to c.orangeOnTint)
        typingName != null -> Triple(texts.phaseTyping(typingName), Icons.Default.Edit, c.blueTint to c.blueOnTint)
        isCoolingDown -> Triple(
            texts.phaseCooldown("%d:%02d".format(countdown / 60, countdown % 60)),
            Icons.Default.HourglassTop,
            c.orangeTint to c.orangeOnTint
        )
        else -> Triple(texts.phaseWaiting(countdown), Icons.Default.Timer, c.orangeTint to c.orangeOnTint)
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.first)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = colors.second, modifier = Modifier.size(16.dp))
        Text(text = text, color = colors.second, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun RowScope.ControlButton(
    text: String,
    icon: ImageVector,
    background: Color,
    content: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .semantics { role = Role.Button }
            .springClickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = content, modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(8.dp))
        Text(text = text, color = content, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RecipientRow(item: QueueItem, isRunning: Boolean, texts: QueueTexts, timeFormat: SimpleDateFormat) {
    val c = AppleTheme.colors
    // A TYPING item left over after Stop is no longer being typed.
    val status = if (item.status == QueueTaskStatus.TYPING && !isRunning) QueueTaskStatus.PENDING else item.status
    val (subtitle, subtitleColor) = when (status) {
        QueueTaskStatus.TYPING -> texts.rowTyping to c.blueOnTint
        QueueTaskStatus.DELIVERED ->
            (item.sentTimestamp?.let { texts.rowSent(timeFormat.format(Date(it))) } ?: texts.rowSentNoTime) to c.secondaryLabel
        QueueTaskStatus.FAILED -> texts.rowFailed(item.errorMessage) to c.redOnTint
        QueueTaskStatus.PENDING, QueueTaskStatus.PAUSED -> texts.rowWaiting to c.secondaryLabel
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (status) {
            QueueTaskStatus.TYPING -> StatusCircle(c.blueTint) { TypingDots(c.blue) }
            QueueTaskStatus.DELIVERED -> StatusCircle(c.greenTint) { StatusIcon(Icons.Default.Check, c.greenOnTint) }
            QueueTaskStatus.FAILED -> StatusCircle(c.redTint) { StatusIcon(Icons.Default.PriorityHigh, c.redOnTint) }
            QueueTaskStatus.PENDING, QueueTaskStatus.PAUSED ->
                StatusCircle(c.fill) { StatusIcon(Icons.Default.Schedule, c.secondaryLabel) }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(text = item.recipientName, color = c.label, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(
                text = subtitle,
                color = subtitleColor,
                fontSize = 13.sp,
                fontWeight = if (status == QueueTaskStatus.TYPING) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun StatusCircle(background: Color, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.size(32.dp).clip(CircleShape).background(background),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun StatusIcon(icon: ImageVector, tint: Color, size: Dp = 18.dp) {
    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(size))
}

@Composable
private fun TypingDots(color: Color) {
    val transition = rememberInfiniteTransition(label = "typingDots")
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(3) { i ->
            val alpha by transition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(450, delayMillis = i * 150), RepeatMode.Reverse),
                label = "dot$i"
            )
            Box(Modifier.size(5.dp).graphicsLayer { this.alpha = alpha }.clip(CircleShape).background(color))
        }
    }
}
