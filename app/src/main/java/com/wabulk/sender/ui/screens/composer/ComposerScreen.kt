package com.wabulk.sender.ui.screens.composer

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.domain.model.MediaAttachment
import com.wabulk.sender.ui.screens.composer.components.MediaCarousel
import com.wabulk.sender.ui.screens.composer.components.WhatsAppPreviewBubble
import com.wabulk.sender.theme.EmeraldDarkSurfaceContainer
import com.wabulk.sender.theme.EmeraldDarkSurfaceHigh
import com.wabulk.sender.theme.EmeraldDarkSurfaceLow
import com.wabulk.sender.theme.EmeraldDarkSurfaceLowest
import com.wabulk.sender.theme.EmeraldOnPrimaryContainer
import com.wabulk.sender.theme.EmeraldPrimary
import com.wabulk.sender.theme.EmeraldPrimaryContainer
import com.wabulk.sender.theme.EmeraldSecondaryContainer
import com.wabulk.sender.theme.EmeraldSecondaryFixedVariant
import com.wabulk.sender.theme.EmeraldTertiary
import com.wabulk.sender.theme.StatusError
import com.wabulk.sender.theme.TextCrispPrimary
import com.wabulk.sender.theme.TextMutedBody
import com.wabulk.sender.theme.TextSubduedTertiary
import com.wabulk.sender.theme.pulseGlow
import com.wabulk.sender.theme.springClickable

@Composable
fun ComposerScreen(
    viewModel: ComposerViewModel,
    selectedContactCount: Int = 0,
    modifier: Modifier = Modifier,
    onLaunchCampaign: () -> Unit = {}
) {
    val strings = LocalAppStrings.current
    val uiState by viewModel.uiState.collectAsState()
    val attachments by viewModel.attachments.collectAsState()
    val context = LocalContext.current
    var showHelpDialog by remember { mutableStateOf(false) }

    var sampleMediaIndex by remember { mutableIntStateOf(0) }
    val sampleMediaPool = remember {
        listOf(
            MediaAttachment(
                uriString = "dummy://promo.jpg",
                fileName = "PROMO-2026.JPG",
                mimeType = "image/jpeg",
                sizeBytes = 1258291L
            ),
            MediaAttachment(
                uriString = "dummy://demo.mp4",
                fileName = "DEMO-CLIP.MP4",
                mimeType = "video/mp4",
                sizeBytes = 4194304L
            ),
            MediaAttachment(
                uriString = "dummy://catalog.pdf",
                fileName = "Catalog_v2.pdf",
                mimeType = "application/pdf",
                sizeBytes = 348160L
            )
        )
    }

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
                viewModel.addAttachment(attachment)
            } catch (_: Exception) {
                // If resolving selected URI fails, do not add attachment
            }
        }
    }

    // Seed default sample attachments on initial composition
    LaunchedEffect(Unit) {
        if (attachments.isEmpty()) {
            viewModel.addAttachment(sampleMediaPool[0])
            viewModel.addAttachment(sampleMediaPool[2])
        }
    }

    val scrollState = rememberScrollState()
    var showVariableConfig by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Meta & Account Status Tier
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = strings.composerTitle,
                        color = TextCrispPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EmeraldDarkSurfaceHigh)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = strings.campaignTagDraft,
                            color = TextMutedBody,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(EmeraldDarkSurfaceContainer)
                        .springClickable { showHelpDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "Help",
                        tint = TextMutedBody,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Segmented Account Switcher Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(EmeraldDarkSurfaceLowest)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // WA Personal
                val personalSelected = uiState.selectedAccount == AccountNode.PERSONAL
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(if (personalSelected) EmeraldSecondaryFixedVariant else Color.Transparent)
                        .springClickable { viewModel.selectAccount(AccountNode.PERSONAL) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (personalSelected) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                                    .pulseGlow()
                            )
                        }
                        Text(
                            text = strings.composerAccountPersonal,
                            color = if (personalSelected) EmeraldPrimary else TextMutedBody,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // WA Business
                val businessSelected = uiState.selectedAccount == AccountNode.BUSINESS
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(if (businessSelected) EmeraldSecondaryFixedVariant else Color.Transparent)
                        .springClickable { viewModel.selectAccount(AccountNode.BUSINESS) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BusinessCenter,
                            contentDescription = null,
                            tint = if (businessSelected) EmeraldPrimary else TextMutedBody,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = strings.composerAccountBusiness,
                            color = if (businessSelected) EmeraldPrimary else TextMutedBody,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Dual Sync
                val dualSelected = uiState.selectedAccount == AccountNode.DUAL_SYNC
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(if (dualSelected) EmeraldSecondaryFixedVariant else Color.Transparent)
                        .springClickable { viewModel.selectAccount(AccountNode.DUAL_SYNC) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.composerAccountDual,
                        color = if (dualSelected) EmeraldPrimary else TextMutedBody,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Active Line & Anti-Ban Shield Tier
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.SimCard,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "+1 (555) 019-2834",
                        color = TextCrispPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = strings.composerPrimaryNode,
                        color = EmeraldTertiary,
                        fontSize = 10.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldSecondaryFixedVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = strings.antiBanLevel3,
                        color = EmeraldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Media Carousel Section
        MediaCarousel(
            attachments = attachments,
            onAddAttachment = {
                try {
                    mediaPickerLauncher.launch("*/*")
                } catch (_: Exception) {
                    val nextAttachment = sampleMediaPool[sampleMediaIndex % sampleMediaPool.size]
                    sampleMediaIndex++
                    viewModel.addAttachment(nextAttachment.copy(id = java.util.UUID.randomUUID().toString()))
                }
            },
            onRemoveAttachment = { viewModel.removeAttachment(it) }
        )

        // Multi-Bubble Message Editor & Variable Insertions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(EmeraldDarkSurfaceLow)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Quick Insert Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.DataObject,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = strings.composerSpintaxTitle,
                        color = TextCrispPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = strings.composerSpintaxTapToInsert,
                    color = EmeraldTertiary,
                    fontSize = 10.sp
                )
            }

            // Variable Insert Pills Horizontal Scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val variables = listOf("{Nickname}", "{Company}", "{Custom_1}")
                variables.forEachIndexed { index, token ->
                    val isPrimary = index == 0
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isPrimary) EmeraldPrimaryContainer else EmeraldDarkSurfaceHigh)
                            .springClickable {
                                viewModel.insertVariable(token)
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "+ $token",
                            color = if (isPrimary) EmeraldOnPrimaryContainer else TextCrispPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // Variable Value Configuration Panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmeraldDarkSurfaceLowest)
                    .springClickable { showVariableConfig = !showVariableConfig }
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Atur Nilai {Company} & {Custom_1}",
                        color = EmeraldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = if (showVariableConfig) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextMutedBody,
                    modifier = Modifier.size(16.dp)
                )
            }

            if (showVariableConfig) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldDarkSurfaceLowest)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Variable Company input
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Nilai {Company} (Nama Bisnis / Brand):",
                            color = TextCrispPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        TextField(
                            value = uiState.customVariables["Company"] ?: "",
                            onValueChange = { viewModel.updateCustomVariable("Company", it) },
                            placeholder = {
                                Text(
                                    text = "Misal: KirimRasa / Toko Berkah",
                                    color = TextSubduedTertiary,
                                    fontSize = 12.sp
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = EmeraldDarkSurfaceHigh,
                                unfocusedContainerColor = EmeraldDarkSurfaceHigh,
                                focusedIndicatorColor = EmeraldPrimary,
                                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                focusedTextColor = TextCrispPrimary,
                                unfocusedTextColor = TextCrispPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Variable Custom_1 input
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Nilai {Custom_1} (Kode Promo / Link / Info Bebas):",
                            color = TextCrispPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        TextField(
                            value = uiState.customVariables["Custom_1"] ?: "",
                            onValueChange = { viewModel.updateCustomVariable("Custom_1", it) },
                            placeholder = {
                                Text(
                                    text = "Misal: PROMO30 / https://wa.me/...",
                                    color = TextSubduedTertiary,
                                    fontSize = 12.sp
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = EmeraldDarkSurfaceHigh,
                                unfocusedContainerColor = EmeraldDarkSurfaceHigh,
                                focusedIndicatorColor = EmeraldPrimary,
                                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                focusedTextColor = TextCrispPrimary,
                                unfocusedTextColor = TextCrispPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Note regarding {Nickname}
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = EmeraldTertiary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "{Nickname} diambil otomatis dari nama panggilan kontak masing-masing di Tab Kontak.",
                            color = TextMutedBody,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // List of Multi-Bubble Cards
            uiState.bubbles.forEachIndexed { index, bubble ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(EmeraldDarkSurfaceLowest)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Bubble Header (Balon #1, Char Count, Delete Button)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                            )
                            Text(
                                text = "Balon #${index + 1}",
                                color = EmeraldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = strings.composerCharCount(bubble.text.length, 1),
                                color = TextMutedBody,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            if (uiState.bubbles.size > 1) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldDarkSurfaceHigh)
                                        .springClickable { viewModel.removeBubble(bubble.id) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hapus Balon",
                                        tint = StatusError,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Text Field for this bubble
                    TextField(
                        value = bubble.text,
                        onValueChange = { viewModel.updateBubbleText(bubble.id, it) },
                        placeholder = {
                            Text(
                                text = strings.composerPlaceholder,
                                color = TextSubduedTertiary,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(95.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextCrispPrimary,
                            unfocusedTextColor = TextCrispPrimary
                        )
                    )
                }
            }

            // + Tambah Balon Pesan (if < 3 bubbles)
            if (uiState.bubbles.size < 3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldDarkSurfaceLowest)
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .springClickable { viewModel.addBubble("") }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "+ Tambah Balon Pesan (Maks 3)",
                            color = EmeraldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // AI Rewrite Action & Tone Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // AI Error Message banner if present
                if (uiState.aiErrorMessage != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF3B1818))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = StatusError,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = uiState.aiErrorMessage ?: "",
                            color = Color(0xFFFFB4AB),
                            fontSize = 11.sp
                        )
                    }
                }

                // AI Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(EmeraldSecondaryFixedVariant, EmeraldSecondaryContainer, EmeraldSecondaryFixedVariant)
                            )
                        )
                        .then(
                            if (!uiState.isAiRewriting) Modifier.springClickable { viewModel.triggerAiRewrite() }
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.isAiRewriting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = EmeraldPrimary,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Menghasilkan variasi AI & Spintax...",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .pulseGlow()
                            )
                            Text(
                                text = strings.composerAiRewriteBtn,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Tone Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Tone 1: Friendly
                    val friendlySelected = uiState.selectedTone == "friendly"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (friendlySelected) EmeraldPrimaryContainer else EmeraldDarkSurfaceHigh)
                            .springClickable { viewModel.selectTone("friendly") }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.composerToneFriendly,
                            color = if (friendlySelected) EmeraldOnPrimaryContainer else TextMutedBody,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Tone 2: Shorter
                    val shorterSelected = uiState.selectedTone == "shorter"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (shorterSelected) EmeraldPrimaryContainer else EmeraldDarkSurfaceHigh)
                            .springClickable { viewModel.selectTone("shorter") }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.composerToneShorter,
                            color = if (shorterSelected) EmeraldOnPrimaryContainer else TextMutedBody,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Tone 3: Max Variance
                    val maxVarianceSelected = uiState.selectedTone == "max_variance"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (maxVarianceSelected) EmeraldPrimaryContainer else EmeraldDarkSurfaceHigh)
                            .springClickable { viewModel.selectTone("max_variance") }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.composerToneMaxVariance,
                            color = if (maxVarianceSelected) EmeraldOnPrimaryContainer else TextMutedBody,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Real-Time WhatsApp Recipient Preview Container
        WhatsAppPreviewBubble(
            bubbles = uiState.bubbles,
            attachments = attachments,
            contactName = "Sarah Miller",
            contactInitials = "S",
            customVariables = uiState.customVariables
        )

        // Sticky Humanizer Safety Controls & Primary Action
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(EmeraldDarkSurfaceHigh)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.composerDynamicJitterTitle,
                        color = TextCrispPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldSecondaryFixedVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                    )
                    Text(
                        text = strings.composerSafeDelayRange,
                        color = EmeraldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Graphic Gauge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(EmeraldDarkSurfaceLowest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(8.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(EmeraldSecondaryContainer, EmeraldPrimaryContainer, EmeraldPrimary)
                            )
                        )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = strings.composerJitterFast, color = TextMutedBody, fontSize = 10.sp)
                Text(text = strings.composerJitterMetaCompliant, color = EmeraldTertiary, fontSize = 10.sp)
                Text(text = strings.composerJitterRelaxed, color = TextMutedBody, fontSize = 10.sp)
            }

            // Primary Launch Bulk Broadcast CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(EmeraldPrimary, Color(0xFF66FF8E), EmeraldPrimary)
                        )
                    )
                    .springClickable { onLaunchCampaign() }
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = Color(0xFF002109),
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = strings.composerLaunchBtn,
                            color = Color(0xFF002109),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF002109))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = strings.composerLaunchContactsCount(if (selectedContactCount > 0) selectedContactCount else 42),
                            color = EmeraldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Modal Panduan / Bantuan Composer
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text("Panduan Pesan & Variabel", color = TextCrispPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "1. Spintax {A|B|C}: Memutar variasi kata secara otomatis agar tiap pesan ke penerima berbeda dan tidak terdeteksi bot/spam.",
                        color = TextMutedBody,
                        fontSize = 12.sp
                    )
                    Text(
                        "2. {Nickname}: Nama panggilan unik yang otomatis diambil dari Tab Kontak.",
                        color = TextMutedBody,
                        fontSize = 12.sp
                    )
                    Text(
                        "3. {Company} & {Custom_1}: Variabel pengirim dan promo yang dapat Anda ubah nilainya pada menu 'Atur Nilai {Company}'.",
                        color = TextMutedBody,
                        fontSize = 12.sp
                    )
                    Text(
                        "4. Multi-Balon: Memecah pesan panjang menjadi 2-3 balon obrolan dengan jeda mengetik 3 detik untuk simulasi mengetik manusia.",
                        color = EmeraldTertiary,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Tutup", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = EmeraldDarkSurfaceContainer
        )
    }
}
