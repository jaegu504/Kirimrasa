package com.wabulk.sender.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Apple-style design tokens. Colours carry meaning:
 * blue = actions/navigation, green = connected/send, orange = safety/delays,
 * purple = AI, red = stop/errors. Every *Tint has a matching *OnTint text colour that passes 4.5:1.
 */
data class AppleColors(
    val isDark: Boolean,
    val bgTop: Color,
    val bgMid: Color,
    val bgBottom: Color,
    val card: Color,
    val cardInset: Color,
    val fill: Color,
    val label: Color,
    val secondaryLabel: Color,
    val tertiaryLabel: Color,
    val separator: Color,
    val blue: Color,
    val green: Color,
    val teal: Color,
    val orange: Color,
    val purple: Color,
    val red: Color,
    val blueTint: Color,
    val blueOnTint: Color,
    val greenTint: Color,
    val greenOnTint: Color,
    val orangeTint: Color,
    val orangeOnTint: Color,
    val purpleTint: Color,
    val purpleOnTint: Color,
    val redTint: Color,
    val redOnTint: Color,
    val tabBar: Color,
    val chatWallpaperTop: Color,
    val chatWallpaperBottom: Color,
    val chatBubble: Color
)

val LightAppleColors = AppleColors(
    isDark = false,
    bgTop = Color(0xFFEEF4FF),
    bgMid = Color(0xFFF6F3FF),
    bgBottom = Color(0xFFF2F2F7),
    card = Color(0xFFFFFFFF),
    cardInset = Color(0xFFF4F5F8),
    fill = Color(0x1F767680),
    label = Color(0xFF1C1C1E),
    secondaryLabel = Color(0xFF5C5C61),
    tertiaryLabel = Color(0xFF8E8E93),
    separator = Color(0x2E3C3C43),
    blue = Color(0xFF007AFF),
    green = Color(0xFF34C759),
    teal = Color(0xFF20B2AA),
    orange = Color(0xFFFF9500),
    purple = Color(0xFFAF52DE),
    red = Color(0xFFFF3B30),
    blueTint = Color(0xFFEAF2FF),
    blueOnTint = Color(0xFF0A5FD6),
    greenTint = Color(0xFFE6F8EC),
    greenOnTint = Color(0xFF1B7F37),
    orangeTint = Color(0xFFFFF1E6),
    orangeOnTint = Color(0xFF9E4A00),
    purpleTint = Color(0xFFF4EAFC),
    purpleOnTint = Color(0xFF7A2BA8),
    redTint = Color(0xFFFFE5E5),
    redOnTint = Color(0xFFC4001A),
    tabBar = Color(0xEBF9F9FB),
    chatWallpaperTop = Color(0xFFEFE7DD),
    chatWallpaperBottom = Color(0xFFE8DFD3),
    chatBubble = Color(0xFFD9FDD3)
)

val DarkAppleColors = AppleColors(
    isDark = true,
    bgTop = Color(0xFF0E1424),
    bgMid = Color(0xFF130F22),
    bgBottom = Color(0xFF000000),
    card = Color(0xFF1C1C1E),
    cardInset = Color(0xFF2C2C2E),
    fill = Color(0x3D767680),
    label = Color(0xFFFFFFFF),
    secondaryLabel = Color(0xFFAEAEB2),
    tertiaryLabel = Color(0xFF8E8E93),
    separator = Color(0x5C545458),
    blue = Color(0xFF0A84FF),
    green = Color(0xFF30D158),
    teal = Color(0xFF40C8E0),
    orange = Color(0xFFFF9F0A),
    purple = Color(0xFFBF5AF2),
    red = Color(0xFFFF453A),
    blueTint = Color(0xFF0B2A4F),
    blueOnTint = Color(0xFF7AB8FF),
    greenTint = Color(0xFF0E3A1D),
    greenOnTint = Color(0xFF6EE08A),
    orangeTint = Color(0xFF3D2608),
    orangeOnTint = Color(0xFFFFB952),
    purpleTint = Color(0xFF34183F),
    purpleOnTint = Color(0xFFDDA3F7),
    redTint = Color(0xFF3F1212),
    redOnTint = Color(0xFFFF8A82),
    tabBar = Color(0xEB161618),
    chatWallpaperTop = Color(0xFF0B141A),
    chatWallpaperBottom = Color(0xFF0B141A),
    chatBubble = Color(0xFF005C4B)
)

val LocalAppleColors = staticCompositionLocalOf { LightAppleColors }

/** Blend between the light and dark palettes; fraction 0 = light, 1 = dark. Drives the animated theme switch. */
fun lerpAppleColors(fraction: Float): AppleColors {
    val a = LightAppleColors
    val b = DarkAppleColors
    fun m(x: Color, y: Color) = lerp(x, y, fraction)
    return AppleColors(
        isDark = fraction >= 0.5f,
        bgTop = m(a.bgTop, b.bgTop), bgMid = m(a.bgMid, b.bgMid), bgBottom = m(a.bgBottom, b.bgBottom),
        card = m(a.card, b.card), cardInset = m(a.cardInset, b.cardInset), fill = m(a.fill, b.fill),
        label = m(a.label, b.label), secondaryLabel = m(a.secondaryLabel, b.secondaryLabel),
        tertiaryLabel = m(a.tertiaryLabel, b.tertiaryLabel), separator = m(a.separator, b.separator),
        blue = m(a.blue, b.blue), green = m(a.green, b.green), teal = m(a.teal, b.teal),
        orange = m(a.orange, b.orange), purple = m(a.purple, b.purple), red = m(a.red, b.red),
        blueTint = m(a.blueTint, b.blueTint), blueOnTint = m(a.blueOnTint, b.blueOnTint),
        greenTint = m(a.greenTint, b.greenTint), greenOnTint = m(a.greenOnTint, b.greenOnTint),
        orangeTint = m(a.orangeTint, b.orangeTint), orangeOnTint = m(a.orangeOnTint, b.orangeOnTint),
        purpleTint = m(a.purpleTint, b.purpleTint), purpleOnTint = m(a.purpleOnTint, b.purpleOnTint),
        redTint = m(a.redTint, b.redTint), redOnTint = m(a.redOnTint, b.redOnTint),
        tabBar = m(a.tabBar, b.tabBar),
        chatWallpaperTop = m(a.chatWallpaperTop, b.chatWallpaperTop),
        chatWallpaperBottom = m(a.chatWallpaperBottom, b.chatWallpaperBottom),
        chatBubble = m(a.chatBubble, b.chatBubble)
    )
}

object AppleTheme {
    val colors: AppleColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppleColors.current
}

/** Gradient pairs for icon tiles, like the Settings app. */
object TileGradients {
    val Blue = listOf(Color(0xFF5AC8FA), Color(0xFF007AFF))
    val Teal = listOf(Color(0xFF64D2FF), Color(0xFF30B0C7))
    val Green = listOf(Color(0xFF5BE584), Color(0xFF25D366))
    val WhatsAppBusiness = listOf(Color(0xFF3FD1B8), Color(0xFF128C7E))
    val Orange = listOf(Color(0xFFFFC24D), Color(0xFFFF9500))
    val Purple = listOf(Color(0xFFBF5AF2), Color(0xFF7D5BF5))
    val Indigo = listOf(Color(0xFF8E8CFF), Color(0xFF5856D6))
    val Pink = listOf(Color(0xFFFF8FA3), Color(0xFFFF2D55))
    val Gray = listOf(Color(0xFF8E8E93), Color(0xFF636366))
    val Send = listOf(Color(0xFF34C759), Color(0xFF20B2AA))
}

/** Soft vertical gradient behind every screen. */
fun Modifier.appleBackground(colors: AppleColors): Modifier = this.drawBehind {
    drawRect(Brush.verticalGradient(listOf(colors.bgTop, colors.bgMid, colors.bgBottom)))
}

@Composable
fun LargeTitle(text: String, modifier: Modifier = Modifier, trailing: @Composable RowScope.() -> Unit = {}) {
    val c = AppleTheme.colors
    Row(
        modifier = modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(text = text, color = c.label, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.8).sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically, content = trailing)
    }
}

/** Uppercase grey caption above a grouped card. */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier, trailing: @Composable RowScope.() -> Unit = {}) {
    val c = AppleTheme.colors
    Row(
        modifier = modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text.uppercase(), color = c.secondaryLabel, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.4.sp)
        Row(verticalAlignment = Alignment.CenterVertically, content = trailing)
    }
}

/** White rounded card that groups related rows. */
@Composable
fun GroupCard(
    modifier: Modifier = Modifier,
    padding: Dp = 0.dp,
    spacing: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val c = AppleTheme.colors
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(if (c.isDark) 0.dp else 1.dp, shape, ambientColor = Color(0x14000000), spotColor = Color(0x14000000))
            .clip(shape)
            .background(c.card)
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content
    )
}

@Composable
fun RowSeparator(startInset: Dp = 62.dp) {
    Box(
        modifier = Modifier
            .padding(start = startInset)
            .fillMaxWidth()
            .height(0.5.dp)
            .background(AppleTheme.colors.separator)
    )
}

/** Rounded square with a gradient and a white glyph. */
@Composable
fun IconTile(icon: ImageVector, gradient: List<Color>, size: Dp = 34.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.3f))
            .background(Brush.linearGradient(gradient)),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(size * 0.56f))
    }
}

/**
 * Settings-style row: optional icon tile, title, subtitle, trailing content and a chevron when tappable.
 * The whole row is the tap target (min 56 dp).
 */
@Composable
fun ListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    subtitleColor: Color? = null,
    icon: ImageVector? = null,
    iconGradient: List<Color> = TileGradients.Blue,
    leading: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    showChevron: Boolean = onClick != null,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    val c = AppleTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .then(if (onClick != null) Modifier.springClickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            leading != null -> leading()
            icon != null -> IconTile(icon = icon, gradient = iconGradient)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, color = c.label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) {
                Text(text = subtitle, color = subtitleColor ?: c.secondaryLabel, fontSize = 13.sp, lineHeight = 17.sp)
            }
        }
        trailing()
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = c.tertiaryLabel,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/** iOS segmented control. The white thumb slides between segments on a spring; each segment is a real selectable tab ≥ 40 dp. */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedTextColor: Color? = null
) {
    val c = AppleTheme.colors
    val count = options.size.coerceAtLeast(1)
    val thumbPosition by animateFloatAsState(
        targetValue = selectedIndex.coerceIn(0, count - 1).toFloat(),
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow),
        label = "segmentThumb"
    )
    val thumbColor = if (c.isDark) Color(0xFF636366) else Color.White
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.fill)
            .padding(3.dp)
            .drawBehind {
                val w = size.width / count
                val radius = CornerRadius(9.dp.toPx())
                val left = w * thumbPosition
                if (!c.isDark) {
                    drawRoundRect(Color(0x14000000), Offset(left, 1.dp.toPx()), Size(w, size.height), radius)
                }
                drawRoundRect(thumbColor, Offset(left, 0f), Size(w, size.height), radius)
            }
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val textColor by animateColorAsState(
                targetValue = if (selected) (selectedTextColor ?: c.label) else c.secondaryLabel,
                animationSpec = tween(220),
                label = "segmentText"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp)
                    .semantics { role = Role.Tab; this.selected = selected }
                    .springClickable(haptic = true) { if (!selected) onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Big green-teal capsule for the one primary action on a screen. */
@Composable
fun PrimaryCapsuleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    gradient: List<Color> = TileGradients.Send
) {
    val shape = RoundedCornerShape(28.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(if (enabled) 10.dp else 0.dp, shape, ambientColor = gradient.last(), spotColor = gradient.last())
            .clip(shape)
            .background(if (enabled) Brush.linearGradient(gradient) else Brush.linearGradient(listOf(Color(0xFFC7C7CC), Color(0xFFAEAEB2))))
            .semantics { role = Role.Button }
            .then(if (enabled) Modifier.springClickable(onClick = onClick) else Modifier),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
        }
        Text(text = text, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

/** Rounded pill button in a tinted colour (e.g. "+ Nama panggilan"). */
@Composable
fun TintPill(
    text: String,
    background: Color,
    content: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    selected: Boolean = false
) {
    Row(
        modifier = modifier
            .heightIn(min = 40.dp)
            .clip(CircleShape)
            .background(background)
            .semantics { role = Role.Button; this.selected = selected }
            .springClickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) Icon(imageVector = icon, contentDescription = null, tint = content, modifier = Modifier.size(16.dp))
        Text(text = text, color = content, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

/** iOS-coloured switch. */
@Composable
fun AppleSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val c = AppleTheme.colors
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = c.green,
            checkedBorderColor = c.green,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = c.fill,
            uncheckedBorderColor = Color.Transparent
        )
    )
}

/** Small coloured status dot + text, e.g. "● Tersambung". */
@Composable
fun StatusLabel(text: String, color: Color, textColor: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(text = text, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
