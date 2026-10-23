package com.wabulk.sender.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("kirimrasa_theme_prefs", Context.MODE_PRIVATE)
        val saved = prefs?.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        _themeMode.value = runCatching { ThemeMode.valueOf(saved) }.getOrDefault(ThemeMode.SYSTEM)
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs?.edit()?.putString("theme_mode", mode.name)?.apply()
    }
}

/**
 * Apple iOS Glassmorphism Design System Colors
 */
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

// Legacy tokens, aligned with AppleColors so screens not yet restyled still match
val DarkNeumorphicColors = NeumorphicColors(
    background = Color(0xFF000000),
    surface = Color(0xFF1C1C1E),
    surfaceRaised = Color(0xFF2C2C2E),
    surfaceInset = Color(0xFF2C2C2E),
    lightShadow = Color(0x00000000),
    darkShadow = Color(0x40000000),
    primary = Color(0xFF0A84FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0B2A4F),
    onPrimaryContainer = Color(0xFF7AB8FF),
    textPrimary = Color(0xFFFFFFFF),
    textMuted = Color(0xFFAEAEB2),
    textSubdued = Color(0xFF8E8E93),
    borderBevel = Brush.verticalGradient(
        listOf(
            Color(0x33FFFFFF),
            Color(0x14FFFFFF)
        )
    ),
    isDark = true
)

// Light Apple Glassmorphic Theme (High contrast, crystal clear)
val LightNeumorphicColors = NeumorphicColors(
    background = Color(0xFFF2F2F7),
    surface = Color(0xFFFFFFFF),
    surfaceRaised = Color(0xFFF4F5F8),
    surfaceInset = Color(0xFFF4F5F8),
    lightShadow = Color(0x80FFFFFF),
    darkShadow = Color(0x0F000000),
    primary = Color(0xFF007AFF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAF2FF),
    onPrimaryContainer = Color(0xFF0A5FD6),
    textPrimary = Color(0xFF1C1C1E),
    textMuted = Color(0xFF5C5C61),
    textSubdued = Color(0xFF8E8E93),
    borderBevel = Brush.verticalGradient(
        listOf(
            Color(0x00FFFFFF),
            Color(0x1F3C3C43)
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
 * Apple iOS Frosted Glass depth & ambient shadow
 */
fun Modifier.glassDepth(
    cornerRadius: Dp = 18.dp,
    elevation: Dp = 2.dp,
    isDark: Boolean = true
): Modifier = this.drawBehind {
    val cornerPx = cornerRadius.toPx()
    val elevPx = elevation.toPx()
    val shadowColor = if (isDark) Color(0x33000000) else Color(0x0E0F172A)
    drawRoundRect(
        color = shadowColor,
        topLeft = Offset(0f, elevPx * 0.75f),
        size = size,
        cornerRadius = CornerRadius(cornerPx, cornerPx)
    )
}

/**
 * Reusable modifier to apply Apple Glassmorphism styling
 */
fun Modifier.neumorphic(
    lightColor: Color = Color.Transparent,
    darkColor: Color = Color.Transparent,
    cornerRadius: Dp = 18.dp,
    elevation: Dp = 2.dp,
    isInset: Boolean = false
): Modifier = this.glassDepth(cornerRadius = cornerRadius, elevation = elevation, isDark = true)

@Composable
fun Modifier.neumorphic(
    cornerRadius: Dp = 18.dp,
    elevation: Dp = 2.dp,
    isInset: Boolean = false
): Modifier {
    val colors = LocalNeumorphicColors.current
    return this.glassDepth(cornerRadius = cornerRadius, elevation = elevation, isDark = colors.isDark)
}

/**
 * Apple iOS Glassmorphic Card container
 */
@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    cornerRadius: Dp = 18.dp,
    elevation: Dp = 2.dp,
    isInset: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = NeumorphismTheme.colors
    Box(
        modifier = modifier
            .glassDepth(cornerRadius = cornerRadius, elevation = elevation, isDark = colors.isDark)
            .clip(shape)
            .background(if (isInset) colors.surfaceInset else colors.surface)
            .border(0.8.dp, colors.borderBevel, shape)
            .padding(14.dp),
        content = content
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 3.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val colors = NeumorphismTheme.colors
    Box(
        modifier = modifier
            .glassDepth(cornerRadius = cornerRadius, elevation = elevation, isDark = colors.isDark)
            .clip(shape)
            .background(colors.surface)
            .border(0.8.dp, colors.borderBevel, shape)
            .padding(contentPadding),
        content = content
    )
}

/**
 * iOS ambient radiant mesh background
 */
fun Modifier.appleGlassBackground(isDark: Boolean): Modifier = this.drawBehind {
    if (!isDark) {
        drawRect(Color(0xFFF4F7FB))
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0x1810B981), Color(0x0010B981)),
                center = Offset(size.width * 0.1f, size.height * 0.08f),
                radius = size.width * 0.75f
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0x1406B6D4), Color(0x0006B6D4)),
                center = Offset(size.width * 0.9f, size.height * 0.35f),
                radius = size.width * 0.65f
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0x106366F1), Color(0x006366F1)),
                center = Offset(size.width * 0.4f, size.height * 0.9f),
                radius = size.width * 0.8f
            )
        )
    } else {
        drawRect(Color(0xFF0B0F17))
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0x22059669), Color(0x00059669)),
                center = Offset(size.width * 0.15f, size.height * 0.1f),
                radius = size.width * 0.85f
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0x1A4F46E5), Color(0x004F46E5)),
                center = Offset(size.width * 0.85f, size.height * 0.45f),
                radius = size.width * 0.75f
            )
        )
    }
}
