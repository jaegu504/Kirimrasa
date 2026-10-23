package com.wabulk.sender.ui.screens.settings

import com.wabulk.sender.engine.queue.AntiBanConfig
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
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
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Work
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
import com.wabulk.sender.engine.model.NodeSession
import com.wabulk.sender.engine.model.NodeStatus
import com.wabulk.sender.theme.EmeraldOnPrimaryContainer
import com.wabulk.sender.theme.EmeraldPrimary
import com.wabulk.sender.theme.EmeraldPrimaryContainer
import com.wabulk.sender.theme.EmeraldSecondary
import com.wabulk.sender.theme.EmeraldSecondaryContainer
import com.wabulk.sender.theme.EmeraldTertiary
import com.wabulk.sender.theme.StatusError
import com.wabulk.sender.theme.StatusWarning
import com.wabulk.sender.theme.StatusErrorContainer
import com.wabulk.sender.theme.TextSubduedTertiary
import com.wabulk.sender.theme.ThemeManager
import com.wabulk.sender.theme.ThemeMode
import com.wabulk.sender.theme.LocalNeumorphicColors
import com.wabulk.sender.theme.NeumorphicCard
import com.wabulk.sender.theme.neumorphic
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
    val neuColors = LocalNeumorphicColors.current
    val currentThemeMode by ThemeManager.themeMode.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val node1Session by viewModel.node1Session.collectAsState()
    val node2Session by viewModel.node2Session.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val antiBan by viewModel.antiBanConfig.collectAsState()
    val delayRange = antiBan.delayRangeSec
    val typingSimEnabled by viewModel.typingSimEnabled.collectAsState()
    val autoRotateEnabled by viewModel.autoRotateEnabled.collectAsState()
    val pauseDurationMin by viewModel.pauseDurationMin.collectAsState()
    val batchSizeMsgs by viewModel.batchSizeMsgs.collectAsState()
    val selectedAiTone by viewModel.selectedAiTone.collectAsState()
    val baileysConfig by viewModel.baileysConfig.collectAsState()
    val gatewayTestResult by viewModel.gatewayTestResult.collectAsState()
    val isTestingGateway by viewModel.isTestingGateway.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current
    val isWaInstalled = remember { com.wabulk.sender.engine.OnDeviceWhatsAppEngine.isPackageInstalled(context, "com.whatsapp") }
    val isWbInstalled = remember { com.wabulk.sender.engine.OnDeviceWhatsAppEngine.isPackageInstalled(context, "com.whatsapp.w4b") }

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
                        .background(neuColors.surfaceRaised)
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
                        color = neuColors.textSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(neuColors.surfaceContainer)
                        .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = com.wabulk.sender.R.drawable.ic_kirimrasa_logo),
                        contentDescription = strings.appTitle,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.settingsTitle,
                        color = neuColors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.settingsSubtitle,
                        color = neuColors.textSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // THEME SELECTOR CARD (Neumorphic Dark, Light, System)
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                            imageVector = when (currentThemeMode) {
                                ThemeMode.DARK -> Icons.Default.DarkMode
                                ThemeMode.LIGHT -> Icons.Default.LightMode
                                ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                            },
                            contentDescription = null,
                            tint = neuColors.accentPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = strings.settingsThemeTitle,
                            color = neuColors.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(neuColors.accentPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (currentThemeMode) {
                                ThemeMode.DARK -> "DARK"
                                ThemeMode.LIGHT -> "LIGHT"
                                ThemeMode.SYSTEM -> "AUTO"
                            },
                            color = neuColors.accentPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = strings.settingsThemeSubtitle,
                    color = neuColors.textSecondary,
                    fontSize = 11.sp
                )

                // 3-Way Theme Switcher Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val themeOptions = listOf(
                        Triple(ThemeMode.DARK, Icons.Default.DarkMode, strings.settingsThemeDark.replace(" (Neumorphic)", "")),
                        Triple(ThemeMode.LIGHT, Icons.Default.LightMode, strings.settingsThemeLight.replace(" (Neumorphic)", "")),
                        Triple(ThemeMode.SYSTEM, Icons.Default.BrightnessAuto, strings.settingsThemeSystem)
                    )

                    themeOptions.forEach { (mode, icon, label) ->
                        val isSelected = currentThemeMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) neuColors.accentPrimary else neuColors.surfaceContainer)
                                .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(10.dp))
                                .springClickable { ThemeManager.setThemeMode(mode) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else neuColors.textSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else neuColors.textPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // LANGUAGE SELECTOR CARD (Indonesian & Turkish - Vector Icons only, no emoji flags)
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                            tint = neuColors.accentPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = strings.settingsLanguageTitle,
                            color = neuColors.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(neuColors.accentPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = currentLanguage.displayName,
                            color = neuColors.accentPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = strings.settingsLanguageSubtitle,
                    color = neuColors.textSecondary,
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
                            .background(if (isIndonesian) neuColors.accentPrimary else neuColors.surfaceContainer)
                            .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(12.dp))
                            .springClickable { onLanguageSelected(AppLanguage.INDONESIAN) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = if (isIndonesian) Color.White else neuColors.textSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = strings.settingsSelectIndonesian,
                                color = if (isIndonesian) Color.White else neuColors.textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isIndonesian) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = Color.White,
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
                            .background(if (isTurkish) neuColors.accentPrimary else neuColors.surfaceContainer)
                            .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(12.dp))
                            .springClickable { onLanguageSelected(AppLanguage.TURKISH) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = if (isTurkish) Color.White else neuColors.textSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = strings.settingsSelectTurkish,
                                color = if (isTurkish) Color.White else neuColors.textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isTurkish) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
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
                        color = neuColors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(neuColors.surfaceRaised)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = strings.settingsEnginesCount,
                        color = neuColors.textSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // Node 1: WhatsApp Personal
            var localNode1Phone by remember(node1Session.phoneNumber) { mutableStateOf(node1Session.phoneNumber) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(neuColors.surface)
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
                                .background(if (isWaInstalled) EmeraldPrimary.copy(alpha = 0.15f) else StatusErrorContainer.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = if (isWaInstalled) EmeraldPrimary else StatusError,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = strings.settingsNode1Title,
                                    color = neuColors.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(neuColors.surfaceRaised)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = strings.settingsNode1NodeNum,
                                        color = neuColors.textPrimary,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Text(
                                text = if (isWaInstalled) strings.waPersonalDetected else strings.waPersonalMissing,
                                color = if (isWaInstalled) EmeraldPrimary else StatusError,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isWaInstalled) EmeraldPrimary.copy(alpha = 0.15f) else StatusErrorContainer.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isWaInstalled) strings.readyLabel else strings.notAvailableLabel,
                            color = if (isWaInstalled) EmeraldPrimary else StatusError,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Phone Input Field
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = strings.yourWaNumber,
                        color = neuColors.textSecondary,
                        fontSize = 10.sp
                    )
                    BasicTextField(
                        value = localNode1Phone,
                        onValueChange = {
                            localNode1Phone = it
                            viewModel.updateNodePhone(AccountNode.PERSONAL, it)
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = EmeraldPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        cursorBrush = SolidColor(EmeraldPrimary),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(neuColors.surfaceInset)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (localNode1Phone.isEmpty()) {
                                    Text(
                                        text = strings.waNumberHint,
                                        color = TextSubduedTertiary,
                                        fontSize = 11.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                if (baileysConfig.isEnabled) {
                    NodeGatewayPairingCard(
                        node = AccountNode.PERSONAL,
                        session = node1Session,
                        phoneNumber = localNode1Phone,
                        viewModel = viewModel,
                        nodeLabel = "WhatsApp Personal"
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(CircleShape)
                            .background(neuColors.surfaceRaised)
                            .springClickable {
                                try {
                                    val launchIntent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                                    if (launchIntent != null) {
                                        context.startActivity(launchIntent)
                                    } else {
                                        android.widget.Toast.makeText(context, strings.waPersonalNotInstalledToast, android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                } catch (_: Exception) {}
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                            Text(text = strings.openWhatsAppBtn, color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(CircleShape)
                            .background(neuColors.surfaceRaised)
                            .springClickable {
                                coroutineScope.launch {
                                    pingState = "Memeriksa..."
                                    val res = viewModel.pingNode(AccountNode.PERSONAL)
                                    delay(400)
                                    pingState = if (res) strings.readyToUse else strings.failedLabel
                                    delay(1500)
                                    pingState = null
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Sensors, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                            Text(text = pingState ?: strings.testReadinessBtn, color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Node 2: WhatsApp Business
            var localNode2Phone by remember(node2Session.phoneNumber) { mutableStateOf(node2Session.phoneNumber) }
            var pingStateBusiness by remember { mutableStateOf<String?>(null) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(neuColors.surface)
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
                                .background(if (isWbInstalled) EmeraldPrimary.copy(alpha = 0.15f) else StatusErrorContainer.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = null,
                                tint = if (isWbInstalled) EmeraldPrimary else StatusError,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = strings.settingsNode2Title,
                                    color = neuColors.textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(neuColors.surfaceRaised)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = strings.settingsNode2NodeNum,
                                        color = neuColors.textPrimary,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Text(
                                text = if (isWbInstalled) strings.waBusinessDetected else strings.waBusinessMissing,
                                color = if (isWbInstalled) EmeraldPrimary else StatusError,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isWbInstalled) EmeraldPrimary.copy(alpha = 0.15f) else StatusErrorContainer.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isWbInstalled) strings.readyLabel else strings.notAvailableLabel,
                            color = if (isWbInstalled) EmeraldPrimary else StatusError,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Phone Input Field
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = strings.yourWbNumber,
                        color = neuColors.textSecondary,
                        fontSize = 10.sp
                    )
                    BasicTextField(
                        value = localNode2Phone,
                        onValueChange = {
                            localNode2Phone = it
                            viewModel.updateNodePhone(AccountNode.BUSINESS, it)
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = EmeraldPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        cursorBrush = SolidColor(EmeraldPrimary),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(neuColors.surfaceInset)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (localNode2Phone.isEmpty()) {
                                    Text(
                                        text = strings.wbNumberHint,
                                        color = TextSubduedTertiary,
                                        fontSize = 11.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                if (baileysConfig.isEnabled) {
                    NodeGatewayPairingCard(
                        node = AccountNode.BUSINESS,
                        session = node2Session,
                        phoneNumber = localNode2Phone,
                        viewModel = viewModel,
                        nodeLabel = "WhatsApp Business"
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(CircleShape)
                            .background(neuColors.surfaceRaised)
                            .springClickable {
                                try {
                                    val launchIntent = context.packageManager.getLaunchIntentForPackage("com.whatsapp.w4b")
                                    if (launchIntent != null) {
                                        context.startActivity(launchIntent)
                                    } else {
                                        android.widget.Toast.makeText(context, strings.waBusinessNotInstalledToast, android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                } catch (_: Exception) {}
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                            Text(text = strings.openWaBusinessBtn, color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(CircleShape)
                            .background(neuColors.surfaceRaised)
                            .springClickable {
                                coroutineScope.launch {
                                    pingStateBusiness = "Memeriksa..."
                                    val res = viewModel.pingNode(AccountNode.BUSINESS)
                                    delay(400)
                                    pingStateBusiness = if (res) strings.readyToUse else strings.failedLabel
                                    delay(1500)
                                    pingStateBusiness = null
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Sensors, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                            Text(text = pingStateBusiness ?: strings.testReadinessBtn, color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                .background(neuColors.surface)
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
                            .background(if (baileysConfig.isEnabled) EmeraldPrimary.copy(alpha = 0.15f) else neuColors.surfaceRaised),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = if (baileysConfig.isEnabled) EmeraldPrimary else neuColors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = strings.settingsBaileysTitle,
                                color = neuColors.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (baileysConfig.isEnabled) EmeraldSecondaryContainer.copy(alpha = 0.4f) else neuColors.surfaceRaised)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (baileysConfig.isEnabled) strings.activeLabel else strings.inactiveLabel,
                                    color = if (baileysConfig.isEnabled) EmeraldSecondary else neuColors.textSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = strings.settingsBaileysSubtitle,
                            color = neuColors.textSecondary,
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
                        uncheckedThumbColor = neuColors.textSecondary,
                        uncheckedTrackColor = neuColors.surfaceRaised
                    )
                )
            }

            // Description text
            Text(
                text = strings.gatewayDesc,
                color = neuColors.textSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            // URL Gateway Input
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = strings.settingsBaileysUrlLabel,
                    color = neuColors.textPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                BasicTextField(
                    value = baileysConfig.gatewayUrl,
                    onValueChange = { viewModel.updateGatewayUrl(it) },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = neuColors.textPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    cursorBrush = SolidColor(EmeraldPrimary),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(neuColors.surfaceInset)
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
                    text = strings.settingsBaileysKeyLabel,
                    color = neuColors.textPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                BasicTextField(
                    value = baileysConfig.apiKey,
                    onValueChange = { viewModel.updateApiKey(it) },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = neuColors.textPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    cursorBrush = SolidColor(EmeraldPrimary),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(neuColors.surfaceInset)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (baileysConfig.apiKey.isEmpty()) {
                                Text(
                                    text = strings.apiKeyHint,
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
                        .background(neuColors.surfaceRaised)
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
                            text = if (isTestingGateway) "Menguji..." else strings.settingsBaileysTestBtn,
                            color = neuColors.textPrimary,
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
                                text = if (isSuccess) strings.gatewayConnectedLatency(gatewayTestResult!!.latencyMs) else strings.failedWith(gatewayTestResult!!.message),
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
                    Text(text = strings.settingsAntiBanTitle, color = neuColors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                    .background(neuColors.surface)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = strings.settingsJitterTitle, color = neuColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = strings.settingsJitterSubtitle, color = neuColors.textSecondary, fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${delayRange.first}s - ${delayRange.last}s",
                            color = EmeraldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Range Graphic Track
                val trackFraction = (delayRange.last / 120f).coerceIn(0.1f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(neuColors.surfaceRaised)
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
                    Text(text = "Min: ${strings.secondsShort(AntiBanConfig.CUSTOM_FLOOR_SEC)}", color = neuColors.textSecondary, fontSize = 10.sp)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(neuColors.surfaceRaised)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(imageVector = Icons.Default.StackedLineChart, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(11.dp))
                        Text(text = strings.randomDelayLabel, color = EmeraldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(text = "Max: ${strings.secondsShort(AntiBanConfig.CUSTOM_CEIL_SEC)}", color = neuColors.textSecondary, fontSize = 10.sp)
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
                        2 to strings.settingsPresetTurbo,
                        AntiBanConfig.PRESET_CUSTOM to strings.settingsPresetCustom
                    )
                    presets.forEach { (idx, label) ->
                        val isSelected = selectedPreset == idx
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) neuColors.accentPrimary else neuColors.surfaceRaised)
                                .springClickable { viewModel.selectPreset(idx) }
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else neuColors.textSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                if (selectedPreset == AntiBanConfig.PRESET_CUSTOM) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DelayStepper(
                            label = strings.customDelayMinLabel,
                            value = strings.secondsShort(antiBan.customMinSec),
                            onMinus = { viewModel.adjustCustomMin(-5) },
                            onPlus = { viewModel.adjustCustomMin(5) },
                            modifier = Modifier.weight(1f)
                        )
                        DelayStepper(
                            label = strings.customDelayMaxLabel,
                            value = strings.secondsShort(antiBan.customMaxSec),
                            onMinus = { viewModel.adjustCustomMax(-5) },
                            onPlus = { viewModel.adjustCustomMax(5) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (antiBan.customMinSec < AntiBanConfig.SAFE_MIN_SEC) {
                        Text(text = strings.customDelayWarning, color = StatusWarning, fontSize = 10.sp)
                    }
                }
            }

            // Rule 2: Typing Simulation Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(neuColors.surface)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = strings.settingsTypingSimTitle, color = neuColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(neuColors.surfaceRaised)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(text = "3s - 5s", color = neuColors.textSecondary, fontSize = 9.sp)
                        }
                    }
                    Text(text = strings.settingsTypingSimSubtitle, color = neuColors.textSecondary, fontSize = 10.sp)
                }

                Switch(
                    checked = typingSimEnabled,
                    onCheckedChange = { viewModel.toggleTypingSim(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = EmeraldPrimary,
                        uncheckedThumbColor = neuColors.textSecondary,
                        uncheckedTrackColor = neuColors.surfaceRaised
                    )
                )
            }

            // Rule 3: Auto-Rotate Engines Toggle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(neuColors.surface)
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
                            Text(text = strings.settingsAutoRotateTitle, color = neuColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmeraldSecondaryContainer.copy(alpha = 0.4f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(text = strings.roundRobinLabel, color = EmeraldSecondary, fontSize = 9.sp)
                            }
                        }
                        Text(text = strings.settingsAutoRotateSubtitle, color = neuColors.textSecondary, fontSize = 10.sp)
                    }

                    Switch(
                        checked = autoRotateEnabled,
                        onCheckedChange = { viewModel.toggleAutoRotate(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmeraldPrimary,
                            uncheckedThumbColor = neuColors.textSecondary,
                            uncheckedTrackColor = neuColors.surfaceRaised
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
                    .background(neuColors.surface)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = strings.settingsCooldownTitle, color = neuColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = strings.settingsCooldownSubtitle, color = neuColors.textSecondary, fontSize = 10.sp)
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
                            .background(neuColors.surfaceRaised)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = strings.settingsPauseDuration, color = neuColors.textSecondary, fontSize = 10.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(neuColors.surface)
                                        .springClickable { viewModel.decrementPauseDuration() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = null, tint = neuColors.textPrimary, modifier = Modifier.size(14.dp))
                                }

                                Text(text = strings.minutesShort(pauseDurationMin), color = neuColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(neuColors.surface)
                                        .springClickable { viewModel.incrementPauseDuration() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = neuColors.textPrimary, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }

                    // Batch Size
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(neuColors.surfaceRaised)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = strings.settingsBatchSize, color = neuColors.textSecondary, fontSize = 10.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(neuColors.surface)
                                        .springClickable { viewModel.decrementBatchSize() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = null, tint = neuColors.textPrimary, modifier = Modifier.size(14.dp))
                                }

                                Text(text = strings.messagesShort(batchSizeMsgs), color = neuColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(neuColors.surface)
                                        .springClickable { viewModel.incrementBatchSize() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = neuColors.textPrimary, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                Text(
                    text = strings.settingsCooldownNotice(batchSizeMsgs),
                    color = neuColors.textSecondary,
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
                .background(neuColors.surface)
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
                    Text(text = strings.settingsAiToneTitle, color = neuColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                    .background(neuColors.surfaceRaised)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tones = listOf(
                    Triple(0, Icons.Default.Mood, strings.settingsToneCasual),
                    Triple(1, Icons.Default.AutoAwesome, strings.settingsToneFriendlyPicker),
                    Triple(2, Icons.Default.Work, strings.settingsToneFormal)
                )

                tones.forEach { (idx, icon, label) ->
                    val isSelected = selectedAiTone == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) neuColors.accentPrimary else Color.Transparent)
                            .springClickable { viewModel.selectAiTone(idx) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else neuColors.textSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else neuColors.textPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Text(
                text = strings.settingsAiToneDesc,
                color = neuColors.textSecondary,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }

        // SECTION 4: Pembaruan Aplikasi (App Updates)
        val updateState by com.wabulk.sender.update.UpdateManager.updateState.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(neuColors.surface)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = strings.settingsUpdateTitle,
                            color = neuColors.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.settingsUpdateDesc,
                            color = neuColors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "v${com.wabulk.sender.update.UpdateManager.CURRENT_VERSION}",
                        color = EmeraldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        softWrap = false,
                        maxLines = 1
                    )
                }
            }

            if (updateState is com.wabulk.sender.update.UpdateState.UpToDate) {
                Text(
                    text = strings.settingsAppUpToDate,
                    color = EmeraldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            } else if (updateState is com.wabulk.sender.update.UpdateState.Error) {
                Text(
                    text = (updateState as com.wabulk.sender.update.UpdateState.Error).message,
                    color = StatusError,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmeraldPrimary)
                    .springClickable {
                        com.wabulk.sender.update.UpdateManager.checkForUpdates(context, isManual = true)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (updateState is com.wabulk.sender.update.UpdateState.Checking) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = strings.settingsCheckUpdateBtn,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Modal QR Code Info
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = {
                Text(strings.qrLinkTitle, color = neuColors.textPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        strings.qrLinkDesc1,
                        color = neuColors.textSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        strings.qrLinkDesc2,
                        color = EmeraldTertiary,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showQrDialog = false }) {
                    Text(strings.understoodBtn, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = neuColors.surface
        )
    }
}

@Composable
private fun NodeGatewayPairingCard(
    node: AccountNode,
    session: NodeSession,
    phoneNumber: String,
    viewModel: SettingsViewModel,
    nodeLabel: String
) {
    val strings = LocalAppStrings.current
    val neuColors = LocalNeumorphicColors.current
    val context = androidx.compose.ui.platform.LocalContext.current
    var pairingLoading by remember { mutableStateOf(false) }
    var gatewayCode by remember { mutableStateOf<String?>(null) }

    if (session.status == NodeStatus.CONNECTED) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(EmeraldPrimaryContainer)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldOnPrimaryContainer, modifier = Modifier.size(16.dp))
                Text(strings.connectedViaGateway(nodeLabel), color = EmeraldOnPrimaryContainer, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = strings.settingsDisconnectBtn,
                color = StatusError,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.springClickable { viewModel.disconnectNode(node) }
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (phoneNumber.isNotBlank() && !pairingLoading) neuColors.accentPrimary else neuColors.surfaceRaised)
                .springClickable {
                    if (phoneNumber.isNotBlank() && !pairingLoading) {
                        pairingLoading = true
                        viewModel.requestGatewayPairingCode(node, phoneNumber) { result ->
                            pairingLoading = false
                            result.onSuccess { code ->
                                gatewayCode = code
                            }.onFailure { err ->
                                android.widget.Toast.makeText(context, strings.failedWith(err.message.orEmpty()), android.widget.Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (pairingLoading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = if (phoneNumber.isNotBlank()) Color.White else neuColors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = strings.requestPairingCodeBtn,
                        color = if (phoneNumber.isNotBlank()) Color.White else neuColors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (!gatewayCode.isNullOrBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(neuColors.surfaceRaised)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(strings.yourPairingCode, color = neuColors.textSecondary, fontSize = 10.sp)
                Text(
                    text = gatewayCode ?: "",
                    color = EmeraldPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .clip(CircleShape)
                        .background(neuColors.accentPrimary)
                        .springClickable {
                            viewModel.copyCodeAndOpenWhatsApp(gatewayCode ?: "")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(strings.copyCodeOpenWaBtn, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DelayStepper(
    label: String,
    value: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val neuColors = LocalNeumorphicColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(neuColors.surfaceRaised)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = label, color = neuColors.textSecondary, fontSize = 10.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(neuColors.surface).springClickable { onMinus() },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = null, tint = neuColors.textPrimary, modifier = Modifier.size(14.dp))
            }
            Text(text = value, color = neuColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(neuColors.surface).springClickable { onPlus() },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = neuColors.textPrimary, modifier = Modifier.size(14.dp))
            }
        }
    }
}
