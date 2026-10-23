package com.wabulk.sender.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.theme.AppleTheme
import com.wabulk.sender.theme.springClickable

@Composable
fun EmeraldBottomNav(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    queueBadgeCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val c = AppleTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(c.tabBar)
            .navigationBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(c.separator))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScreenTab.entries.forEach { tab ->
                val isSelected = currentTab == tab
                val label = when (tab) {
                    ScreenTab.COMPOSE -> strings.navCompose
                    ScreenTab.CONTACTS -> strings.navContacts
                    ScreenTab.QUEUE -> strings.navQueue
                    ScreenTab.SETTINGS -> strings.navSettings
                }
                val tint = if (isSelected) c.blue else c.secondaryLabel
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .semantics { role = Role.Tab; selected = isSelected }
                        .springClickable { onTabSelected(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(imageVector = tab.icon, contentDescription = null, tint = tint, modifier = Modifier.size(26.dp))
                        if (tab == ScreenTab.QUEUE && queueBadgeCount > 0) {
                            Box(
                                modifier = Modifier
                                    .offset(x = 8.dp, y = (-4).dp)
                                    .heightIn(min = 18.dp)
                                    .clip(CircleShape)
                                    .background(c.red)
                                    .padding(horizontal = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (queueBadgeCount > 99) "99+" else queueBadgeCount.toString(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = label,
                        color = tint,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
