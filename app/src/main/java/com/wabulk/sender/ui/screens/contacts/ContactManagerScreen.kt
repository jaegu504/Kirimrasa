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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.UploadFile
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wabulk.sender.data.locale.AppStrings
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.data.local.db.ContactEntity
import com.wabulk.sender.data.local.db.TagEntity
import com.wabulk.sender.data.repository.ContactRepository
import com.wabulk.sender.theme.EmeraldDarkSurfaceContainer
import com.wabulk.sender.theme.EmeraldDarkSurfaceHigh
import com.wabulk.sender.theme.EmeraldDarkSurfaceHighest
import com.wabulk.sender.theme.EmeraldDarkSurfaceLow
import com.wabulk.sender.theme.EmeraldDarkSurfaceLowest
import com.wabulk.sender.theme.EmeraldOnPrimaryContainer
import com.wabulk.sender.theme.EmeraldPrimary
import com.wabulk.sender.theme.EmeraldPrimaryContainer
import com.wabulk.sender.theme.EmeraldSecondary
import com.wabulk.sender.theme.TextCrispPrimary
import com.wabulk.sender.theme.TextMutedBody
import com.wabulk.sender.theme.TextSubduedTertiary
import com.wabulk.sender.theme.pulseGlow
import com.wabulk.sender.theme.springClickable

@Composable
fun ContactManagerScreen(
    viewModel: ContactViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
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
    var contactForTagManagement by remember { mutableStateOf<ContactEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.ensureDefaultTags()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subtitle & Sync Pill Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = strings.contactsTitle,
                        color = TextCrispPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                            .pulseGlow()
                    )
                }
                Text(
                    text = strings.contactsSubtitle,
                    color = TextMutedBody,
                    fontSize = 11.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Impor CSV Button
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldDarkSurfaceHigh)
                        .springClickable { showImportCsvDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = strings.contactsImportCsvBtn,
                            color = EmeraldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Sync Button
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldDarkSurfaceHigh)
                        .springClickable { viewModel.syncContacts() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = EmeraldPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = if (isSyncing) "Syncing..." else strings.contactsSyncBtn,
                            color = EmeraldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Telemetry Shelf
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TelemetryPill(label = strings.contactsTotalMetric, value = totalCount.toString())
            TelemetryPill(label = strings.contactsSelectedMetric, value = selectedCount.toString())
            TelemetryPill(label = strings.contactsNicknamesMetric, value = "$nicknameCount/$selectedCount")
        }

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            placeholder = {
                Text(
                    text = strings.contactsSearchPlaceholder,
                    color = TextSubduedTertiary,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextMutedBody,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Clear",
                        tint = TextMutedBody,
                        modifier = Modifier
                            .size(18.dp)
                            .springClickable { viewModel.onSearchQueryChange("") }
                    )
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = EmeraldDarkSurfaceContainer,
                focusedContainerColor = EmeraldDarkSurfaceHigh,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = EmeraldPrimary,
                cursorColor = EmeraldPrimary,
                focusedTextColor = TextCrispPrimary,
                unfocusedTextColor = TextCrispPrimary
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Tag Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TagChip(
                label = strings.contactsTagAll(totalCount),
                isActive = activeTag == null,
                onClick = { viewModel.onTagFilterChange(null) }
            )
            // Default tags
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

        // Select All in Tag
        if (activeTag != null) {
            val tagContactCount = contacts.size
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmeraldDarkSurfaceContainer)
                    .springClickable { viewModel.selectAllInTag(true) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.5.dp, EmeraldPrimary, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = strings.contactsSelectAllInTag(activeTag ?: "", tagContactCount),
                    color = TextCrispPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Empty state
        if (contacts.isEmpty() && !isSyncing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldDarkSurfaceContainer)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = strings.contactsSyncBtn,
                        color = TextCrispPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lastSyncResult ?: strings.contactsSyncedAgo,
                        color = TextMutedBody,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Contact List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                onDismiss = { showNewTagDialog = false },
                onConfirm = { tagName ->
                    if (tagName.isNotBlank()) {
                        viewModel.createTag(tagName.trim())
                    }
                    showNewTagDialog = false
                }
            )
        }

        // Dialog Kelola Tag pada Kontak
        if (contactForTagManagement != null) {
            val targetId = contactForTagManagement!!.id
            val liveContact = contacts.find { it.id == targetId } ?: contactForTagManagement!!
            ManageContactTagsDialog(
                contact = liveContact,
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

@Composable
private fun TelemetryPill(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(EmeraldDarkSurfaceContainer)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = EmeraldPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = label,
            color = TextMutedBody,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun TagChip(label: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isActive) EmeraldPrimary else EmeraldDarkSurfaceHigh)
            .springClickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (isActive) Color(0xFF003915) else TextCrispPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

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
    var localNickname by remember(contact.id, contact.customNickname) { mutableStateOf(contact.customNickname) }
    val initials = ContactRepository.extractInitials(contact.rawName)
    val contactTags = ContactRepository.parseTagsJson(contact.tagsJson)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EmeraldDarkSurfaceContainer)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Selection checkbox
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .then(
                        if (contact.isSelected)
                            Modifier.background(EmeraldPrimary)
                        else
                            Modifier.border(1.5.dp, TextSubduedTertiary, RoundedCornerShape(6.dp))
                    )
                    .springClickable { onToggleSelection() },
                contentAlignment = Alignment.Center
            ) {
                if (contact.isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF003915),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmeraldDarkSurfaceHighest),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = EmeraldPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Name and Phone
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = contact.rawName.replace(Regex("\\(.*?\\)"), "").trim(),
                        color = TextCrispPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    // Tag badges (clickable to manage)
                    for (tag in contactTags.take(3)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldDarkSurfaceHigh)
                                .springClickable { onManageTags() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                color = EmeraldSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Text(
                    text = contact.phoneNumber,
                    color = TextMutedBody,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Tag icon button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(EmeraldDarkSurfaceHigh.copy(alpha = 0.6f))
                    .springClickable { onManageTags() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Sell,
                    contentDescription = "Kelola Tag",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(16.dp)
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
                color = TextMutedBody,
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
                    color = EmeraldPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                ),
                cursorBrush = SolidColor(EmeraldPrimary),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldDarkSurfaceLow)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        innerTextField()
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Greeting Preview
        if (localNickname.isNotBlank()) {
            Text(
                text = strings.contactsGreetingPreview(localNickname),
                color = TextMutedBody,
                fontSize = 11.sp
            )
        }

        // Quick Format Pills
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = strings.contactsQuickFormat,
                color = TextSubduedTertiary,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            QuickPill(strings.contactsFormatFirstName) { onQuickFirstName() }
            QuickPill(strings.contactsFormatRemoveNotes) { onQuickRemoveNotes() }
            QuickPill(strings.contactsFormatAddHello) { onQuickAddGreeting() }
        }
    }
}

@Composable
private fun QuickPill(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(EmeraldDarkSurfaceHigh)
            .springClickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = EmeraldPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CreateTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tagName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(EmeraldDarkSurfaceContainer)
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Tambah Kategori Tag Baru",
                    color = TextCrispPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Buat tag kategori baru untuk mengelompokkan kontak (contoh: VIP, Pelanggan, Reseller, Klien).",
                    color = TextMutedBody,
                    fontSize = 12.sp
                )

                BasicTextField(
                    value = tagName,
                    onValueChange = { tagName = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = TextCrispPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(EmeraldPrimary),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldDarkSurfaceHigh)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (tagName.isEmpty()) {
                                Text(
                                    text = "Nama tag (contoh: VIP)",
                                    color = TextSubduedTertiary,
                                    fontSize = 13.sp
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
                            text = "Batal",
                            color = TextMutedBody,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (tagName.isNotBlank()) EmeraldPrimary else EmeraldDarkSurfaceHigh)
                            .then(if (tagName.isNotBlank()) Modifier.springClickable { onConfirm(tagName) } else Modifier)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Simpan",
                            color = if (tagName.isNotBlank()) Color(0xFF003915) else TextSubduedTertiary,
                            fontSize = 13.sp,
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
    availableTags: List<TagEntity>,
    onDismiss: () -> Unit,
    onToggleTag: (String, Boolean) -> Unit,
    onCreateAndAssignTag: (String) -> Unit
) {
    val assignedTags = remember(contact.tagsJson) {
        ContactRepository.parseTagsJson(contact.tagsJson).toSet()
    }
    var newTagInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(EmeraldDarkSurfaceContainer)
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Kelola Tag Kontak",
                            color = TextCrispPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = contact.rawName.replace(Regex("\\(.*?\\)"), "").trim(),
                            color = EmeraldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(EmeraldDarkSurfaceHigh)
                            .springClickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Tutup",
                            tint = TextMutedBody,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = "Pilih kategori tag yang ingin disematkan pada kontak ini:",
                    color = TextMutedBody,
                    fontSize = 12.sp
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
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isAssigned) EmeraldPrimary else EmeraldDarkSurfaceHigh)
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
                                        tint = Color(0xFF003915),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Text(
                                    text = tagName,
                                    color = if (isAssigned) Color(0xFF003915) else TextCrispPrimary,
                                    fontSize = 12.sp,
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
                            color = TextCrispPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(EmeraldPrimary),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EmeraldDarkSurfaceHigh)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (newTagInput.isEmpty()) {
                                    Text(
                                        text = "+ Buat tag baru...",
                                        color = TextSubduedTertiary,
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
                            .background(if (newTagInput.isNotBlank()) EmeraldPrimary else EmeraldDarkSurfaceHigh)
                            .then(
                                if (newTagInput.isNotBlank()) Modifier.springClickable {
                                    onCreateAndAssignTag(newTagInput)
                                    newTagInput = ""
                                } else Modifier
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Tambah",
                            color = if (newTagInput.isNotBlank()) Color(0xFF003915) else TextSubduedTertiary,
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
                        .background(EmeraldPrimaryContainer)
                        .springClickable { onDismiss() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Selesai",
                        color = EmeraldOnPrimaryContainer,
                        fontSize = 13.sp,
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
                                    lastImportStatus = "Gagal: ${result.message}"
                                }
                            }
                        }
                    } else {
                        isSuccess = false
                        lastImportStatus = "Gagal membuka file CSV"
                    }
                } catch (e: Exception) {
                    isSuccess = false
                    lastImportStatus = "Error: ${e.message}"
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(EmeraldDarkSurfaceContainer)
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.contactsImportDialogTitle,
                            color = TextCrispPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pilih file CSV atau muat contoh instan",
                            color = TextMutedBody,
                            fontSize = 11.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(EmeraldDarkSurfaceHigh)
                            .springClickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Tutup",
                            tint = TextMutedBody,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = "File CSV dapat memuat kolom: Nama, Nomor Telepon, Panggilan, Perusahaan, Custom_1, dan Tag. Pemisah koma (,) atau titik-koma (;) didukung otomatis.",
                    color = TextMutedBody,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                // Option 1: File Picker
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldPrimaryContainer)
                        .springClickable {
                            csvPickerLauncher.launch("*/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            tint = EmeraldOnPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = strings.contactsImportPickFile,
                            color = EmeraldOnPrimaryContainer,
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
                        .background(EmeraldDarkSurfaceHigh)
                        .springClickable {
                            coroutineScope.launch {
                                val result = onImportSample()
                                if (result is com.wabulk.sender.data.io.CsvParseResult.Success) {
                                    isSuccess = true
                                    lastImportStatus = strings.contactsImportResult(result.contacts.size, result.rowCount)
                                } else if (result is com.wabulk.sender.data.io.CsvParseResult.Error) {
                                    isSuccess = false
                                    lastImportStatus = "Gagal: ${result.message}"
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
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = strings.contactsImportLoadSample,
                            color = TextCrispPrimary,
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
                            .background(if (isSuccess) EmeraldPrimary.copy(alpha = 0.15f) else Color(0xFF381414))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = lastImportStatus!!,
                            color = if (isSuccess) EmeraldPrimary else Color(0xFFFFB4AB),
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
                        .background(EmeraldDarkSurfaceLow)
                        .springClickable { onDismiss() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tutup",
                        color = TextCrispPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


