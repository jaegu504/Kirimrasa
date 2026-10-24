package com.wabulk.sender.ui.screens.composer

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.data.locale.composerTexts
import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.domain.model.MediaAttachment
import com.wabulk.sender.engine.WhatsAppSessionManager
import com.wabulk.sender.engine.model.NodeSession
import com.wabulk.sender.engine.model.NodeStatus
import com.wabulk.sender.engine.queue.AntiBanConfig
import com.wabulk.sender.engine.queue.AntiBanSettings
import com.wabulk.sender.theme.AppleTheme
import com.wabulk.sender.theme.GroupCard
import com.wabulk.sender.theme.IconTile
import com.wabulk.sender.theme.LargeTitle
import com.wabulk.sender.theme.RowSeparator
import com.wabulk.sender.theme.SectionHeader
import com.wabulk.sender.theme.SegmentedControl
import com.wabulk.sender.theme.StatusLabel
import com.wabulk.sender.theme.TileGradients
import com.wabulk.sender.theme.TintPill
import com.wabulk.sender.theme.springClickable
import com.wabulk.sender.ui.screens.composer.components.AttachmentCard
import com.wabulk.sender.ui.screens.composer.components.WhatsAppPreviewBubble
import kotlin.math.ceil
import kotlinx.coroutines.launch

/**
 * Send tab. [selectedContactCount] 0 means "send to everyone"; [totalContactCount] null = unknown
 * (button stays enabled, MainScreen still refuses an empty list). [previewContactName] is the
 * nickname shown in the preview, a sample name when null.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComposerScreen(
    onOpenHelp: () -> Unit = {},
    viewModel: ComposerViewModel,
    selectedContactCount: Int = 0,
    modifier: Modifier = Modifier,
    onLaunchCampaign: () -> Unit = {},
    onOpenContacts: () -> Unit = {},
    totalContactCount: Int? = null,
    previewContactName: String? = null
) {
    val c = AppleTheme.colors
    val t = composerTexts()
    val strings = LocalAppStrings.current
    val uiState by viewModel.uiState.collectAsState()
    val attachments by viewModel.attachments.collectAsState()
    val antiBan by AntiBanSettings.config.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { WhatsAppSessionManager.getInstance() }
    val node1Session by sessionManager.node1Session.collectAsState()
    val node2Session by sessionManager.node2Session.collectAsState()
    var showOptions by remember { mutableStateOf(false) }

    val toast = { message: String -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                var fileName = "lampiran_${System.currentTimeMillis()}"
                var fileSize = 0L

                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) {
                            fileName = cursor.getString(nameIndex) ?: fileName
                        }
                        if (sizeIndex != -1) {
                            fileSize = cursor.getLong(sizeIndex)
                        }
                    }
                }

                val mimeType = context.contentResolver.getType(uri) ?: when {
                    fileName.endsWith(".jpg", true) || fileName.endsWith(".jpeg", true) -> "image/jpeg"
                    fileName.endsWith(".png", true) -> "image/png"
                    fileName.endsWith(".webp", true) -> "image/webp"
                    fileName.endsWith(".mp4", true) -> "video/mp4"
                    fileName.endsWith(".pdf", true) -> "application/pdf"
                    else -> "application/octet-stream"
                }

                val attachment = MediaAttachment(
                    id = java.util.UUID.randomUUID().toString(),
                    uriString = uri.toString(),
                    fileName = fileName,
                    mimeType = mimeType,
                    sizeBytes = fileSize
                )
                if (!viewModel.addAttachment(attachment)) toast(t.attachFull)
            } catch (_: Exception) {
                // If resolving selected URI fails, do not add attachment
            }
        }
    }

    val bubbles = uiState.bubbles
    val recipients = if (selectedContactCount > 0) selectedContactCount else totalContactCount
    val hasContent = bubbles.any { it.text.isNotBlank() } || attachments.isNotEmpty()
    val canSend = recipients != 0 && hasContent
    val allText = bubbles.joinToString(" ") { it.text }
    val emptyVars = listOfNotNull(
        t.companyName.takeIf { "{Company}" in allText && uiState.customVariables["Company"].isNullOrBlank() },
        t.promoName.takeIf { "{Custom_1}" in allText && uiState.customVariables["Custom_1"].isNullOrBlank() }
    )
    val accounts = listOf(AccountNode.PERSONAL, AccountNode.BUSINESS, AccountNode.DUAL_SYNC)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LargeTitle(text = t.title) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(c.card)
                    .springClickable(onClick = onOpenHelp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = t.help,
                    tint = c.blue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Which WhatsApp account sends, and whether it is linked
        SegmentedControl(
            options = listOf(t.personal, t.business, t.dual),
            selectedIndex = accounts.indexOf(uiState.selectedAccount),
            onSelect = { viewModel.selectAccount(accounts[it]) }
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            when (uiState.selectedAccount) {
                AccountNode.PERSONAL -> SessionStatus(node1Session, null)
                AccountNode.BUSINESS -> SessionStatus(node2Session, null)
                AccountNode.DUAL_SYNC -> {
                    SessionStatus(node1Session, t.personal)
                    SessionStatus(node2Session, t.business)
                }
            }
        }

        // Recipients
        GroupCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .springClickable(onClick = onOpenContacts)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconTile(icon = Icons.Default.Group, gradient = TileGradients.Blue, size = 36.dp)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = t.to, color = c.secondaryLabel, fontSize = 13.sp)
                    Text(
                        text = when {
                            selectedContactCount > 0 -> t.selectedContacts(selectedContactCount)
                            totalContactCount == null -> t.allContacts
                            totalContactCount == 0 -> t.noContacts
                            else -> t.allContactsCount(totalContactCount)
                        },
                        color = c.label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = c.tertiaryLabel,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Message bubbles, each with the files sent alongside it
        SectionHeader(text = t.writeMessage)
        // Ids from the last composition: a new id next to all old ones is a user "add";
        // a whole new set is an AI rewrite (new ids), which only fades its text in.
        val currentIds = bubbles.map { it.id }.toSet()
        val prevIds = remember { currentIds.toMutableSet() }
        val pureAdd = currentIds.size > prevIds.size && currentIds.containsAll(prevIds)
        SideEffect { prevIds.clear(); prevIds.addAll(currentIds) }
        // Bubbles playing their exit; counted so the last remaining one can't be removed too
        val exiting = remember { mutableStateListOf<String>() }
        val stayingCount = bubbles.size - exiting.count { it in currentIds }

        GroupCard(padding = 16.dp, spacing = 14.dp) {
            bubbles.forEachIndexed { index, bubble ->
              key(bubble.id) {
                val isNew = bubble.id !in prevIds
                val enter = remember {
                    if (pureAdd) {
                        expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) +
                            fadeIn(tween(250)) + slideInVertically(tween(300)) { it / 4 }
                    } else {
                        fadeIn(tween(350))
                    }
                }
                val visibleState = remember { MutableTransitionState(!isNew) }
                LaunchedEffect(Unit) { visibleState.targetState = true }
                // The bubble leaves the draft only after its exit animation ends. A new bubble also
                // starts idle and hidden, so require that the user actually tapped remove.
                if (bubble.id in exiting && visibleState.isIdle && !visibleState.currentState) {
                    LaunchedEffect(Unit) { viewModel.removeBubble(bubble.id) }
                }
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = enter,
                    exit = shrinkVertically(tween(250)) + fadeOut(tween(200))
                ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (index > 0) RowSeparator(startInset = 0.dp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = t.bubble(index + 1), color = c.blueOnTint, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        if (stayingCount > 1 && bubble.id !in exiting) {
                            IconButton(onClick = { exiting.add(bubble.id); visibleState.targetState = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = t.removeBubble(index + 1),
                                    tint = c.tertiaryLabel,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    attachments.filter { it.bubbleIndex == index }.forEach { att ->
                        AttachmentCard(
                            attachment = att,
                            bubbleCount = bubbles.size,
                            onPlacementChange = { viewModel.setAttachmentPlacement(att.id, it) },
                            onRemove = { viewModel.removeAttachment(att.id) }
                        )
                    }
                    TextField(
                        value = bubble.text,
                        onValueChange = { viewModel.updateBubbleText(bubble.id, it) },
                        placeholder = { Text(text = t.placeholder, color = c.tertiaryLabel, fontSize = 16.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, lineHeight = 24.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 96.dp)
                            // Variable pills insert into the bubble the user last tapped
                            .onFocusChanged { if (it.isFocused) viewModel.setActiveBubble(bubble.id) }
                            .aiShimmer(uiState.isAiRewriting),
                        shape = RoundedCornerShape(14.dp),
                        colors = appleFieldColors()
                    )
                }
                }
                }
              }
            }

            attachments.filter { it.bubbleIndex !in bubbles.indices }.forEach { att ->
                AttachmentCard(
                    attachment = att,
                    bubbleCount = bubbles.size,
                    onPlacementChange = { viewModel.setAttachmentPlacement(att.id, it) },
                    onRemove = { viewModel.removeAttachment(att.id) }
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TintPill(text = t.addNickname, background = c.blueTint, content = c.blueOnTint, onClick = { viewModel.insertVariable("{Nickname}") })
                TintPill(text = t.addCompany, background = c.orangeTint, content = c.orangeOnTint, onClick = { viewModel.insertVariable("{Company}") })
                TintPill(text = t.addPromo, background = c.orangeTint, content = c.orangeOnTint, onClick = { viewModel.insertVariable("{Custom_1}") })
                if (bubbles.size < 3) {
                    TintPill(text = t.addBubble, background = c.fill, content = c.label, onClick = { viewModel.addBubble("") })
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionTile(
                text = t.attach,
                icon = Icons.Default.AttachFile,
                gradient = TileGradients.Teal,
                modifier = Modifier.weight(1f),
                onClick = {
                    try {
                        mediaPickerLauncher.launch("*/*")
                    } catch (e: Exception) {
                        toast(strings.filePickerFailed(e.message.orEmpty()))
                    }
                }
            )
            ActionTile(
                text = if (uiState.isAiRewriting) t.aiWorking else t.aiTidy,
                icon = Icons.Default.AutoAwesome,
                gradient = TileGradients.Purple,
                modifier = Modifier.weight(1f),
                loading = uiState.isAiRewriting,
                onClick = {
                    if (bubbles.all { it.text.isBlank() }) toast(t.aiNeedText) else viewModel.triggerAiRewrite()
                }
            )
        }

        uiState.aiErrorMessage?.let { error ->
            Text(
                text = error,
                color = c.redOnTint,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(c.redTint)
                    .padding(12.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = t.aiStyle, color = c.secondaryLabel, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "friendly" to t.toneFriendly,
                    "shorter" to t.toneShorter,
                    "max_variance" to t.toneVaried
                ).forEach { (key, label) ->
                    val selected = uiState.selectedTone == key
                    TintPill(
                        text = label,
                        background = if (selected) c.purpleTint else c.fill,
                        content = if (selected) c.purpleOnTint else c.secondaryLabel,
                        onClick = { viewModel.selectTone(key) },
                        selected = selected
                    )
                }
            }
        }

        SectionHeader(text = t.preview)
        WhatsAppPreviewBubble(
            bubbles = bubbles,
            attachments = attachments,
            contactName = previewContactName?.takeIf { it.isNotBlank() } ?: t.sampleName,
            customVariables = uiState.customVariables
        )

        // More options: values for {Company}/{Custom_1}, word-variation help, delay info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(c.card.copy(alpha = 0.6f))
                .springClickable { showOptions = !showOptions }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = t.moreOptions, color = c.label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = t.moreOptionsHint, color = c.secondaryLabel, fontSize = 14.sp)
                Icon(
                    imageVector = if (showOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = c.tertiaryLabel,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        if (showOptions) {
            GroupCard(padding = 16.dp, spacing = 14.dp) {
                listOf(
                    Triple("Company", t.companyLabel, strings.composerCompanyPlaceholder),
                    Triple("Custom_1", t.custom1Label, strings.composerCustom1Placeholder)
                ).forEach { (key, label, hint) ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = label, color = c.label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        TextField(
                            value = uiState.customVariables[key].orEmpty(),
                            onValueChange = { viewModel.updateCustomVariable(key, it) },
                            placeholder = { Text(text = hint, color = c.tertiaryLabel, fontSize = 15.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = appleFieldColors()
                        )
                    }
                }
                Text(text = t.nicknameNote, color = c.secondaryLabel, fontSize = 14.sp, lineHeight = 20.sp)
                val range = antiBan.delayRangeSec
                listOf(
                    t.variationTitle to t.variationHelp,
                    t.delayTitle to t.delayInfo(range.first, range.last, antiBan.batchSize, antiBan.pauseDurationMin)
                ).forEach { (title, body) ->
                    RowSeparator(startInset = 0.dp)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = title, color = c.label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = body, color = c.secondaryLabel, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }
        }

        // {Company}/{Custom_1} in the text with no value would reach contacts as blanks
        if (emptyVars.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(c.orangeTint)
                    .springClickable { showOptions = true }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.WarningAmber, contentDescription = null, tint = c.orangeOnTint, modifier = Modifier.size(20.dp))
                Text(text = t.emptyVarsWarning(emptyVars.joinToString(" & ")), color = c.orangeOnTint, fontSize = 14.sp)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SendButton(
                text = if (recipients == null) t.sendAll else t.sendTo(recipients),
                enabled = canSend,
                onSend = onLaunchCampaign
            )
            Text(
                text = when {
                    recipients == 0 -> t.needContacts
                    !hasContent -> t.needMessage
                    recipients == null -> t.safeDelayOn
                    else -> t.estimate(t.duration(estimateMinutes(recipients, bubbles.count { it.text.isNotBlank() }, antiBan)))
                },
                color = c.secondaryLabel,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/** Rough send time: average contact delay, 4.5 s typing between bubbles, plus batch cooldowns. */
private fun estimateMinutes(contacts: Int, bubbleCount: Int, config: AntiBanConfig): Int {
    val r = config.delayRangeSec
    val perContact = (r.first + r.last) / 2.0 + (bubbleCount - 1).coerceAtLeast(0) * 4.5
    val cooldowns = (contacts - 1).coerceAtLeast(0) / config.batchSize * config.pauseDurationMin * 60
    return ceil((contacts * perContact + cooldowns) / 60).toInt().coerceAtLeast(1)
}

@Composable
private fun SessionStatus(session: NodeSession, prefix: String?) {
    val c = AppleTheme.colors
    val t = composerTexts()
    val (text, dot, color) = when (session.status) {
        NodeStatus.CONNECTED -> Triple(t.connected(session.phoneNumber), c.green, c.greenOnTint)
        NodeStatus.PAIRING -> Triple(t.connecting, c.orange, c.orangeOnTint)
        NodeStatus.ERROR -> Triple(t.notConnected, c.orange, c.orangeOnTint)
        NodeStatus.DISCONNECTED -> Triple(t.notConnected, c.tertiaryLabel, c.secondaryLabel)
    }
    StatusLabel(
        text = if (prefix == null) text else "$prefix: $text",
        color = dot,
        textColor = color,
        modifier = Modifier.padding(start = 16.dp)
    )
}

@Composable
private fun ActionTile(
    text: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    val c = AppleTheme.colors
    GroupCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .springClickable(enabled = !loading, onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                // Same tile as IconTile, but the sparkle turns slowly while the AI works
                val spin = rememberInfiniteTransition(label = "aiSpin")
                val angle by spin.animateFloat(0f, 360f, infiniteRepeatable(tween(2400, easing = LinearEasing)), label = "aiAngle")
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(34.dp * 0.3f))
                        .background(Brush.linearGradient(gradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp * 0.56f).graphicsLayer { rotationZ = angle }
                    )
                }
            } else {
                IconTile(icon = icon, gradient = gradient)
            }
            Text(text = text, color = c.label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 2)
        }
    }
}

/** Moving purple border around a field while the AI rewrites it. */
@Composable
private fun Modifier.aiShimmer(active: Boolean): Modifier {
    if (!active) return this
    val purple = AppleTheme.colors.purple
    val sweep = rememberInfiniteTransition(label = "aiShimmer")
    val progress by sweep.animateFloat(0f, 1f, infiniteRepeatable(tween(1400, easing = LinearEasing)), label = "aiShimmerProgress")
    return drawWithContent {
        drawContent()
        val shift = progress * size.width * 2
        drawRoundRect(
            brush = Brush.linearGradient(
                listOf(purple.copy(alpha = 0.15f), purple, purple.copy(alpha = 0.15f)),
                start = Offset(shift - size.width, 0f),
                end = Offset(shift, size.height),
                tileMode = TileMode.Mirror
            ),
            cornerRadius = CornerRadius(14.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

/**
 * PrimaryCapsuleButton with a paper plane that flies off up-right before [onSend] fires.
 * Same look as the theme button; kept here because the plane animation lives inside it.
 */
@Composable
private fun SendButton(text: String, enabled: Boolean, onSend: () -> Unit) {
    val gradient = TileGradients.Send
    val shape = RoundedCornerShape(28.dp)
    val scope = rememberCoroutineScope()
    val currentOnSend by rememberUpdatedState(onSend)
    val flight = remember { Animatable(0f) }
    var busy by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(if (enabled) 10.dp else 0.dp, shape, ambientColor = gradient.last(), spotColor = gradient.last())
            .clip(shape)
            .background(if (enabled) Brush.linearGradient(gradient) else Brush.linearGradient(listOf(Color(0xFFC7C7CC), Color(0xFFAEAEB2))))
            .semantics { role = Role.Button }
            .then(
                if (enabled) {
                    Modifier.springClickable(enabled = !flight.isRunning, haptic = true) {
                        // Send first: the flight is decoration and must never be able to cancel a send.
                        // The busy flag blocks a fast double tap from queueing the campaign twice.
                        if (busy) return@springClickable
                        busy = true
                        currentOnSend()
                        scope.launch {
                            flight.animateTo(1f, tween(250, easing = FastOutLinearInEasing))
                            flight.snapTo(0f)
                            busy = false
                        }
                    }
                } else Modifier
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer {
                    val p = flight.value
                    translationX = p * 48.dp.toPx()
                    translationY = -p * 32.dp.toPx()
                    rotationZ = -p * 20f
                    alpha = 1f - p
                }
        )
        Spacer(Modifier.width(10.dp))
        Text(text = text, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun appleFieldColors(): TextFieldColors {
    val c = AppleTheme.colors
    return TextFieldDefaults.colors(
        focusedContainerColor = c.cardInset,
        unfocusedContainerColor = c.cardInset,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedTextColor = c.label,
        unfocusedTextColor = c.label,
        cursorColor = c.blue
    )
}
