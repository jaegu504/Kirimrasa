package com.wabulk.sender.ui

import com.wabulk.sender.theme.AppleTheme
import com.wabulk.sender.theme.appleBackground
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
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import com.wabulk.sender.theme.LocalNeumorphicColors
import com.wabulk.sender.theme.PlusJakartaSansFamily
import com.wabulk.sender.theme.ThemeManager
import com.wabulk.sender.theme.ThemeMode
import com.wabulk.sender.theme.appleGlassBackground
import com.wabulk.sender.ui.tutorial.OnboardingTutorialDialog
import com.wabulk.sender.ui.tutorial.TutorialManager
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import com.wabulk.sender.theme.EmeraldPrimary
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
    var showTutorial by remember { mutableStateOf(!TutorialManager.hasSeenTutorial(context)) }
    val showUpdateDialog by com.wabulk.sender.update.UpdateManager.showDialog.collectAsState()

    LaunchedEffect(Unit) {
        com.wabulk.sender.update.UpdateManager.checkForUpdates(context, isManual = false)
    }

    CompositionLocalProvider(LocalAppStrings provides strings) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .appleBackground(AppleTheme.colors),
            containerColor = Color.Transparent,
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
                    .padding(innerPadding)
                    .statusBarsPadding(),
                label = "tabTransition"
            ) { tab ->
                when (tab) {
                    ScreenTab.COMPOSE -> ComposerScreen(
                        viewModel = composerViewModel,
                        onOpenHelp = { showTutorial = true },
                        onOpenContacts = { currentTab = ScreenTab.CONTACTS },
                        totalContactCount = contacts.size,
                        previewContactName = contacts.firstOrNull { it.isSelected }?.let { c ->
                            c.customNickname.ifBlank { com.wabulk.sender.data.repository.ContactRepository.extractSmartNickname(c.rawName) }
                        },
                        selectedContactCount = selectedCount,
                        onLaunchCampaign = {
                            scope.launch {
                                val targetContacts = contactViewModel.getTargetContactsForCampaign()
                                if (targetContacts.isEmpty()) return@launch

                                val companyVal = composerViewModel.uiState.value.customVariables["Company"].orEmpty()
                                val custom1Val = composerViewModel.uiState.value.customVariables["Custom_1"].orEmpty()
                                val bubbles = composerViewModel.uiState.value.bubbles.filter { it.text.isNotBlank() }
                                val attachments = composerViewModel.attachments.value
                                val mediaUris = attachments.map { it.uriString }
                                val mediaPlacements = attachments.map { it.bubbleIndex }
                                // Nothing to send: never fall back to a template that would reach real contacts
                                if (bubbles.isEmpty() && mediaUris.isEmpty()) return@launch
                                com.wabulk.sender.data.LastMessageStore.save(bubbles.map { it.text })
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
                                        mediaUris = mediaUris,
                                        mediaPlacements = mediaPlacements,
                                        fallbackNode = if (selectedAccount == AccountNode.DUAL_SYNC) {
                                            if (chosenNode == AccountNode.PERSONAL) AccountNode.BUSINESS else AccountNode.PERSONAL
                                        } else null
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
                    ScreenTab.CONTACTS -> ContactManagerScreen(
                        viewModel = contactViewModel,
                        onUseSelection = { currentTab = ScreenTab.COMPOSE }
                    )
                    ScreenTab.QUEUE -> LiveQueueScreen()
                    ScreenTab.SETTINGS -> SettingsScreen(
                        currentLanguage = currentLanguage,
                        onLanguageSelected = { localizationManager.setLanguage(it) }
                    )
                }
            }

            if (showTutorial) {
                OnboardingTutorialDialog(
                    onDismiss = { showTutorial = false }
                )
            }

            if (showUpdateDialog) {
                com.wabulk.sender.ui.update.AppUpdateDialog()
            }
        }
    }
}
