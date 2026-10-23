package com.wabulk.sender.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wabulk.sender.data.locale.AppLanguage
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.data.locale.settingsTexts
import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.engine.OnDeviceWhatsAppEngine
import com.wabulk.sender.engine.model.NodeSession
import com.wabulk.sender.engine.model.NodeStatus
import com.wabulk.sender.engine.queue.AntiBanConfig
import com.wabulk.sender.theme.AppleSwitch
import com.wabulk.sender.theme.AppleTheme
import com.wabulk.sender.theme.GroupCard
import com.wabulk.sender.theme.IconTile
import com.wabulk.sender.theme.LargeTitle
import com.wabulk.sender.theme.ListRow
import com.wabulk.sender.theme.PrimaryCapsuleButton
import com.wabulk.sender.theme.RowSeparator
import com.wabulk.sender.theme.SectionHeader
import com.wabulk.sender.theme.SegmentedControl
import com.wabulk.sender.theme.StatusLabel
import com.wabulk.sender.theme.ThemeManager
import com.wabulk.sender.theme.ThemeMode
import com.wabulk.sender.theme.TileGradients
import com.wabulk.sender.theme.TintPill
import com.wabulk.sender.ui.tutorial.OnboardingTutorialDialog
import com.wabulk.sender.update.UpdateManager
import com.wabulk.sender.update.UpdateState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val PRESETS = listOf(
    AntiBanConfig.PRESET_CONSERVATIVE,
    AntiBanConfig.PRESET_OPTIMAL,
    AntiBanConfig.PRESET_TURBO,
    AntiBanConfig.PRESET_CUSTOM
)
private val THEME_MODES = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)

@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    viewModel: SettingsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val t = settingsTexts()
    val strings = LocalAppStrings.current
    val c = AppleTheme.colors
    val context = LocalContext.current

    val themeMode by ThemeManager.themeMode.collectAsState()
    val node1Session by viewModel.node1Session.collectAsState()
    val node2Session by viewModel.node2Session.collectAsState()
    val antiBan by viewModel.antiBanConfig.collectAsState()
    val selectedAiTone by viewModel.selectedAiTone.collectAsState()
    val baileysConfig by viewModel.baileysConfig.collectAsState()
    val updateState by UpdateManager.updateState.collectAsState()

    var showBreak by remember { mutableStateOf(false) }
    var showAdvanced by remember { mutableStateOf(false) }
    var showTutorial by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LargeTitle(t.title)

        // WhatsApp accounts
        SectionHeader(t.sectionAccounts)
        GroupCard {
            AccountRow(
                node = AccountNode.PERSONAL,
                session = node1Session,
                name = t.personal,
                icon = Icons.AutoMirrored.Filled.Chat,
                gradient = TileGradients.Green,
                packageName = "com.whatsapp",
                phoneHint = strings.waNumberHint,
                serverOn = baileysConfig.isEnabled,
                viewModel = viewModel
            )
            RowSeparator()
            AccountRow(
                node = AccountNode.BUSINESS,
                session = node2Session,
                name = t.business,
                icon = Icons.Default.Work,
                gradient = TileGradients.WhatsAppBusiness,
                packageName = "com.whatsapp.w4b",
                phoneHint = strings.wbNumberHint,
                serverOn = baileysConfig.isEnabled,
                viewModel = viewModel
            )
        }

        // Sending safety
        SectionHeader(t.sectionSafety)
        GroupCard {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconTile(icon = Icons.Default.Security, gradient = TileGradients.Orange)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(t.speedTitle, color = c.label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            t.delayCaption(antiBan.delayRangeSec.first, antiBan.delayRangeSec.last),
                            color = c.secondaryLabel,
                            fontSize = 13.sp
                        )
                    }
                }
                SegmentedControl(
                    options = listOf(t.presetCareful, t.presetNormal, t.presetFast, t.presetOwn),
                    selectedIndex = PRESETS.indexOf(antiBan.preset),
                    onSelect = { viewModel.selectPreset(PRESETS[it]) }
                )
                if (antiBan.preset == AntiBanConfig.PRESET_CUSTOM) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Stepper(
                            label = strings.customDelayMinLabel,
                            value = strings.secondsShort(antiBan.customMinSec),
                            onMinus = { viewModel.adjustCustomMin(-5) },
                            onPlus = { viewModel.adjustCustomMin(5) },
                            modifier = Modifier.weight(1f)
                        )
                        Stepper(
                            label = strings.customDelayMaxLabel,
                            value = strings.secondsShort(antiBan.customMaxSec),
                            onMinus = { viewModel.adjustCustomMax(-5) },
                            onPlus = { viewModel.adjustCustomMax(5) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (antiBan.customMinSec < AntiBanConfig.SAFE_MIN_SEC) {
                        Text(
                            text = strings.customDelayWarning,
                            color = c.orangeOnTint,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(c.orangeTint)
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                }
            }
            RowSeparator(startInset = 16.dp)
            ListRow(
                title = t.showTyping,
                onClick = { viewModel.toggleTypingSim(!antiBan.typingSimEnabled) },
                showChevron = false,
                trailing = { AppleSwitch(checked = antiBan.typingSimEnabled, onCheckedChange = { viewModel.toggleTypingSim(it) }) }
            )
            RowSeparator(startInset = 16.dp)
            ListRow(
                title = t.breakTitle,
                subtitle = t.breakCaption(antiBan.pauseDurationMin, antiBan.batchSize),
                onClick = { showBreak = !showBreak },
                showChevron = false,
                trailing = { ExpandIcon(showBreak) }
            )
            if (showBreak) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Stepper(
                        label = t.breakLength,
                        value = strings.minutesShort(antiBan.pauseDurationMin),
                        onMinus = { viewModel.decrementPauseDuration() },
                        onPlus = { viewModel.incrementPauseDuration() },
                        modifier = Modifier.weight(1f)
                    )
                    Stepper(
                        label = t.breakEvery,
                        value = strings.messagesShort(antiBan.batchSize),
                        onMinus = { viewModel.decrementBatchSize() },
                        onPlus = { viewModel.incrementBatchSize() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // AI rewrite tone
        SectionHeader(t.sectionAi)
        GroupCard(padding = 16.dp, spacing = 12.dp) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconTile(icon = Icons.Default.AutoAwesome, gradient = TileGradients.Purple)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(t.toneTitle, color = c.label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(t.toneCaption, color = c.secondaryLabel, fontSize = 13.sp)
                }
            }
            SegmentedControl(
                options = listOf(strings.settingsToneCasual, strings.settingsToneFriendlyPicker, strings.settingsToneFormal),
                selectedIndex = selectedAiTone,
                onSelect = { viewModel.selectAiTone(it) }
            )
        }

        // General
        SectionHeader(t.sectionGeneral)
        GroupCard {
            ChoiceRow(
                title = t.language,
                icon = Icons.Default.Language,
                gradient = TileGradients.Blue,
                options = AppLanguage.entries.map { it.displayName },
                selectedIndex = AppLanguage.entries.indexOf(currentLanguage),
                onSelect = { onLanguageSelected(AppLanguage.entries[it]) }
            )
            RowSeparator()
            ChoiceRow(
                title = t.appearance,
                icon = Icons.Default.DarkMode,
                gradient = TileGradients.Indigo,
                options = listOf(t.themeAuto, t.themeLight, t.themeDark),
                selectedIndex = THEME_MODES.indexOf(themeMode),
                onSelect = { ThemeManager.setThemeMode(THEME_MODES[it]) }
            )
            RowSeparator()
            val updateError = updateState as? UpdateState.Error
            ListRow(
                title = t.update,
                subtitle = when {
                    updateError != null -> updateError.message
                    updateState is UpdateState.UpToDate -> strings.settingsAppUpToDate
                    else -> t.updateCaption
                },
                subtitleColor = when {
                    updateError != null -> c.redOnTint
                    updateState is UpdateState.UpToDate -> c.greenOnTint
                    else -> null
                },
                icon = Icons.Default.FileDownload,
                iconGradient = TileGradients.Gray,
                onClick = { UpdateManager.checkForUpdates(context, isManual = true) },
                showChevron = false,
                trailing = {
                    if (updateState is UpdateState.Checking) {
                        CircularProgressIndicator(color = c.blue, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    } else {
                        Text("v${UpdateManager.CURRENT_VERSION}", color = c.secondaryLabel, fontSize = 15.sp, maxLines = 1)
                    }
                }
            )
            RowSeparator()
            ListRow(
                title = t.guide,
                subtitle = t.guideCaption,
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                iconGradient = TileGradients.Teal,
                onClick = { showTutorial = true }
            )
        }

        // Advanced: server settings. Normal users never need these, so they start collapsed
        GroupCard {
            ListRow(
                title = t.advanced,
                subtitle = t.advancedCaption,
                icon = Icons.Default.Tune,
                iconGradient = TileGradients.Gray,
                onClick = { showAdvanced = !showAdvanced },
                showChevron = false,
                trailing = { ExpandIcon(showAdvanced) }
            )
            if (showAdvanced) {
                RowSeparator(startInset = 16.dp)
                ServerSettings(viewModel)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showTutorial) {
        OnboardingTutorialDialog(onDismiss = { showTutorial = false })
    }
}

/** One WhatsApp account: status row that expands into its linking flow. */
@Composable
private fun AccountRow(
    node: AccountNode,
    session: NodeSession,
    name: String,
    icon: ImageVector,
    gradient: List<Color>,
    packageName: String,
    phoneHint: String,
    serverOn: Boolean,
    viewModel: SettingsViewModel
) {
    val t = settingsTexts()
    val strings = LocalAppStrings.current
    val c = AppleTheme.colors
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }
    var phone by remember(session.phoneNumber) { mutableStateOf(session.phoneNumber) }
    var requesting by remember { mutableStateOf(false) }
    var pingText by remember { mutableStateOf<String?>(null) }
    var confirmDisconnect by remember { mutableStateOf(false) }
    val installed = remember(packageName) { OnDeviceWhatsAppEngine.isPackageInstalled(context, packageName) }

    val connected = session.status == NodeStatus.CONNECTED
    val pairing = session.status == NodeStatus.PAIRING && session.pairingCode.isNotBlank()

    // Seconds left before the pairing code expires
    var secondsLeft by remember { mutableIntStateOf(0) }
    LaunchedEffect(session.pairingCode, session.codeExpiresAt) {
        while (true) {
            secondsLeft = ((session.codeExpiresAt - System.currentTimeMillis()) / 1000).toInt().coerceAtLeast(0)
            if (secondsLeft == 0) break
            delay(1000)
        }
    }

    val ping: () -> Unit = {
        scope.launch {
            pingText = t.checking
            val ok = viewModel.pingNode(node)
            pingText = if (ok) t.ready else t.notReady
            delay(1500)
            pingText = null
        }
    }

    val (statusText, statusColor) = when (session.status) {
        NodeStatus.CONNECTED -> "● ${t.statusConnected}" to c.greenOnTint
        NodeStatus.PAIRING -> t.statusConnecting to c.orangeOnTint
        NodeStatus.ERROR -> t.statusFailed to c.redOnTint
        NodeStatus.DISCONNECTED -> t.statusNotLinked to c.secondaryLabel
    }

    ListRow(
        title = name,
        subtitle = statusText,
        subtitleColor = statusColor,
        icon = icon,
        iconGradient = gradient,
        onClick = { expanded = !expanded },
        showChevron = false,
        trailing = {
            if (!connected && !expanded) {
                TintPill(text = t.link, background = c.blueTint, content = c.blueOnTint, onClick = { expanded = true })
            } else {
                ExpandIcon(expanded)
            }
        }
    )

    if (expanded) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!installed) {
                Text(t.appNotInstalled, color = c.orangeOnTint, fontSize = 13.sp)
            }
            when {
                connected -> {
                    if (phone.isNotBlank()) {
                        Text(phone, color = c.secondaryLabel, fontSize = 15.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TintPill(text = pingText ?: t.testReady, background = c.blueTint, content = c.blueOnTint, onClick = ping, icon = Icons.Default.Sensors)
                        TintPill(
                            text = t.disconnect,
                            background = c.redTint,
                            content = c.redOnTint,
                            onClick = { confirmDisconnect = true },
                            icon = Icons.Default.LinkOff
                        )
                    }
                }
                !serverOn -> Text(t.serverOff, color = c.secondaryLabel, fontSize = 13.sp)
                else -> {
                    Text(t.phoneLabel, color = c.secondaryLabel, fontSize = 13.sp)
                    Field(
                        value = phone,
                        onValueChange = {
                            phone = it
                            viewModel.updateNodePhone(node, it)
                        },
                        placeholder = phoneHint,
                        keyboardType = KeyboardType.Phone
                    )
                    PrimaryCapsuleButton(
                        text = if (requesting) t.requesting else t.requestCode,
                        enabled = phone.isNotBlank() && !requesting,
                        gradient = TileGradients.Blue,
                        onClick = {
                            requesting = true
                            viewModel.requestGatewayPairingCode(node, phone) { result ->
                                requesting = false
                                result.onFailure { err ->
                                    Toast.makeText(context, strings.failedWith(err.message.orEmpty()), Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                    if (pairing) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(c.cardInset)
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (secondsLeft > 0) {
                                Text(t.yourCode, color = c.secondaryLabel, fontSize = 13.sp)
                                Text(
                                    text = session.pairingCode,
                                    color = c.label,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 3.sp
                                )
                                Text(
                                    t.codeValidFor("%d:%02d".format(secondsLeft / 60, secondsLeft % 60)),
                                    color = c.secondaryLabel,
                                    fontSize = 13.sp
                                )
                                PrimaryCapsuleButton(
                                    text = t.copyAndOpen,
                                    icon = Icons.Default.ContentCopy,
                                    onClick = { viewModel.copyCodeAndOpenWhatsApp(session.pairingCode, node) }
                                )
                                Text(t.linkSteps, color = c.secondaryLabel, fontSize = 13.sp, textAlign = TextAlign.Center)
                            } else {
                                Text(t.codeExpired, color = c.orangeOnTint, fontSize = 13.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                    TintPill(text = pingText ?: t.testReady, background = c.blueTint, content = c.blueOnTint, onClick = ping, icon = Icons.Default.Sensors)
                }
            }
        }
    }

    if (confirmDisconnect) {
        AlertDialog(
            onDismissRequest = { confirmDisconnect = false },
            title = { Text(t.disconnectTitle(name), color = c.label, fontWeight = FontWeight.Bold) },
            text = { Text(t.disconnectBody, color = c.secondaryLabel, fontSize = 15.sp) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDisconnect = false
                    viewModel.disconnectNode(node)
                }) {
                    Text(t.disconnect, color = c.redOnTint, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDisconnect = false }) {
                    Text(t.cancel, color = c.blueOnTint)
                }
            },
            containerColor = c.card
        )
    }
}

/** Server address, access key, on/off and connection test. */
@Composable
private fun ServerSettings(viewModel: SettingsViewModel) {
    val t = settingsTexts()
    val strings = LocalAppStrings.current
    val c = AppleTheme.colors
    val config by viewModel.baileysConfig.collectAsState()
    val testResult by viewModel.gatewayTestResult.collectAsState()
    val isTesting by viewModel.isTestingGateway.collectAsState()
    var showKey by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(t.server, color = c.label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(t.useServer, color = c.label, fontSize = 15.sp)
                Text(t.serverCaption, color = c.secondaryLabel, fontSize = 13.sp)
            }
            AppleSwitch(checked = config.isEnabled, onCheckedChange = { viewModel.toggleBaileysEnabled(it) })
        }

        Text(t.serverAddress, color = c.secondaryLabel, fontSize = 13.sp)
        Field(
            value = config.gatewayUrl,
            onValueChange = { viewModel.updateGatewayUrl(it) },
            placeholder = "https://…",
            keyboardType = KeyboardType.Uri
        )

        Text(t.accessKey, color = c.secondaryLabel, fontSize = 13.sp)
        Field(
            value = config.apiKey,
            onValueChange = { viewModel.updateApiKey(it) },
            placeholder = t.accessKeyHint,
            keyboardType = KeyboardType.Password,
            // Hidden by default so screenshots don't leak the key
            visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
            trailing = {
                IconButton(onClick = { showKey = !showKey }, modifier = Modifier.size(44.dp)) {
                    Icon(
                        imageVector = if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showKey) t.hideKey else t.showKey,
                        tint = c.secondaryLabel
                    )
                }
            }
        )

        TintPill(
            text = if (isTesting) t.testing else t.testConnection,
            background = c.blueTint,
            content = c.blueOnTint,
            onClick = { if (!isTesting) viewModel.testGatewayConnection() },
            icon = Icons.Default.Sensors
        )
        testResult?.let { r ->
            StatusLabel(
                text = if (r.isSuccess) strings.gatewayConnectedLatency(r.latencyMs) else strings.failedWith(r.message),
                color = if (r.isSuccess) c.green else c.red,
                textColor = if (r.isSuccess) c.greenOnTint else c.redOnTint
            )
        }
    }
}

/** Row with the current value on the right; tapping shows a segmented picker underneath. */
@Composable
private fun ChoiceRow(
    title: String,
    icon: ImageVector,
    gradient: List<Color>,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val c = AppleTheme.colors
    var expanded by remember { mutableStateOf(false) }
    ListRow(
        title = title,
        icon = icon,
        iconGradient = gradient,
        onClick = { expanded = !expanded },
        showChevron = false,
        trailing = {
            Text(options.getOrElse(selectedIndex) { "" }, color = c.secondaryLabel, fontSize = 15.sp, maxLines = 1)
            ExpandIcon(expanded)
        }
    )
    if (expanded) {
        SegmentedControl(
            options = options,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )
    }
}

@Composable
private fun ExpandIcon(expanded: Boolean) {
    Icon(
        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
        contentDescription = null,
        tint = AppleTheme.colors.tertiaryLabel,
        modifier = Modifier.size(22.dp)
    )
}

@Composable
private fun Field(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null
) {
    val c = AppleTheme.colors
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = c.label, fontSize = 16.sp),
        cursorBrush = SolidColor(c.blue),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.cardInset)
                    .padding(start = 14.dp, end = if (trailing != null) 2.dp else 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f).padding(vertical = 12.dp)) {
                    if (value.isEmpty()) {
                        Text(placeholder, color = c.secondaryLabel, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    innerTextField()
                }
                trailing?.invoke()
            }
        }
    )
}

@Composable
private fun Stepper(
    label: String,
    value: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val t = settingsTexts()
    val c = AppleTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(c.cardInset)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(label, color = c.secondaryLabel, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMinus, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Default.Remove, contentDescription = "${t.decrease} $label", tint = c.blue)
            }
            Text(value, color = c.label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            IconButton(onClick = onPlus, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Default.Add, contentDescription = "${t.increase} $label", tint = c.blue)
            }
        }
    }
}
