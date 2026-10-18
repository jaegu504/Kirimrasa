package com.wabulk.sender.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

object ThemeManager {
    private val _themeMode = MutableStateFlow(ThemeMode.DARK)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("kirimrasa_theme_prefs", Context.MODE_PRIVATE)
        val saved = prefs?.getString("theme_mode", ThemeMode.DARK.name) ?: ThemeMode.DARK.name
        _themeMode.value = runCatching { ThemeMode.valueOf(saved) }.getOrDefault(ThemeMode.DARK)
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs?.edit()?.putString("theme_mode", mode.name)?.apply()
    }
}

data class NeumorphicColors(
    val background: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val surfaceInset: Color,
    val lightShadow: Color,
    val darkShadow: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val textSubdued: Color,
    val borderBevel: Brush,
    val isDark: Boolean
) {
    val accentPrimary: Color get() = primary
    val textSecondary: Color get() = textMuted
    val textTertiary: Color get() = textSubdued
    val surfaceContainer: Color get() = surfaceRaised
}

val DarkNeumorphicColors = NeumorphicColors(
    background = Color(0xFF13171D),
    surface = Color(0xFF181F26),
    surfaceRaised = Color(0xFF1E2630),
    surfaceInset = Color(0xFF101419),
    lightShadow = Color(0xFF283442).copy(alpha = 0.65f),
    darkShadow = Color(0xFF07090C).copy(alpha = 0.85f),
    primary = Color(0xFF4FF07F),
    onPrimary = Color(0xFF003915),
    primaryContainer = Color(0xFF25D366),
    onPrimaryContainer = Color(0xFF005523),
    textPrimary = Color(0xFFE2E8F0),
    textMuted = Color(0xFF94A3B8),
    textSubdued = Color(0xFF64748B),
    borderBevel = Brush.linearGradient(
        listOf(
            Color(0xFF2B3746).copy(alpha = 0.5f),
            Color(0xFF090C0F).copy(alpha = 0.7f)
        )
    ),
    isDark = true
)

val LightNeumorphicColors = NeumorphicColors(
    background = Color(0xFFE6EDF5),
    surface = Color(0xFFE6EDF5),
    surfaceRaised = Color(0xFFEDF3FA),
    surfaceInset = Color(0xFFDCE4ED),
    lightShadow = Color.White.copy(alpha = 0.95f),
    darkShadow = Color(0xFFA1AFC2).copy(alpha = 0.6f),
    primary = Color(0xFF059669),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF10B981),
    onPrimaryContainer = Color.White,
    textPrimary = Color(0xFF1E293B),
    textMuted = Color(0xFF475569),
    textSubdued = Color(0xFF64748B),
    borderBevel = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = 0.8f),
            Color(0xFFA1AFC2).copy(alpha = 0.35f)
        )
    ),
    isDark = false
)

val LocalNeumorphicColors = staticCompositionLocalOf { DarkNeumorphicColors }

object NeumorphismTheme {
    val colors: NeumorphicColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNeumorphicColors.current
}

/**
 * Reusable modifier to apply tactile neumorphic depth (dual shadow and soft extrusion)
 */
fun Modifier.neumorphic(
    lightColor: Color,
    darkColor: Color,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 4.dp,
    isInset: Boolean = false
): Modifier = this.drawBehind {
    val cornerPx = cornerRadius.toPx()
    val elevPx = elevation.toPx()

    if (!isInset) {
        // Top-left specular highlight
        drawRoundRect(
            color = lightColor,
            topLeft = Offset(-elevPx * 0.75f, -elevPx * 0.75f),
            size = size,
            cornerRadius = CornerRadius(cornerPx, cornerPx)
        )
        // Bottom-right soft ambient shadow
        drawRoundRect(
            color = darkColor,
            topLeft = Offset(elevPx, elevPx),
            size = size,
            cornerRadius = CornerRadius(cornerPx, cornerPx)
        )
    } else {
        // Inset/debossed shadow
        drawRoundRect(
            color = darkColor.copy(alpha = darkColor.alpha * 0.5f),
            topLeft = Offset(-elevPx * 0.5f, -elevPx * 0.5f),
            size = size,
            cornerRadius = CornerRadius(cornerPx, cornerPx)
        )
        drawRoundRect(
            color = lightColor.copy(alpha = lightColor.alpha * 0.5f),
            topLeft = Offset(elevPx * 0.5f, elevPx * 0.5f),
            size = size,
            cornerRadius = CornerRadius(cornerPx, cornerPx)
        )
    }
}

/**
 * Composable overload for Modifier.neumorphic taking shadow colors automatically from LocalNeumorphicColors.
 */
@Composable
fun Modifier.neumorphic(
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 4.dp,
    isInset: Boolean = false
): Modifier {
    val colors = LocalNeumorphicColors.current
    return this.neumorphic(
        lightColor = colors.lightShadow,
        darkColor = colors.darkShadow,
        cornerRadius = cornerRadius,
        elevation = elevation,
        isInset = isInset
    )
}

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 4.dp,
    isInset: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = NeumorphismTheme.colors
    Box(
        modifier = modifier
            .neumorphic(
                lightColor = colors.lightShadow,
                darkColor = colors.darkShadow,
                cornerRadius = cornerRadius,
                elevation = elevation,
                isInset = isInset
            )
            .clip(shape)
            .background(if (isInset) colors.surfaceInset else colors.surface)
            .border(0.8.dp, colors.borderBevel, shape)
            .padding(14.dp),
        content = content
    )
}
