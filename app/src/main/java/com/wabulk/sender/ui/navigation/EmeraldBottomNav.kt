package com.wabulk.sender.ui.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.theme.EmeraldDarkSurfaceLow
import com.wabulk.sender.theme.EmeraldOnPrimaryContainer
import com.wabulk.sender.theme.EmeraldPrimary
import com.wabulk.sender.theme.EmeraldPrimaryContainer
import com.wabulk.sender.theme.EmeraldSpring
import com.wabulk.sender.theme.TextCrispPrimary
import com.wabulk.sender.theme.TextMutedBody
import com.wabulk.sender.theme.pulseGlow
import com.wabulk.sender.theme.springClickable

@Composable
fun EmeraldBottomNav(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(EmeraldDarkSurfaceLow.copy(alpha = 0.95f))
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
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

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .springClickable { onTabSelected(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = label,
                                tint = if (isSelected) EmeraldPrimary else TextMutedBody,
                                modifier = Modifier.size(24.dp)
                            )

                            // Queue Badge Counter
                            if (tab.badgeCount != null && tab.badgeCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tab.badgeCount.toString(),
                                        color = EmeraldOnPrimaryContainer,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = label,
                            color = if (isSelected) EmeraldPrimary else TextMutedBody,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        // Glowing bottom indicator pill for active tab
                        val indicatorWidth by animateDpAsState(
                            targetValue = if (isSelected) 28.dp else 0.dp,
                            animationSpec = EmeraldSpring.bouncy(),
                            label = "indicatorWidth"
                        )

                        Box(
                            modifier = Modifier
                                .padding(top = 3.dp)
                                .height(2.5.dp)
                                .width(indicatorWidth)
                                .clip(CircleShape)
                                .background(if (isSelected) EmeraldPrimary else Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}
