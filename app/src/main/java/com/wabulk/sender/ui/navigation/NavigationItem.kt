package com.wabulk.sender.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ScheduleSend
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

enum class ScreenTab(
    val route: String,
    val icon: ImageVector,
    val badgeCount: Int? = null
) {
    COMPOSE("compose", Icons.AutoMirrored.Filled.Send),
    CONTACTS("contacts", Icons.Default.Contacts),
    QUEUE("queue", Icons.AutoMirrored.Filled.ScheduleSend, badgeCount = 4),
    SETTINGS("settings", Icons.Default.Tune)
}
