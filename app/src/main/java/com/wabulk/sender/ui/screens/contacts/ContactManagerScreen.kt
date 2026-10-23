package com.wabulk.sender.ui.screens.contacts

import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.UploadFile
import com.wabulk.sender.theme.PlusJakartaSansFamily
import com.wabulk.sender.theme.glassDepth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wabulk.sender.data.locale.AppStrings
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.data.local.db.ContactEntity
import com.wabulk.sender.data.local.db.TagEntity
import com.wabulk.sender.data.repository.ContactRepository
import com.wabulk.sender.theme.EmeraldPrimary
import com.wabulk.sender.theme.LocalNeumorphicColors
import com.wabulk.sender.theme.NeumorphicCard
import com.wabulk.sender.theme.neumorphic
import com.wabulk.sender.theme.pulseGlow
import com.wabulk.sender.theme.springClickable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ContactManagerScreen(
    viewModel: ContactViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val neuColors = LocalNeumorphicColors.current
    val contacts by viewModel.contacts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeTag by viewModel.activeTag.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val selectedCount by viewModel.selectedCount.collectAsState()
    val nicknameCount by viewModel.nicknameCount.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val lastSyncResult by viewModel.lastSyncResult.collectAsState()

    var showNewTagDialog by remember { mutableStateOf(false) }
    var showImportCsvDialog by remember { mutableStateOf(false) }
    var showBatchTagDialog by remember { mutableStateOf(false) }
    var contactForTagManagement by remember { mutableStateOf<ContactEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.ensureDefaultTags()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header: Title on left, Action Buttons (Impor CSV, Sinkron) on right
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = strings.contactsTitle,
                        color = neuColors.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(neuColors.accentPrimary)
                            .pulseGlow()
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Impor CSV Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .neumorphic(elevation = 2.dp, cornerRadius = 12.dp)
                            .springClickable { showImportCsvDialog = true }
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null,
                                tint = neuColors.accentPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = strings.contactsImportCsvBtn,
                                color = neuColors.accentPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    // Sync Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .neumorphic(elevation = 2.dp, cornerRadius = 12.dp)
                            .springClickable { viewModel.syncContacts() }
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = neuColors.accentPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = neuColors.accentPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = if (isSyncing) strings.contactsSyncing else strings.contactsSyncBtn,
                                color = neuColors.accentPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            Text(
                text = lastSyncResult ?: strings.contactsSubtitle,
                color = neuColors.textSecondary,
                fontSize = 11.sp
            )
        }

        // Telemetry Shelf (Apple Glass Metrics)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TelemetryPill(
                label = strings.contactsTotalMetric,
                value = totalCount.toString(),
                icon = Icons.Default.Group,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TelemetryPill(
                label = strings.contactsSelectedMetric,
                value = selectedCount.toString(),
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TelemetryPill(
                label = strings.contactsNicknamesMetric,
                value = nicknameCount.toString(),
                icon = Icons.Default.Badge,
                modifier = Modifier.weight(1f)
            )
        }

        // Search Bar (Neumorphic Inset Feel)
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            placeholder = {
                Text(
                    text = strings.contactsSearchPlaceholder,
                    color = neuColors.textTertiary,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = neuColors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = strings.contactsClearSearch,
                        tint = neuColors.textSecondary,
                        modifier = Modifier
                            .size(18.dp)
                            .springClickable { viewModel.onSearchQueryChange("") }
                    )
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = neuColors.surfaceContainer,
                focusedContainerColor = neuColors.surface,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = neuColors.accentPrimary,
                cursorColor = neuColors.accentPrimary,
                focusedTextColor = neuColors.textPrimary,
                unfocusedTextColor = neuColors.textPrimary
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(14.dp))
        )

        // Tag Filter Chips with Dedicated "Terpilih" Segment
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // All Contacts Tag
            TagChip(
                label = strings.contactsTagAll(totalCount),
                isActive = activeTag == null,
                onClick = { viewModel.onTagFilterChange(null) }
            )

            // Dedicated "Terpilih" Segment Filter
            TagChip(
                label = strings.contactsTagSelected(selectedCount),
                isActive = activeTag == ContactViewModel.SELECTED_FILTER_TAG,
                isSpecialSelectedTag = true,
                onClick = {
                    viewModel.onTagFilterChange(
                        if (activeTag == ContactViewModel.SELECTED_FILTER_TAG) null
                        else ContactViewModel.SELECTED_FILTER_TAG
                    )
                }
            )

            // Default categories
            TagChip(
                label = strings.contactsTagFamily(contacts.count { ContactRepository.parseTagsJson(it.tagsJson).contains("Keluarga") }),
                isActive = activeTag == "Keluarga",
                onClick = { viewModel.onTagFilterChange("Keluarga") }
            )
            TagChip(
                label = strings.contactsTagFriends(contacts.count { ContactRepository.parseTagsJson(it.tagsJson).contains("Teman") }),
                isActive = activeTag == "Teman",
                onClick = { viewModel.onTagFilterChange("Teman") }
            )
            TagChip(
                label = strings.contactsTagBusiness(contacts.count { ContactRepository.parseTagsJson(it.tagsJson).contains("Bisnis") }),
                isActive = activeTag == "Bisnis",
                onClick = { viewModel.onTagFilterChange("Bisnis") }
            )

            // Custom tags from DB
            for (tag in tags.filter { it.name !in listOf("Keluarga", "Teman", "Bisnis") }) {
                TagChip(
                    label = "${tag.name} (${contacts.count { c -> ContactRepository.parseTagsJson(c.tagsJson).contains(tag.name) }})",
                    isActive = activeTag == tag.name,
                    onClick = { viewModel.onTagFilterChange(tag.name) }
                )
            }

            // + New Tag button
            TagChip(
                label = "+ ${strings.contactsNewTagBtn}",
                isActive = false,
                onClick = { showNewTagDialog = true }
            )
        }

        // Select All / Deselect All Bar
        val isAllSelectedInView = contacts.isNotEmpty() && contacts.all { it.isSelected }
        val viewCount = contacts.size
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .neumorphic(elevation = 2.dp, cornerRadius = 12.dp)
                .springClickable { viewModel.selectAllContacts(!isAllSelectedInView) }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .then(
                                if (isAllSelectedInView) Modifier.background(neuColors.accentPrimary)
                                else Modifier.border(1.5.dp, neuColors.accentPrimary, RoundedCornerShape(5.dp))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAllSelectedInView) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isAllSelectedInView)
                            strings.contactsAllSelectedInViewCount(viewCount)
                        else if (activeTag == ContactViewModel.SELECTED_FILTER_TAG)
                            strings.contactsTagSelected(viewCount)
                        else if (activeTag != null)
                            strings.contactsSelectAllInTag(activeTag ?: "", viewCount)
                        else
                            strings.contactsSelectAllInViewCount(viewCount),
                        color = neuColors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = if (isAllSelectedInView) strings.contactsDeselectAllInView else strings.contactsSelectAllInView,
                    color = neuColors.accentPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Batch Action Bar (Visible whenever contacts are selected)
        if (selectedCount > 0) {
            BatchActionBar(
                selectedCount = selectedCount,
                strings = strings,
                onBatchTag = { showBatchTagDialog = true },
                onClearSelection = {
                    viewModel.selectAllContacts(false)
                }
            )
        }

        // Empty state
        if (contacts.isEmpty() && !isSyncing) {
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = neuColors.accentPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = if (activeTag == ContactViewModel.SELECTED_FILTER_TAG)
                            strings.contactsTagSelected(0)
                        else
                            strings.contactsSyncBtn,
                        color = neuColors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lastSyncResult ?: strings.contactsSyncedAgo,
                        color = neuColors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Contact List (Supports tap-anywhere on card to multi-select)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(contacts, key = { it.id }) { contact ->
                ContactCard(
                    contact = contact,
                    strings = strings,
                    onToggleSelection = { viewModel.toggleContactSelection(contact.id, !contact.isSelected) },
                    onNicknameChange = { viewModel.updateNickname(contact.id, it) },
                    onQuickFirstName = { viewModel.quickFormatFirstName(contact.id) },
                    onQuickRemoveNotes = { viewModel.quickFormatRemoveNotes(contact.id) },
                    onQuickAddGreeting = { viewModel.quickFormatAddGreeting(contact.id) },
                    onManageTags = { contactForTagManagement = contact }
                )
            }
        }

        // Dialog Tambah Kategori Tag Baru
        if (showNewTagDialog) {
            CreateTagDialog(
                strings = strings,
                onDismiss = { showNewTagDialog = false },
                onConfirm = { tagName ->
                    if (tagName.isNotBlank()) {
                        viewModel.createTag(tagName.trim())
                    }
                    showNewTagDialog = false
                }
            )
        }

        // Dialog Beri Label Bersamaan (Batch Tagging)
        if (showBatchTagDialog) {
            BatchTagDialog(
                selectedCount = selectedCount,
                strings = strings,
                availableTags = tags,
                onDismiss = { showBatchTagDialog = false },
                onApplyTag = { tagName ->
                    if (tagName.isNotBlank()) {
                        viewModel.addTagToSelectedContacts(tagName.trim())
                    }
                    showBatchTagDialog = false
                }
            )
        }

        // Dialog Kelola Tag pada Kontak Individu
        if (contactForTagManagement != null) {
            val targetId = contactForTagManagement!!.id
            val liveContact = contacts.find { it.id == targetId } ?: contactForTagManagement!!
            ManageContactTagsDialog(
                contact = liveContact,
                strings = strings,
                availableTags = tags,
                onDismiss = { contactForTagManagement = null },
                onToggleTag = { tagName, isCurrentlyAssigned ->
                    if (isCurrentlyAssigned) {
                        viewModel.removeTagFromContact(liveContact.id, tagName)
                    } else {
                        viewModel.addTagToContact(liveContact.id, tagName)
                    }
                },
                onCreateAndAssignTag = { newTagName ->
                    if (newTagName.isNotBlank()) {
                        val trimmed = newTagName.trim()
                        viewModel.createTag(trimmed)
                        viewModel.addTagToContact(liveContact.id, trimmed)
                    }
                }
            )
        }

        // Dialog Impor CSV Kontak
        if (showImportCsvDialog) {
            ImportCsvDialog(
                strings = strings,
                onDismiss = { showImportCsvDialog = false },
                onImportFile = { stream -> viewModel.importContactsFromStream(stream) },
                onImportSample = { viewModel.importSampleCsv() }
            )
        }
    }
}

/**
 * Batch Action Bar shown when 1 or more contacts are selected.
 * Provides quick actions to assign a tag in bulk, view selected, or deselect all.
 */
@Composable
private fun BatchActionBar(
    selectedCount: Int,
    strings: AppStrings,
    onBatchTag: () -> Unit,
    onClearSelection: () -> Unit
) {
    val neuColors = LocalNeumorphicColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(neuColors.surfaceRaised.copy(alpha = 0.95f))
            .border(1.dp, neuColors.borderBevel, RoundedCornerShape(20.dp))
            .glassDepth(cornerRadius = 20.dp, elevation = 4.dp, isDark = neuColors.isDark)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Selection count pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(neuColors.accentPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = strings.contactsBatchSelectedCount(selectedCount),
                    color = neuColors.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSansFamily
                )
            }

            // Right: Tag + Cancel Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // "+ Label" Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(neuColors.accentPrimary)
                        .springClickable { onBatchTag() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sell,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = strings.contactsBatchAssignTagBtn,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSansFamily,
                            maxLines = 1
                        )
                    }
                }

                // "Batal" Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(neuColors.surfaceInset)
                        .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(12.dp))
                        .springClickable { onClearSelection() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.contactsDeselectAllInView,
                            tint = neuColors.textSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = strings.contactsDeselectAllInView,
                            color = neuColors.textSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = PlusJakartaSansFamily,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Batch Tag Assignment Dialog
 */
@Composable
private fun BatchTagDialog(
    selectedCount: Int,
    strings: AppStrings,
    availableTags: List<TagEntity>,
    onDismiss: () -> Unit,
    onApplyTag: (String) -> Unit
) {
    val neuColors = LocalNeumorphicColors.current
    var selectedTagName by remember { mutableStateOf("") }
    var customTagInput by remember { mutableStateOf("") }

    val allTagNames = remember(availableTags) {
        (listOf("Keluarga", "Teman", "Bisnis") + availableTags.map { it.name }).distinct()
    }

    Dialog(onDismissRequest = onDismiss) {
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
            elevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.contactsBatchTagDialogTitle(selectedCount),
                            color = neuColors.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.contactsManageTagsDesc,
                            color = neuColors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .neumorphic(elevation = 2.dp, cornerRadius = 14.dp)
                            .springClickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = strings.contactsCloseBtn,
                            tint = neuColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Available Tags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (tagName in allTagNames) {
                        val isChosen = selectedTagName == tagName
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isChosen) neuColors.accentPrimary else neuColors.surfaceContainer)
                                .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(14.dp))
                                .springClickable {
                                    selectedTagName = if (isChosen) "" else tagName
                                    if (selectedTagName.isNotEmpty()) customTagInput = ""
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tagName,
                                color = if (isChosen) Color.White else neuColors.textPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Or create custom tag
                BasicTextField(
                    value = customTagInput,
                    onValueChange = {
                        customTagInput = it
                        if (it.isNotBlank()) selectedTagName = it
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = neuColors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(neuColors.accentPrimary),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(neuColors.surfaceContainer)
                                .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (customTagInput.isEmpty()) {
                                Text(
                                    text = strings.contactsAddNewTagOption,
                                    color = neuColors.textTertiary,
                                    fontSize = 11.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .springClickable { onDismiss() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = strings.contactsCancelBtn,
                            color = neuColors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    val effectiveTag = selectedTagName.ifBlank { customTagInput }.trim()
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (effectiveTag.isNotBlank()) neuColors.accentPrimary else neuColors.surfaceContainer)
                            .then(
                                if (effectiveTag.isNotBlank()) Modifier.springClickable { onApplyTag(effectiveTag) }
                                else Modifier
                            )
                            .padding(horizontal = 16.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = strings.contactsSaveBtn,
                            color = if (effectiveTag.isNotBlank()) Color.White else neuColors.textTertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryPill(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val neuColors = LocalNeumorphicColors.current

    NeumorphicCard(
        modifier = modifier,
        cornerRadius = 16.dp,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = neuColors.accentPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = value,
                    color = neuColors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PlusJakartaSansFamily
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = neuColors.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = PlusJakartaSansFamily,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TagChip(
    label: String,
    isActive: Boolean,
    isSpecialSelectedTag: Boolean = false,
    onClick: () -> Unit
) {
    val neuColors = LocalNeumorphicColors.current

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (isActive) {
                    Modifier.background(neuColors.accentPrimary)
                } else if (isSpecialSelectedTag) {
                    Modifier
                        .background(neuColors.surfaceContainer)
                        .border(1.dp, neuColors.accentPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                } else {
                    Modifier
                        .neumorphic(elevation = 2.dp, cornerRadius = 20.dp)
                }
            )
            .springClickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = if (isActive) Color.White else if (isSpecialSelectedTag) neuColors.accentPrimary else neuColors.textPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * ContactCard represents a contact. Tapping anywhere on the top header/row
 * toggles selection, fulfilling the multi-select UX requested by the user.
 */
@Composable
private fun ContactCard(
    contact: ContactEntity,
    strings: AppStrings,
    onToggleSelection: () -> Unit,
    onNicknameChange: (String) -> Unit,
    onQuickFirstName: () -> Unit,
    onQuickRemoveNotes: () -> Unit,
    onQuickAddGreeting: () -> Unit,
    onManageTags: () -> Unit
) {
    val neuColors = LocalNeumorphicColors.current
    var localNickname by remember(contact.id, contact.customNickname) { mutableStateOf(contact.customNickname) }
    val initials = ContactRepository.extractInitials(contact.rawName)
    val contactTags = ContactRepository.parseTagsJson(contact.tagsJson)

    NeumorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (contact.isSelected) Modifier.border(1.dp, neuColors.accentPrimary.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                else Modifier
            ),
        cornerRadius = 14.dp,
        elevation = if (contact.isSelected) 4.dp else 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Tapping this row toggles selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .springClickable { onToggleSelection() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Checkbox Box
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .then(
                            if (contact.isSelected) Modifier.background(neuColors.accentPrimary)
                            else Modifier.border(1.5.dp, neuColors.borderBevel, RoundedCornerShape(6.dp))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (contact.isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Avatar with Neumorphic Inset/Raised Circle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(neuColors.surfaceContainer)
                        .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = neuColors.accentPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Contact Name & Phone
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = contact.rawName.replace(Regex("\\(.*?\\)"), "").trim(),
                            color = neuColors.textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        // Tag badges (Clicking opens tag manager)
                        for (tag in contactTags.take(3)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(neuColors.surfaceContainer)
                                    .border(0.5.dp, neuColors.borderBevel, RoundedCornerShape(6.dp))
                                    .springClickable { onManageTags() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tag,
                                    color = neuColors.accentPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Text(
                        text = contact.phoneNumber,
                        color = neuColors.textSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Tag icon button
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .neumorphic(elevation = 2.dp, cornerRadius = 15.dp)
                        .springClickable { onManageTags() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sell,
                        contentDescription = strings.contactsManageTagsTitle,
                        tint = neuColors.accentPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Inline Nickname Editor
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = strings.contactsCallNameLabel,
                    color = neuColors.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                BasicTextField(
                    value = localNickname,
                    onValueChange = {
                        localNickname = it
                        onNicknameChange(it)
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = neuColors.accentPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = SolidColor(neuColors.accentPrimary),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(neuColors.surfaceContainer)
                                .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (localNickname.isEmpty()) {
                                Text(
                                    text = strings.contactsAutoKak,
                                    color = neuColors.textTertiary,
                                    fontSize = 11.sp
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Greeting Preview
            val effectivePreviewNick = localNickname.ifBlank { strings.nicknameFallback }
            Text(
                text = strings.contactsGreetingPreview(effectivePreviewNick),
                color = if (localNickname.isNotBlank()) neuColors.accentPrimary else neuColors.textSecondary,
                fontSize = 10.sp
            )

            // Quick Format Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.contactsQuickFormat,
                    color = neuColors.textTertiary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    softWrap = false
                )
                QuickPill(strings.contactsFormatFirstName) { onQuickFirstName() }
                QuickPill(strings.contactsFormatRemoveNotes) { onQuickRemoveNotes() }
                QuickPill(strings.contactsFormatAddHello) { onQuickAddGreeting() }
            }
        }
    }
}

@Composable
private fun QuickPill(label: String, onClick: () -> Unit) {
    val neuColors = LocalNeumorphicColors.current

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .neumorphic(elevation = 1.dp, cornerRadius = 8.dp)
            .springClickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = neuColors.accentPrimary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun CreateTagDialog(
    strings: AppStrings,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val neuColors = LocalNeumorphicColors.current
    var tagName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
            elevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = strings.contactsAddNewTagTitle,
                    color = neuColors.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = strings.contactsAddNewTagDesc,
                    color = neuColors.textSecondary,
                    fontSize = 11.sp
                )

                BasicTextField(
                    value = tagName,
                    onValueChange = { tagName = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = neuColors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(neuColors.accentPrimary),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(neuColors.surfaceContainer)
                                .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (tagName.isEmpty()) {
                                Text(
                                    text = strings.contactsTagNamePlaceholder,
                                    color = neuColors.textTertiary,
                                    fontSize = 12.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .springClickable { onDismiss() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = strings.contactsCancelBtn,
                            color = neuColors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (tagName.isNotBlank()) neuColors.accentPrimary else neuColors.surfaceContainer)
                            .then(if (tagName.isNotBlank()) Modifier.springClickable { onConfirm(tagName) } else Modifier)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = strings.contactsSaveBtn,
                            color = if (tagName.isNotBlank()) Color.White else neuColors.textTertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ManageContactTagsDialog(
    contact: ContactEntity,
    strings: AppStrings,
    availableTags: List<TagEntity>,
    onDismiss: () -> Unit,
    onToggleTag: (String, Boolean) -> Unit,
    onCreateAndAssignTag: (String) -> Unit
) {
    val neuColors = LocalNeumorphicColors.current
    val assignedTags = remember(contact.tagsJson) {
        ContactRepository.parseTagsJson(contact.tagsJson).toSet()
    }
    var newTagInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
            elevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.contactsManageTagsTitle,
                            color = neuColors.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = contact.rawName.replace(Regex("\\(.*?\\)"), "").trim(),
                            color = neuColors.accentPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .neumorphic(elevation = 2.dp, cornerRadius = 14.dp)
                            .springClickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = strings.contactsCloseBtn,
                            tint = neuColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = strings.contactsManageTagsDesc,
                    color = neuColors.textSecondary,
                    fontSize = 11.sp
                )

                // List of tags as clickable toggle chips
                val allTagNames = (listOf("Keluarga", "Teman", "Bisnis") + availableTags.map { it.name }).distinct()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (tagName in allTagNames) {
                        val isAssigned = assignedTags.contains(tagName)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isAssigned) neuColors.accentPrimary else neuColors.surfaceContainer)
                                .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(14.dp))
                                .springClickable { onToggleTag(tagName, isAssigned) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isAssigned) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Text(
                                    text = tagName,
                                    color = if (isAssigned) Color.White else neuColors.textPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Add custom tag inline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BasicTextField(
                        value = newTagInput,
                        onValueChange = { newTagInput = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = neuColors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(neuColors.accentPrimary),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(neuColors.surfaceContainer)
                                    .border(0.8.dp, neuColors.borderBevel, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (newTagInput.isEmpty()) {
                                    Text(
                                        text = strings.contactsAddNewTagOption,
                                        color = neuColors.textTertiary,
                                        fontSize = 11.sp
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (newTagInput.isNotBlank()) neuColors.accentPrimary else neuColors.surfaceContainer)
                            .then(
                                if (newTagInput.isNotBlank()) Modifier.springClickable {
                                    onCreateAndAssignTag(newTagInput)
                                    newTagInput = ""
                                } else Modifier
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = strings.contactsAddBtn,
                            color = if (newTagInput.isNotBlank()) Color.White else neuColors.textTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Selesai Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(neuColors.accentPrimary)
                        .springClickable { onDismiss() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.contactsDoneBtn,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportCsvDialog(
    strings: AppStrings,
    onDismiss: () -> Unit,
    onImportFile: suspend (java.io.InputStream) -> com.wabulk.sender.data.io.CsvParseResult,
    onImportSample: suspend () -> com.wabulk.sender.data.io.CsvParseResult
) {
    val strings = LocalAppStrings.current
    val neuColors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var lastImportStatus by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    val csvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val stream = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)
                    }
                    if (stream != null) {
                        stream.use { s ->
                            when (val result = onImportFile(s)) {
                                is com.wabulk.sender.data.io.CsvParseResult.Success -> {
                                    isSuccess = true
                                    lastImportStatus = strings.contactsImportResult(result.contacts.size, result.rowCount)
                                }
                                is com.wabulk.sender.data.io.CsvParseResult.Error -> {
                                    isSuccess = false
                                    lastImportStatus = strings.failedWith(result.message)
                                }
                            }
                        }
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

    Dialog(onDismissRequest = onDismiss) {
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
            elevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.contactsImportDialogTitle,
                            color = neuColors.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.csvImportTitle,
                            color = neuColors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .neumorphic(elevation = 2.dp, cornerRadius = 14.dp)
                            .springClickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = strings.contactsCloseBtn,
                            tint = neuColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Option 1: File Picker
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(neuColors.accentPrimary)
                        .springClickable { csvPickerLauncher.launch("*/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = strings.contactsImportPickFile,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Option 2: Sample CSV Loader
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .neumorphic(elevation = 2.dp, cornerRadius = 12.dp)
                        .springClickable {
                            coroutineScope.launch {
                                val result = onImportSample()
                                if (result is com.wabulk.sender.data.io.CsvParseResult.Success) {
                                    isSuccess = true
                                    lastImportStatus = strings.contactsImportResult(result.contacts.size, result.rowCount)
                                } else if (result is com.wabulk.sender.data.io.CsvParseResult.Error) {
                                    isSuccess = false
                                    lastImportStatus = strings.failedWith(result.message)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = null,
                            tint = neuColors.accentPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = strings.contactsImportLoadSample,
                            color = neuColors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Status banner if present
                if (lastImportStatus != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSuccess) neuColors.accentPrimary.copy(alpha = 0.15f) else Color(0x33FF5252))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = lastImportStatus!!,
                            color = if (isSuccess) neuColors.accentPrimary else Color(0xFFFF5252),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Close Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .neumorphic(elevation = 1.dp, cornerRadius = 10.dp)
                        .springClickable { onDismiss() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.contactsCloseBtn,
                        color = neuColors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
