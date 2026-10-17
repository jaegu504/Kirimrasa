package com.wabulk.sender.ui.screens.settings

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wabulk.sender.data.locale.AppLanguage
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.engine.model.NodeStatus
import com.wabulk.sender.theme.EmeraldDarkSurfaceContainer
import com.wabulk.sender.theme.EmeraldDarkSurfaceHigh
import com.wabulk.sender.theme.EmeraldDarkSurfaceHighest
import com.wabulk.sender.theme.EmeraldDarkSurfaceLow
import com.wabulk.sender.theme.EmeraldDarkSurfaceLowest
import com.wabulk.sender.theme.EmeraldOnPrimaryContainer
import com.wabulk.sender.theme.EmeraldPrimary
import com.wabulk.sender.theme.EmeraldPrimaryContainer
import com.wabulk.sender.theme.EmeraldSecondary
import com.wabulk.sender.theme.EmeraldSecondaryContainer
import com.wabulk.sender.theme.EmeraldTertiary
import com.wabulk.sender.theme.StatusError
import com.wabulk.sender.theme.StatusErrorContainer
import com.wabulk.sender.theme.TextCrispPrimary
import com.wabulk.sender.theme.TextMutedBody
import com.wabulk.sender.theme.TextSubduedTertiary
import com.wabulk.sender.theme.pulseGlow
import com.wabulk.sender.theme.springClickable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    viewModel: SettingsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val coroutineScope = rememberCoroutineScope()

    val node1Session by viewModel.node1Session.collectAsState()
    val node2Session by viewModel.node2Session.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val typingSimEnabled by viewModel.typingSimEnabled.collectAsState()
    val autoRotateEnabled by viewModel.autoRotateEnabled.collectAsState()
    val pauseDurationMin by viewModel.pauseDurationMin.collectAsState()
    val batchSizeMsgs by viewModel.batchSizeMsgs.collectAsState()
    val selectedAiTone by viewModel.selectedAiTone.collectAsState()
    val baileysConfig by viewModel.baileysConfig.collectAsState()
    val gatewayTestResult by viewModel.gatewayTestResult.collectAsState()
    val isTestingGateway by viewModel.isTestingGateway.collectAsState()

    var pingState by remember { mutableStateOf<String?>(null) }
    var codeCopiedState by remember { mutableStateOf(false) }
    var pairingSecondsLeft by remember { mutableIntStateOf(165) }
    var showQrDialog by remember { mutableStateOf(false) }

    // Countdown simulation for 8-digit pairing code
    LaunchedEffect(node2Session.pairingCode) {
        pairingSecondsLeft = 180
        while (pairingSecondsLeft > 0) {
            delay(1000)
            pairingSecondsLeft--
        }
    }

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
                        text = strings.settingsShieldActive.uppercase(),
                        color = EmeraldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldDarkSurfaceHigh)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                            .pulseGlow()
                    )
                    Text(
                        text = strings.settingsGuardrailVersion,
                        color = TextMutedBody,
                        fontSize = 10.sp
                    )
                }
            }

            Text(
                text = strings.settingsTitle,
                color = TextCrispPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = strings.settingsSubtitle,
                color = TextMutedBody,
                fontSize = 11.sp
            )
        }

        // LANGUAGE SELECTOR CARD (Indonesian & Turkish)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(EmeraldDarkSurfaceLowest)
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
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.settingsLanguageTitle,
                        color = TextCrispPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = currentLanguage.displayName,
                        color = EmeraldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = strings.settingsLanguageSubtitle,
                color = TextMutedBody,
                fontSize = 11.sp
            )

            // Language Switcher Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Indonesian Button
                val isIndonesian = currentLanguage == AppLanguage.INDONESIAN
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isIndonesian) EmeraldPrimaryContainer else EmeraldDarkSurfaceHigh)
                        .springClickable { onLanguageSelected(AppLanguage.INDONESIAN) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🇮🇩", fontSize = 16.sp)
                        Text(
                            text = strings.settingsSelectIndonesian,
                            color = if (isIndonesian) EmeraldOnPrimaryContainer else TextCrispPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isIndonesian) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = EmeraldOnPrimaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Turkish Button
                val isTurkish = currentLanguage == AppLanguage.TURKISH
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isTurkish) EmeraldPrimaryContainer else EmeraldDarkSurfaceHigh)
                        .springClickable { onLanguageSelected(AppLanguage.TURKISH) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🇹🇷", fontSize = 16.sp)
                        Text(
                            text = strings.settingsSelectTurkish,
                            color = if (isTurkish) EmeraldOnPrimaryContainer else TextCrispPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isTurkish) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = EmeraldOnPrimaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // SECTION 1: Connected WhatsApp Engines
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.settingsEnginesTitle,
                        color = TextCrispPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldDarkSurfaceHighest)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = strings.settingsEnginesCount,
                        color = TextMutedBody,
                        fontSize = 10.sp
                    )
                }
            }

            // Node 1: WhatsApp Personal
            val isNode1Connected = node1Session.status == NodeStatus.CONNECTED
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldDarkSurfaceLowest)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isNode1Connected) EmeraldPrimary.copy(alpha = 0.15f) else StatusErrorContainer.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = if (isNode1Connected) EmeraldPrimary else StatusError,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = strings.settingsNode1Title,
                                    color = TextCrispPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldDarkSurfaceHigh)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = strings.settingsNode1NodeNum,
                                        color = TextCrispPrimary,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Text(
                                text = if (isNode1Connected) node1Session.phoneNumber else "Node Tidak Terhubung",
                                color = TextMutedBody,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isNode1Connected) EmeraldPrimary.copy(alpha = 0.15f) else StatusErrorContainer.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isNode1Connected) EmeraldPrimary else StatusError)
                        )
                        Text(
                            text = if (isNode1Connected) strings.settingsConnected else "Terputus",
                            color = if (isNode1Connected) EmeraldPrimary else StatusError,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Stats Strip
                if (isNode1Connected) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldDarkSurfaceLow)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Timelapse, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(13.dp))
                            Text(text = "${strings.settingsUptime} 48h 12m", color = TextCrispPrimary, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(13.dp))
                            Text(text = "${strings.settingsHealth} 99.4%", color = TextCrispPrimary, fontSize = 10.sp)
                        }
                    }
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isNode1Connected) {
                        // Test Ping Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldDarkSurfaceHigh)
                                .springClickable {
                                    coroutineScope.launch {
                                        pingState = strings.settingsPinging
                                        viewModel.pingNode(AccountNode.PERSONAL)
                                        delay(500)
                                        pingState = strings.settingsPongResult
                                        delay(1500)
                                        pingState = null
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (pingState != null) Icons.Default.Sync else Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = if (pingState == strings.settingsPongResult) EmeraldPrimary else TextCrispPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = pingState ?: strings.settingsTestPingBtn,
                                    color = if (pingState == strings.settingsPongResult) EmeraldPrimary else TextCrispPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Disconnect Button
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .clip(CircleShape)
                                .background(StatusErrorContainer.copy(alpha = 0.4f))
                                .springClickable {
                                    viewModel.disconnectNode(AccountNode.PERSONAL)
                                }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.LinkOff, contentDescription = null, tint = StatusError, modifier = Modifier.size(14.dp))
                                Text(text = strings.settingsDisconnectBtn, color = StatusError, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Reconnect Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimaryContainer)
                                .springClickable {
                                    viewModel.connectNode(AccountNode.PERSONAL, "+1 (555) 019-2834")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = EmeraldOnPrimaryContainer, modifier = Modifier.size(15.dp))
                                Text(text = "Hubungkan Ulang Node 1", color = EmeraldOnPrimaryContainer, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Node 2: WhatsApp Business (Pairing Ready with 8-Digit Pairing Code)
            val isNode2Connected = node2Session.status == NodeStatus.CONNECTED
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldDarkSurfaceLowest)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isNode2Connected) EmeraldPrimary.copy(alpha = 0.15f) else EmeraldSecondaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = null,
                                tint = if (isNode2Connected) EmeraldPrimary else EmeraldSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = strings.settingsNode2Title,
                                    color = TextCrispPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldDarkSurfaceHigh)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = strings.settingsNode2NodeNum,
                                        color = TextCrispPrimary,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Text(
                                text = if (isNode2Connected) node2Session.phoneNumber else "WhatsApp Business Engine",
                                color = TextMutedBody,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isNode2Connected) EmeraldPrimary.copy(alpha = 0.15f) else EmeraldSecondaryContainer.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isNode2Connected) strings.settingsConnected else strings.settingsPairingReady,
                            color = if (isNode2Connected) EmeraldPrimary else EmeraldSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!isNode2Connected) {
                    Text(
                        text = strings.settingsPairingDescription,
                        color = TextMutedBody,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    // 8-Digit Pairing Code Display
                    val rawCode = node2Session.pairingCode.ifEmpty { "K97B-42VX" }
                    val parts = rawCode.split("-")
                    val codeGroup1 = parts.getOrNull(0)?.map { it.toString() } ?: listOf("K", "9", "7", "B")
                    val codeGroup2 = parts.getOrNull(1)?.map { it.toString() } ?: listOf("4", "2", "V", "X")

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldDarkSurfaceLow)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            codeGroup1.forEach { char ->
                                Box(
                                    modifier = Modifier
                                        .size(width = 30.dp, height = 36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(EmeraldDarkSurfaceLowest),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        color = EmeraldPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Text(
                                text = "-",
                                color = TextMutedBody,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )

                            codeGroup2.forEach { char ->
                                Box(
                                    modifier = Modifier
                                        .size(width = 30.dp, height = 36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(EmeraldDarkSurfaceLowest),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        color = EmeraldPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Countdown Timer & Refresh button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            val mins = pairingSecondsLeft / 60
                            val secs = pairingSecondsLeft % 60
                            Text(
                                text = "${strings.settingsExpiresIn} %02d:%02d".format(mins, secs),
                                color = TextMutedBody,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(EmeraldDarkSurfaceHighest)
                                    .springClickable {
                                        viewModel.requestNewPairingCode(AccountNode.BUSINESS)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(11.dp))
                                    Text(text = "Kode Baru", color = EmeraldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Copy Code & Open WhatsApp Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                            .springClickable {
                                codeCopiedState = true
                                viewModel.copyCodeAndOpenWhatsApp(rawCode)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        LaunchedEffect(codeCopiedState) {
                            if (codeCopiedState) {
                                delay(1800)
                                codeCopiedState = false
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (codeCopiedState) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = Color(0xFF003915),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (codeCopiedState) strings.settingsCodeCopied else strings.settingsCopyOpenBtn,
                                color = Color(0xFF003915),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (!codeCopiedState) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = Color(0xFF003915),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Switch to QR button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .springClickable { showQrDialog = true },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = TextMutedBody, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = strings.settingsSwitchToQr, color = TextMutedBody, fontSize = 10.sp)
                    }
                } else {
                    // Connected Node 2 actions
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .clip(CircleShape)
                            .background(StatusErrorContainer.copy(alpha = 0.4f))
                            .springClickable { viewModel.disconnectNode(AccountNode.BUSINESS) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.LinkOff, contentDescription = null, tint = StatusError, modifier = Modifier.size(14.dp))
                            Text(text = "Putuskan Node 2", color = StatusError, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Baileys Bridge Gateway Configuration Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(EmeraldDarkSurfaceLowest)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Title + Toggle Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (baileysConfig.isEnabled) EmeraldPrimary.copy(alpha = 0.15f) else EmeraldDarkSurfaceHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = if (baileysConfig.isEnabled) EmeraldPrimary else TextMutedBody,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Baileys Bridge Gateway",
                                color = TextCrispPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (baileysConfig.isEnabled) EmeraldSecondaryContainer.copy(alpha = 0.4f) else EmeraldDarkSurfaceHigh)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (baileysConfig.isEnabled) "AKTIF" else "NONAKTIF",
                                    color = if (baileysConfig.isEnabled) EmeraldSecondary else TextMutedBody,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Gateway multi-device tanpa scan berulang",
                            color = TextMutedBody,
                            fontSize = 10.sp
                        )
                    }
                }

                Switch(
                    checked = baileysConfig.isEnabled,
                    onCheckedChange = { viewModel.toggleBaileysEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = EmeraldPrimary,
                        uncheckedThumbColor = TextMutedBody,
                        uncheckedTrackColor = EmeraldDarkSurfaceHigh
                    )
                )
            }

            // Description text
            Text(
                text = "Gunakan gateway server Node.js Baileys lokal atau remote untuk pengiriman pesan massal berkecepatan tinggi dengan multi-device protocol.",
                color = TextMutedBody,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            // URL Gateway Input
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "URL GATEWAY",
                    color = TextCrispPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                BasicTextField(
                    value = baileysConfig.gatewayUrl,
                    onValueChange = { viewModel.updateGatewayUrl(it) },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = TextCrispPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    cursorBrush = SolidColor(EmeraldPrimary),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldDarkSurfaceLow)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (baileysConfig.gatewayUrl.isEmpty()) {
                                Text(
                                    text = "http://10.0.2.2:3000",
                                    color = TextSubduedTertiary,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            // API Key Input
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "API KEY (OPSIONAL)",
                    color = TextCrispPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                BasicTextField(
                    value = baileysConfig.apiKey,
                    onValueChange = { viewModel.updateApiKey(it) },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = TextCrispPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    cursorBrush = SolidColor(EmeraldPrimary),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldDarkSurfaceLow)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (baileysConfig.apiKey.isEmpty()) {
                                Text(
                                    text = "Bearer token atau secret key (kosongkan jika tanpa auth)",
                                    color = TextSubduedTertiary,
                                    fontSize = 12.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            // Action: Test Gateway Connection Button & Result Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldDarkSurfaceHigh)
                        .springClickable { viewModel.testGatewayConnection() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isTestingGateway) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(13.dp),
                                color = EmeraldPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = if (isTestingGateway) "Menguji..." else "Uji Koneksi Gateway",
                            color = TextCrispPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Result Pill
                if (gatewayTestResult != null) {
                    val isSuccess = gatewayTestResult!!.isSuccess
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSuccess) EmeraldPrimaryContainer else StatusErrorContainer)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (isSuccess) EmeraldOnPrimaryContainer else StatusError,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isSuccess) "Terhubung (${gatewayTestResult!!.latencyMs}ms)" else "Gagal (${gatewayTestResult!!.message})",
                                color = if (isSuccess) EmeraldOnPrimaryContainer else StatusError,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // SECTION 2: Anti-Ban Humanizer Rules
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                    Text(text = strings.settingsAntiBanTitle, color = TextCrispPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldSecondaryContainer.copy(alpha = 0.4f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(imageVector = Icons.Default.ThumbUp, contentDescription = null, tint = EmeraldSecondary, modifier = Modifier.size(11.dp))
                        Text(text = strings.settingsRecommended, color = EmeraldSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Rule 1: Jitter Range
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldDarkSurfaceLowest)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = strings.settingsJitterTitle, color = TextCrispPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = strings.settingsJitterSubtitle, color = TextMutedBody, fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (selectedPreset) {
                                0 -> "35s - 75s"
                                1 -> "25s - 65s"
                                else -> "15s - 30s"
                            },
                            color = EmeraldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Range Graphic Track
                val trackFraction = when (selectedPreset) {
                    0 -> 0.75f
                    1 -> 0.55f
                    else -> 0.35f
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(EmeraldDarkSurfaceHigh)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(trackFraction)
                            .height(8.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Min: 10s", color = TextMutedBody, fontSize = 10.sp)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(EmeraldDarkSurfaceLow)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(imageVector = Icons.Default.StackedLineChart, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(11.dp))
                        Text(text = "Gaussian Distribution", color = EmeraldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(text = "Max: 120s", color = TextMutedBody, fontSize = 10.sp)
                }

                // Preset Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(
                        0 to strings.settingsPresetConservative,
                        1 to strings.settingsPresetOptimal,
                        2 to strings.settingsPresetTurbo
                    )
                    presets.forEach { (idx, label) ->
                        val isSelected = selectedPreset == idx
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) EmeraldPrimary else EmeraldDarkSurfaceHigh)
                                .springClickable { viewModel.selectPreset(idx) }
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color(0xFF003915) else TextMutedBody,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Rule 2: Typing Simulation Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldDarkSurfaceLowest)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = strings.settingsTypingSimTitle, color = TextCrispPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(EmeraldDarkSurfaceHigh)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(text = "3s - 5s", color = TextMutedBody, fontSize = 9.sp)
                        }
                    }
                    Text(text = strings.settingsTypingSimSubtitle, color = TextMutedBody, fontSize = 10.sp)
                }

                Switch(
                    checked = typingSimEnabled,
                    onCheckedChange = { viewModel.toggleTypingSim(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = EmeraldPrimary,
                        uncheckedThumbColor = TextMutedBody,
                        uncheckedTrackColor = EmeraldDarkSurfaceHigh
                    )
                )
            }

            // Rule 3: Auto-Rotate Engines Toggle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldDarkSurfaceLowest)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = strings.settingsAutoRotateTitle, color = TextCrispPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmeraldSecondaryContainer.copy(alpha = 0.4f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(text = "Round-Robin", color = EmeraldSecondary, fontSize = 9.sp)
                            }
                        }
                        Text(text = strings.settingsAutoRotateSubtitle, color = TextMutedBody, fontSize = 10.sp)
                    }

                    Switch(
                        checked = autoRotateEnabled,
                        onCheckedChange = { viewModel.toggleAutoRotate(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmeraldPrimary,
                            uncheckedThumbColor = TextMutedBody,
                            uncheckedTrackColor = EmeraldDarkSurfaceHigh
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(imageVector = Icons.Default.SyncAlt, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(13.dp))
                    Text(text = strings.settingsAutoRotatePattern, color = EmeraldPrimary, fontSize = 10.sp)
                }
            }

            // Rule 4: Batch Cooldown Steppers
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldDarkSurfaceLowest)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = strings.settingsCooldownTitle, color = TextCrispPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = strings.settingsCooldownSubtitle, color = TextMutedBody, fontSize = 10.sp)
                    }
                    Icon(imageVector = Icons.Default.Snooze, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pause Duration
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldDarkSurfaceLow)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = strings.settingsPauseDuration, color = TextMutedBody, fontSize = 10.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldDarkSurfaceHighest)
                                        .springClickable { viewModel.decrementPauseDuration() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = null, tint = TextCrispPrimary, modifier = Modifier.size(14.dp))
                                }

                                Text(text = "$pauseDurationMin min", color = TextCrispPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldDarkSurfaceHighest)
                                        .springClickable { viewModel.incrementPauseDuration() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = TextCrispPrimary, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }

                    // Batch Size
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldDarkSurfaceLow)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = strings.settingsBatchSize, color = TextMutedBody, fontSize = 10.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldDarkSurfaceHighest)
                                        .springClickable { viewModel.decrementBatchSize() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = null, tint = TextCrispPrimary, modifier = Modifier.size(14.dp))
                                }

                                Text(text = "$batchSizeMsgs msgs", color = TextCrispPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldDarkSurfaceHighest)
                                        .springClickable { viewModel.incrementBatchSize() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = TextCrispPrimary, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                Text(
                    text = strings.settingsCooldownNotice(batchSizeMsgs),
                    color = TextMutedBody,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // SECTION 3: Default AI Rewrite Tone
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(EmeraldDarkSurfaceLowest)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                    Text(text = strings.settingsAiToneTitle, color = TextCrispPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = strings.settingsSpintaxEngineTag, color = EmeraldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // 3-Option Tone Picker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmeraldDarkSurfaceHigh)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tones = listOf(
                    Triple(0, "😊", strings.settingsToneCasual),
                    Triple(1, "✨", strings.settingsToneFriendlyPicker),
                    Triple(2, "💼", strings.settingsToneFormal)
                )

                tones.forEach { (idx, icon, label) ->
                    val isSelected = selectedAiTone == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) EmeraldPrimary else Color.Transparent)
                            .springClickable { viewModel.selectAiTone(idx) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = icon, fontSize = 12.sp)
                            Text(
                                text = label,
                                color = if (isSelected) Color(0xFF003915) else TextMutedBody,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Text(
                text = strings.settingsAiToneDesc,
                color = TextMutedBody,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Modal QR Code Info
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = {
                Text("Tautkan dengan Kode QR", color = TextCrispPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Mode 8-Digit Pairing Code lebih direkomendasikan karena dapat dilakukan langsung di HP yang sama tanpa perlu kamera ponsel lain.",
                        color = TextMutedBody,
                        fontSize = 12.sp
                    )
                    Text(
                        "Untuk menggunakan Kode Pairing: Klik 'Salin & Buka WhatsApp', lalu buka Pengaturan WA > Perangkat Tertaut > Tautkan dengan nomor telepon.",
                        color = EmeraldTertiary,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showQrDialog = false }) {
                    Text("Mengerti", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = EmeraldDarkSurfaceContainer
        )
    }
}
