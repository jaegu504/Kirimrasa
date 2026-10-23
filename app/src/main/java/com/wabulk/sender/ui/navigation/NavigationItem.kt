package com.wabulk.sender.ui.navigation

import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ScheduleSend
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

enum class ScreenTab(
    val route: String,
    val icon: ImageVector
) {
    COMPOSE("compose", Icons.AutoMirrored.Filled.Send),
    CONTACTS("contacts", Icons.Default.Person),
    QUEUE("queue", Icons.Default.Schedule),
    SETTINGS("settings", Icons.Default.Tune)
}
