package com.wabulk.sender.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import com.wabulk.sender.theme.LocalNeumorphicColors
import com.wabulk.sender.theme.ThemeManager
import com.wabulk.sender.theme.ThemeMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wabulk.sender.data.locale.AppLanguage
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.data.locale.LocalizationManager
import com.wabulk.sender.theme.EmeraldDarkSurface
import com.wabulk.sender.theme.EmeraldDarkSurfaceHigh
import com.wabulk.sender.theme.EmeraldDarkSurfaceLow
import com.wabulk.sender.theme.EmeraldPrimary
import com.wabulk.sender.theme.TextCrispPrimary
import com.wabulk.sender.theme.TextMutedBody
import com.wabulk.sender.theme.pulseGlow
import com.wabulk.sender.theme.springClickable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wabulk.sender.data.repository.ContactRepository
import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.domain.model.MessageBubble
import com.wabulk.sender.domain.spintax.SpintaxParser
import com.wabulk.sender.engine.queue.DispatchQueueManager
import com.wabulk.sender.engine.queue.model.QueueItem
import com.wabulk.sender.engine.queue.model.QueueTaskStatus
import com.wabulk.sender.ui.navigation.EmeraldBottomNav
import com.wabulk.sender.ui.navigation.ScreenTab
import com.wabulk.sender.ui.screens.composer.ComposerScreen
import com.wabulk.sender.ui.screens.composer.ComposerViewModel
import com.wabulk.sender.ui.screens.contacts.ContactManagerScreen
import com.wabulk.sender.ui.screens.contacts.ContactViewModel
import com.wabulk.sender.ui.screens.queue.LiveQueueScreen
import com.wabulk.sender.ui.screens.settings.SettingsScreen

@Composable
fun MainScreen(
    localizationManager: LocalizationManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLanguage by localizationManager.currentLanguage.collectAsState()
    val strings = localizationManager.getStrings(currentLanguage)
    var currentTab by remember { mutableStateOf(ScreenTab.COMPOSE) }
    val contactViewModel: ContactViewModel = viewModel()
    val composerViewModel: ComposerViewModel = viewModel()
    val contacts by contactViewModel.contacts.collectAsState()
    val selectedCount by contactViewModel.selectedCount.collectAsState()
    val queueItems by DispatchQueueManager.getInstance().queueItems.collectAsState()
    val pendingQueueCount = queueItems.count { it.status == QueueTaskStatus.PENDING || it.status == QueueTaskStatus.TYPING }
    val scope = rememberCoroutineScope()

    val neuColors = LocalNeumorphicColors.current
    val currentThemeMode by ThemeManager.themeMode.collectAsState()

    CompositionLocalProvider(LocalAppStrings provides strings) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(neuColors.background),
            containerColor = neuColors.background,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(neuColors.surface.copy(alpha = 0.95f))
                        .border(width = 0.5.dp, brush = neuColors.borderBevel, shape = androidx.compose.ui.graphics.RectangleShape)
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Pulsing status dot + Screen Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(neuColors.accentPrimary)
                                    .pulseGlow()
                            )

                            Text(
                                text = when (currentTab) {
                                    ScreenTab.COMPOSE -> strings.navCompose.uppercase()
                                    ScreenTab.CONTACTS -> strings.navContacts.uppercase()
                                    ScreenTab.QUEUE -> strings.navQueue.uppercase()
                                    ScreenTab.SETTINGS -> strings.navSettings.uppercase()
                                },
                                color = neuColors.textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Right: Quick Theme Toggle + Quick Language Toggle Pill + Shield Icon + Avatar Monogram
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Quick Theme Toggle Button
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(neuColors.surfaceContainer)
                                    .border(0.8.dp, neuColors.borderBevel, CircleShape)
                                    .springClickable {
                                        val nextMode = when (currentThemeMode) {
                                            ThemeMode.DARK -> ThemeMode.LIGHT
                                            ThemeMode.LIGHT -> ThemeMode.SYSTEM
                                            ThemeMode.SYSTEM -> ThemeMode.DARK
                                        }
                                        ThemeManager.setThemeMode(nextMode)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (currentThemeMode) {
                                        ThemeMode.DARK -> Icons.Default.DarkMode
                                        ThemeMode.LIGHT -> Icons.Default.LightMode
                                        ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                    },
                                    contentDescription = "Toggle Theme",
                                    tint = neuColors.accentPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Quick Language Switcher pill in Top Bar (Vector Icon only, no emoji flags)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(neuColors.surfaceContainer)
                                    .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(12.dp))
                                    .springClickable {
                                        val nextLang = if (currentLanguage == AppLanguage.INDONESIAN) {
                                            AppLanguage.TURKISH
                                        } else {
                                            AppLanguage.INDONESIAN
                                        }
                                        localizationManager.setLanguage(nextLang)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = null,
                                        tint = neuColors.accentPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = currentLanguage.code.uppercase(),
                                        color = neuColors.accentPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Shield (Quick link to Security Settings)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(neuColors.surfaceContainer)
                                    .border(0.8.dp, neuColors.borderBevel, CircleShape)
                                    .springClickable { currentTab = ScreenTab.SETTINGS },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Shield",
                                    tint = neuColors.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // User Avatar (Quick link to Settings)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(neuColors.accentPrimary)
                                    .springClickable { currentTab = ScreenTab.SETTINGS },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                EmeraldBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it },
                    queueBadgeCount = pendingQueueCount
                )
            }
        ) { innerPadding ->
            // Screen content with Spring animated transition between tabs
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    val springSpec = spring<Float>(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                    val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1

                    (slideInHorizontally(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) { width -> direction * width / 2 } +
                            fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)))
                        .togetherWith(
                            slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) { width -> -direction * width / 2 } +
                                    fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                        )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                label = "tabTransition"
            ) { tab ->
                when (tab) {
                    ScreenTab.COMPOSE -> ComposerScreen(
                        viewModel = composerViewModel,
                        selectedContactCount = selectedCount,
                        onLaunchCampaign = {
                            scope.launch {
                                val targetContacts = contactViewModel.getTargetContactsForCampaign()
                                if (targetContacts.isEmpty()) return@launch

                                val companyVal = composerViewModel.uiState.value.customVariables["Company"] ?: "KirimRasa"
                                val custom1Val = composerViewModel.uiState.value.customVariables["Custom_1"] ?: "PROMO30"
                                val bubbles = composerViewModel.uiState.value.bubbles.filter { it.text.isNotBlank() }.ifEmpty {
                                    listOf(MessageBubble(text = "Halo {Nickname}, penawaran spesial minggu ini dari {Company}! Gunakan kode {Custom_1}."))
                                }
                                val mediaUris = composerViewModel.attachments.value.map { it.uriString }
                                val selectedAccount = composerViewModel.uiState.value.selectedAccount
                                val queueItems = targetContacts.mapIndexed { idx, c ->
                                    val chosenNode = when (selectedAccount) {
                                        AccountNode.PERSONAL -> AccountNode.PERSONAL
                                        AccountNode.BUSINESS -> AccountNode.BUSINESS
                                        AccountNode.DUAL_SYNC -> if (idx % 2 == 0) AccountNode.PERSONAL else AccountNode.BUSINESS
                                    }
                                    DispatchQueueManager.buildQueueItem(
                                        contact = c,
                                        bubbles = bubbles,
                                        defaultCompany = companyVal,
                                        defaultCustom1 = custom1Val,
                                        nodeUsed = chosenNode,
                                        mediaUris = mediaUris
                                    )
                                }
                                val formattedDate = java.text.SimpleDateFormat("dd MMM HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                                DispatchQueueManager.getInstance().enqueueBroadcast(
                                    items = queueItems,
                                    context = context,
                                    campaignTitle = "Broadcast $formattedDate"
                                )
                                currentTab = ScreenTab.QUEUE
                            }
                        }
                    )
                    ScreenTab.CONTACTS -> ContactManagerScreen(viewModel = contactViewModel)
                    ScreenTab.QUEUE -> LiveQueueScreen()
                    ScreenTab.SETTINGS -> SettingsScreen(
                        currentLanguage = currentLanguage,
                        onLanguageSelected = { localizationManager.setLanguage(it) }
                    )
                }
            }
        }
    }
}
