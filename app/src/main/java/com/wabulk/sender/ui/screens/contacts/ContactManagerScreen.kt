package com.wabulk.sender.ui.screens.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.wabulk.sender.theme.countRollTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.wabulk.sender.data.io.CsvParseResult
import com.wabulk.sender.data.local.db.ContactEntity
import com.wabulk.sender.data.locale.ContactsTexts
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.data.locale.contactsTexts
import com.wabulk.sender.data.repository.ContactRepository
import com.wabulk.sender.theme.AppleTheme
import com.wabulk.sender.theme.GroupCard
import com.wabulk.sender.theme.LargeTitle
import com.wabulk.sender.theme.RowSeparator
import com.wabulk.sender.theme.TileGradients
import com.wabulk.sender.theme.TintPill
import com.wabulk.sender.theme.springClickable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val DEFAULT_TAGS = listOf("Keluarga", "Teman", "Bisnis")
private val NOTES_IN_BRACKETS = Regex("\\(.*?\\)")

@Composable
fun ContactManagerScreen(
    viewModel: ContactViewModel,
    modifier: Modifier = Modifier,
    onUseSelection: () -> Unit = {}
) {
    val t = contactsTexts()
    val strings = LocalAppStrings.current
    val c = AppleTheme.colors
    val context = LocalContext.current
    val contacts by viewModel.contacts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeTag by viewModel.activeTag.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val selectedCount by viewModel.selectedCount.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val lastSyncResult by viewModel.lastSyncResult.collectAsState()

    var showNewTagDialog by remember { mutableStateOf(false) }
    var showImportCsvDialog by remember { mutableStateOf(false) }
    var showBatchTagDialog by remember { mutableStateOf(false) }
    var contactForTagManagement by remember { mutableStateOf<ContactEntity?>(null) }
    var expandedId by remember { mutableStateOf<Long?>(null) }
    var permissionDenied by remember { mutableStateOf(false) }

    val tagNames = remember(tags) { (DEFAULT_TAGS + tags.map { it.name }).distinct() }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionDenied = !granted
        if (granted) viewModel.syncContacts()
    }
    val syncFromPhone = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            permissionDenied = false
            viewModel.syncContacts()
        } else {
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.ensureDefaultTags()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                LargeTitle(text = t.title) {
                    IconButton(onClick = { showImportCsvDialog = true }) {
                        Icon(imageVector = Icons.Default.FileUpload, contentDescription = t.importCsv, tint = c.blue)
                    }
                    if (isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = c.blue, strokeWidth = 2.dp)
                    }
                    TintPill(
                        text = if (isSyncing) t.syncing else t.syncButton,
                        background = c.blueTint,
                        content = c.blueOnTint,
                        icon = Icons.Default.Sync,
                        onClick = { if (!isSyncing) syncFromPhone() }
                    )
                }
                val syncMessage = if (permissionDenied) t.permissionDenied else lastSyncResult
                if (syncMessage != null) {
                    Text(
                        text = syncMessage,
                        color = if (permissionDenied) c.orangeOnTint else c.secondaryLabel,
                        fontSize = 13.sp
                    )
                }
            }

            AppleTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = t.searchPlaceholder,
                leadingIcon = Icons.Default.Search,
                trailing = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }, modifier = Modifier.size(44.dp)) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = t.clearSearch,
                                tint = c.tertiaryLabel,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(label = t.all(totalCount), selected = activeTag == null) {
                    viewModel.onTagFilterChange(null)
                }
                val showingSelected = activeTag == ContactViewModel.SELECTED_FILTER_TAG
                if (selectedCount > 0 || showingSelected) {
                    FilterChip(label = t.selectedFilter(selectedCount), selected = showingSelected, dot = c.blue) {
                        viewModel.onTagFilterChange(if (showingSelected) null else ContactViewModel.SELECTED_FILTER_TAG)
                    }
                }
                for (name in tagNames) {
                    FilterChip(label = name, selected = activeTag == name, dot = tagColor(name)) {
                        viewModel.onTagFilterChange(name)
                    }
                }
                FilterChip(label = "+ ${t.newLabel}", selected = false, textColor = c.blue) {
                    showNewTagDialog = true
                }
            }

            val allSelectedInView = remember(contacts) { contacts.isNotEmpty() && contacts.all { it.isSelected } }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    targetState = selectedCount,
                    transitionSpec = { countRollTransition(initialState, targetState) },
                    label = "selectedCaption"
                ) { n ->
                    Text(
                        text = t.selectedCaption(n).uppercase(),
                        color = c.secondaryLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.4.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selectedCount > 0 || allSelectedInView) {
                        TextAction(text = t.deselectAll, color = c.blue) { viewModel.selectAllContacts(false) }
                    }
                    if (contacts.isNotEmpty() && !allSelectedInView) {
                        TextAction(text = t.selectAll, color = c.blue) { viewModel.selectAllContacts(true) }
                    }
                }
            }

            if (contacts.isEmpty() && !isSyncing) {
                GroupCard(padding = 20.dp) {
                    Text(
                        text = when {
                            activeTag == ContactViewModel.SELECTED_FILTER_TAG -> t.emptySelected
                            searchQuery.isNotBlank() || activeTag != null -> t.emptyNoMatch
                            else -> t.emptyNoContacts
                        },
                        color = c.secondaryLabel,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                // Room for the floating selection bar so the last row stays reachable
                contentPadding = PaddingValues(bottom = if (selectedCount > 0) 96.dp else 16.dp)
            ) {
                itemsIndexed(contacts, key = { _, contact -> contact.id }) { index, contact ->
                    val corner = 20.dp
                    ContactRow(
                        contact = contact,
                        t = t,
                        nicknameFallback = strings.nicknameFallback,
                        shape = RoundedCornerShape(
                            topStart = if (index == 0) corner else 0.dp,
                            topEnd = if (index == 0) corner else 0.dp,
                            bottomEnd = if (index == contacts.lastIndex) corner else 0.dp,
                            bottomStart = if (index == contacts.lastIndex) corner else 0.dp
                        ),
                        showSeparator = index > 0,
                        expanded = expandedId == contact.id,
                        onToggleSelection = { viewModel.toggleContactSelection(contact.id, !contact.isSelected) },
                        onToggleExpanded = { expandedId = if (expandedId == contact.id) null else contact.id },
                        onNicknameChange = { viewModel.updateNickname(contact.id, it) },
                        onQuickFirstName = { viewModel.quickFormatFirstName(contact.id) },
                        onQuickRemoveNotes = { viewModel.quickFormatRemoveNotes(contact.id) },
                        onQuickAddGreeting = { viewModel.quickFormatAddGreeting(contact.id) },
                        onManageTags = { contactForTagManagement = contact }
                    )
                }
            }
        }

        // Keep the last count while the bar slides away, so it never flashes "0"
        var barCount by remember { mutableIntStateOf(selectedCount) }
        if (selectedCount > 0) barCount = selectedCount
        AnimatedVisibility(
            visible = selectedCount > 0,
            enter = slideInVertically(spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)) { it } + fadeIn(),
            exit = slideOutVertically(tween(200)) { it } + fadeOut(tween(200)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            SelectionBar(
                count = barCount,
                countText = t.barCount,
                labelText = t.giveLabel,
                useText = t.use,
                onLabel = { showBatchTagDialog = true },
                onUse = onUseSelection
            )
        }
    }

    if (showNewTagDialog) {
        CreateTagDialog(
            t = t,
            onDismiss = { showNewTagDialog = false },
            onConfirm = { tagName ->
                viewModel.createTag(tagName.trim())
                showNewTagDialog = false
            }
        )
    }

    if (showBatchTagDialog) {
        BatchTagDialog(
            t = t,
            selectedCount = selectedCount,
            tagNames = tagNames,
            onDismiss = { showBatchTagDialog = false },
            onApplyTag = { tagName ->
                viewModel.addTagToSelectedContacts(tagName)
                showBatchTagDialog = false
            }
        )
    }

    contactForTagManagement?.let { target ->
        val liveContact = contacts.find { it.id == target.id } ?: target
        ManageContactTagsDialog(
            t = t,
            contact = liveContact,
            tagNames = tagNames,
            onDismiss = { contactForTagManagement = null },
            onToggleTag = { tagName, isAssigned ->
                if (isAssigned) viewModel.removeTagFromContact(liveContact.id, tagName)
                else viewModel.addTagToContact(liveContact.id, tagName)
            },
            onCreateAndAssignTag = { newTagName ->
                val trimmed = newTagName.trim()
                viewModel.createTag(trimmed)
                viewModel.addTagToContact(liveContact.id, trimmed)
            }
        )
    }

    if (showImportCsvDialog) {
        ImportCsvDialog(
            t = t,
            onDismiss = { showImportCsvDialog = false },
            onImportFile = { stream -> viewModel.importContactsFromStream(stream) },
            onImportSample = { viewModel.importSampleCsv() }
        )
    }
}

/** Stable colour per tag name so a tag looks the same everywhere. */
@Composable
private fun tagColor(name: String): Color {
    val c = AppleTheme.colors
    val palette = listOf(c.blue, c.orange, c.purple, TileGradients.Pink.last(), c.teal, TileGradients.Indigo.last())
    return palette[Math.floorMod(name.hashCode(), palette.size)]
}

private val AVATAR_GRADIENTS = listOf(
    TileGradients.Orange, TileGradients.Blue, TileGradients.Purple, TileGradients.Green,
    TileGradients.Pink, TileGradients.Teal, TileGradients.Indigo
)

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    dot: Color? = null,
    textColor: Color? = null,
    idleBackground: Color = AppleTheme.colors.card,
    onClick: () -> Unit
) {
    val c = AppleTheme.colors
    // 44 dp touch target around a 36 dp pill
    Box(
        modifier = Modifier
            .height(44.dp)
            .semantics { role = Role.Button; this.selected = selected }
            .springClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .height(36.dp)
                .clip(CircleShape)
                .background(if (selected) c.label else idleBackground)
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (dot != null) Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dot))
            Text(
                text = label,
                color = if (selected) c.card else textColor ?: c.label,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ContactRow(
    contact: ContactEntity,
    t: ContactsTexts,
    nicknameFallback: String,
    shape: Shape,
    showSeparator: Boolean,
    expanded: Boolean,
    onToggleSelection: () -> Unit,
    onToggleExpanded: () -> Unit,
    onNicknameChange: (String) -> Unit,
    onQuickFirstName: () -> Unit,
    onQuickRemoveNotes: () -> Unit,
    onQuickAddGreeting: () -> Unit,
    onManageTags: () -> Unit
) {
    val c = AppleTheme.colors
    val contactTags = remember(contact.tagsJson) { ContactRepository.parseTagsJson(contact.tagsJson) }
    val displayName = remember(contact.rawName) {
        contact.rawName.replace(NOTES_IN_BRACKETS, "").trim().ifEmpty { contact.phoneNumber }
    }
    val subtitle = buildAnnotatedString {
        if (contact.customNickname.isBlank()) {
            withStyle(SpanStyle(color = c.orangeOnTint)) { append(t.noNickname) }
        } else {
            append(t.calledAs(contact.customNickname))
        }
        contactTags.forEach { append(" · $it") }
    }

    val haptic = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(c.card)
    ) {
        if (showSeparator) RowSeparator(startInset = 72.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 68.dp)
                .toggleable(value = contact.isSelected, role = Role.Checkbox, onValueChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onToggleSelection()
                })
                .padding(start = 16.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(AVATAR_GRADIENTS[Math.floorMod(contact.phoneNumber.hashCode(), AVATAR_GRADIENTS.size)])),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ContactRepository.extractInitials(contact.rawName),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = displayName,
                    color = c.label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = c.secondaryLabel,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onToggleExpanded) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = t.editContact,
                    tint = if (expanded) c.blue else c.tertiaryLabel,
                    modifier = Modifier.size(20.dp)
                )
            }
            RoundCheck(checked = contact.isSelected)
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 72.dp, end = 16.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = contact.phoneNumber, color = c.secondaryLabel, fontSize = 13.sp)
                Text(text = t.nicknameLabel, color = c.secondaryLabel, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                NicknameEditor(
                    contact = contact,
                    placeholder = t.nicknameHint(nicknameFallback),
                    onNicknameChange = onNicknameChange
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TintPill(text = t.firstNameOnly, background = c.blueTint, content = c.blueOnTint, onClick = onQuickFirstName)
                    TintPill(text = t.removeNotes, background = c.blueTint, content = c.blueOnTint, onClick = onQuickRemoveNotes)
                    TintPill(text = t.addGreeting, background = c.blueTint, content = c.blueOnTint, onClick = onQuickAddGreeting)
                    TintPill(text = t.labels, background = c.purpleTint, content = c.purpleOnTint, icon = Icons.Default.Sell, onClick = onManageTags)
                }
            }
        }
    }
}

/**
 * Saves the nickname 400 ms after the last keystroke instead of on every key,
 * and flushes a pending edit if the row scrolls away or collapses.
 */
@Composable
private fun NicknameEditor(
    contact: ContactEntity,
    placeholder: String,
    onNicknameChange: (String) -> Unit
) {
    var text by remember(contact.id) { mutableStateOf(contact.customNickname) }
    var lastSent by remember(contact.id) { mutableStateOf<String?>(null) }
    val latestText by rememberUpdatedState(text)
    val saved by rememberUpdatedState(contact.customNickname)
    val save by rememberUpdatedState(onNicknameChange)

    // Pick up changes made elsewhere (quick formats), but ignore the echo of our own save.
    LaunchedEffect(contact.customNickname) {
        if (contact.customNickname != lastSent) text = contact.customNickname
    }
    LaunchedEffect(text) {
        delay(400)
        if (text != saved) {
            lastSent = text
            save(text)
        }
    }
    DisposableEffect(contact.id) {
        onDispose { if (latestText != saved) save(latestText) }
    }

    AppleTextField(value = text, onValueChange = { text = it }, placeholder = placeholder)
}

@Composable
private fun RoundCheck(checked: Boolean) {
    val c = AppleTheme.colors
    // Fill fades in while the tick pops with a small overshoot
    val fill by animateColorAsState(if (checked) c.blue else Color.Transparent, tween(180), label = "checkFill")
    val border by animateColorAsState(if (checked) c.blue else c.tertiaryLabel, tween(180), label = "checkBorder")
    val tickScale by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = if (checked) spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium) else tween(120),
        label = "checkTick"
    )
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(fill)
            .border(2.dp, border, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp).graphicsLayer { scaleX = tickScale; scaleY = tickScale; alpha = tickScale.coerceIn(0f, 1f) }
        )
    }
}

@Composable
private fun SelectionBar(
    count: Int,
    countText: (Int) -> String,
    labelText: String,
    useText: String,
    onLabel: () -> Unit,
    onUse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = AppleTheme.colors
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .shadow(12.dp, shape)
            .clip(shape)
            .background(if (c.isDark) c.cardInset else c.label.copy(alpha = 0.94f))
            .padding(start = 18.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(
            targetState = count,
            transitionSpec = { countRollTransition(initialState, targetState) },
            modifier = Modifier.weight(1f),
            label = "barCount"
        ) { n ->
            Text(
                text = countText(n),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        FilledButton(text = labelText, onClick = onLabel, background = Color.White.copy(alpha = 0.14f))
        FilledButton(text = useText, onClick = onUse)
    }
}

/** Blue (or custom-coloured) rounded button, ≥ 44 dp tall. */
@Composable
private fun FilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    background: Color = AppleTheme.colors.blue,
    content: Color = Color.White,
    icon: ImageVector? = null
) {
    val c = AppleTheme.colors
    Row(
        modifier = modifier
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) background else c.fill)
            .semantics { role = Role.Button }
            .springClickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tint = if (enabled) content else c.tertiaryLabel
        if (icon != null) Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        Text(text = text, color = tint, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun TextAction(text: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(10.dp))
            .semantics { role = Role.Button }
            .springClickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = color, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** iOS grey-filled text field. */
@Composable
private fun AppleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailing: @Composable () -> Unit = {}
) {
    val c = AppleTheme.colors
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(color = c.label, fontSize = 16.sp),
        cursorBrush = SolidColor(c.blue),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.fill)
                    .padding(start = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    Icon(imageVector = leadingIcon, contentDescription = null, tint = c.secondaryLabel, modifier = Modifier.size(18.dp))
                }
                Box(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    if (value.isEmpty()) {
                        Text(text = placeholder, color = c.secondaryLabel, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    innerTextField()
                }
                trailing()
            }
        }
    )
}

/** White rounded sheet with a title, optional subtitle and a close button. */
@Composable
private fun SheetDialog(
    title: String,
    subtitle: String?,
    closeText: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val c = AppleTheme.colors
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(c.card)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f).padding(top = 6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = title, color = c.label, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    if (subtitle != null) Text(text = subtitle, color = c.secondaryLabel, fontSize = 14.sp)
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = closeText, tint = c.secondaryLabel)
                }
            }
            content()
        }
    }
}

@Composable
private fun DialogButtons(
    t: ContactsTexts,
    confirmText: String,
    confirmEnabled: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextAction(text = t.cancel, color = AppleTheme.colors.blue, onClick = onCancel)
        FilledButton(text = confirmText, enabled = confirmEnabled, onClick = onConfirm)
    }
}

@Composable
private fun TagChoiceRow(tagNames: List<String>, isChosen: (String) -> Boolean, onTap: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (name in tagNames) {
            FilterChip(
                label = name,
                selected = isChosen(name),
                dot = tagColor(name),
                idleBackground = AppleTheme.colors.fill
            ) { onTap(name) }
        }
    }
}

@Composable
private fun CreateTagDialog(
    t: ContactsTexts,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tagName by remember { mutableStateOf("") }
    SheetDialog(title = t.newLabel, subtitle = t.newLabelDesc, closeText = t.close, onDismiss = onDismiss) {
        AppleTextField(value = tagName, onValueChange = { tagName = it }, placeholder = t.labelNamePlaceholder)
        DialogButtons(
            t = t,
            confirmText = t.save,
            confirmEnabled = tagName.isNotBlank(),
            onConfirm = { onConfirm(tagName) },
            onCancel = onDismiss
        )
    }
}

@Composable
private fun BatchTagDialog(
    t: ContactsTexts,
    selectedCount: Int,
    tagNames: List<String>,
    onDismiss: () -> Unit,
    onApplyTag: (String) -> Unit
) {
    var chosen by remember { mutableStateOf("") }
    var typed by remember { mutableStateOf("") }
    val effectiveTag = typed.trim().ifEmpty { chosen }

    SheetDialog(title = t.batchTitle(selectedCount), subtitle = t.batchDesc, closeText = t.close, onDismiss = onDismiss) {
        TagChoiceRow(tagNames = tagNames, isChosen = { it == chosen && typed.isBlank() }) { name ->
            chosen = if (chosen == name) "" else name
            typed = ""
        }
        AppleTextField(value = typed, onValueChange = { typed = it }, placeholder = t.typeNewLabel)
        DialogButtons(
            t = t,
            confirmText = t.save,
            confirmEnabled = effectiveTag.isNotBlank(),
            onConfirm = { onApplyTag(effectiveTag) },
            onCancel = onDismiss
        )
    }
}

@Composable
private fun ManageContactTagsDialog(
    t: ContactsTexts,
    contact: ContactEntity,
    tagNames: List<String>,
    onDismiss: () -> Unit,
    onToggleTag: (String, Boolean) -> Unit,
    onCreateAndAssignTag: (String) -> Unit
) {
    val assignedTags = remember(contact.tagsJson) { ContactRepository.parseTagsJson(contact.tagsJson).toSet() }
    var newTagInput by remember { mutableStateOf("") }

    SheetDialog(
        title = contact.rawName.replace(NOTES_IN_BRACKETS, "").trim().ifEmpty { contact.phoneNumber },
        subtitle = t.manageDesc,
        closeText = t.close,
        onDismiss = onDismiss
    ) {
        TagChoiceRow(tagNames = tagNames, isChosen = { it in assignedTags }) { name ->
            onToggleTag(name, name in assignedTags)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppleTextField(
                value = newTagInput,
                onValueChange = { newTagInput = it },
                placeholder = t.typeNewLabel,
                modifier = Modifier.weight(1f)
            )
            FilledButton(
                text = t.add,
                enabled = newTagInput.isNotBlank(),
                onClick = {
                    onCreateAndAssignTag(newTagInput)
                    newTagInput = ""
                }
            )
        }
        FilledButton(text = t.done, onClick = onDismiss, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ImportCsvDialog(
    t: ContactsTexts,
    onDismiss: () -> Unit,
    onImportFile: suspend (java.io.InputStream) -> CsvParseResult,
    onImportSample: suspend () -> CsvParseResult
) {
    val strings = LocalAppStrings.current
    val c = AppleTheme.colors
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var lastImportStatus by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    fun showResult(result: CsvParseResult) {
        when (result) {
            is CsvParseResult.Success -> {
                isSuccess = true
                lastImportStatus = strings.contactsImportResult(result.contacts.size, result.rowCount)
            }
            is CsvParseResult.Error -> {
                isSuccess = false
                lastImportStatus = strings.failedWith(result.message)
            }
        }
    }

    val csvPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val stream = withContext(Dispatchers.IO) { context.contentResolver.openInputStream(uri) }
                    if (stream != null) {
                        stream.use { showResult(onImportFile(it)) }
                    } else {
                        isSuccess = false
                        lastImportStatus = strings.csvOpenFailed
                    }
                } catch (e: Exception) {
                    isSuccess = false
                    lastImportStatus = strings.failedWith(e.message.orEmpty())
                }
            }
        }
    }

    SheetDialog(title = t.importCsv, subtitle = t.importDesc, closeText = t.close, onDismiss = onDismiss) {
        FilledButton(
            text = t.importPickFile,
            icon = Icons.Default.UploadFile,
            onClick = { csvPickerLauncher.launch("*/*") },
            modifier = Modifier.fillMaxWidth()
        )
        FilledButton(
            text = t.importSample,
            icon = Icons.Default.AutoFixHigh,
            background = c.blueTint,
            content = c.blueOnTint,
            onClick = { coroutineScope.launch { showResult(onImportSample()) } },
            modifier = Modifier.fillMaxWidth()
        )
        lastImportStatus?.let { status ->
            Text(
                text = status,
                color = if (isSuccess) c.greenOnTint else c.redOnTint,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSuccess) c.greenTint else c.redTint)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
    }
}
