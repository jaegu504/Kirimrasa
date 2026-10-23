package com.wabulk.sender.theme

import androidx.core.view.WindowCompat
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.SideEffect
import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

// Dark Apple Glassmorphism Cybernetic Palette
private val DarkEmeraldColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color(0xFF003915),
    primaryContainer = Color(0xFF047857),
    onPrimaryContainer = Color(0xFFD1FAE5),
    inversePrimary = Color(0xFF006D2F),

    secondary = Color(0xFF2DD4BF),
    onSecondary = Color(0xFF003824),
    secondaryContainer = Color(0xFF115E59),
    onSecondaryContainer = Color(0xFFCCFBF1),

    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF003825),
    tertiaryContainer = Color(0xFF0369A1),
    onTertiaryContainer = Color(0xFFE0F2FE),

    background = Color(0xFF0B0F17),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF131B26),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    surfaceTint = Color(0xFF10B981),

    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155),

    error = StatusError,
    onError = StatusOnError,
    errorContainer = StatusErrorContainer,
    onErrorContainer = Color(0xFFFFDAD6)
)

// Light Apple Glassmorphism Palette (Ultra clean, high contrast)
private val LightEmeraldColorScheme = lightColorScheme(
    primary = Color(0xFF059669),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF065F46),

    secondary = Color(0xFF0D9488),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF115E59),

    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,

    background = Color(0xFFF4F7FB),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),

    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A)
)

@Composable
fun KirimrasaTheme(
    themeMode: ThemeMode? = null,
    content: @Composable () -> Unit
) {
    val currentMode by ThemeManager.themeMode.collectAsState()
    val activeMode = themeMode ?: currentMode
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (activeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemDark
    }

    // Status/navigation bar icons follow the app theme, not the system one (they can differ)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDark
                isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    val colorScheme = if (isDark) DarkEmeraldColorScheme else LightEmeraldColorScheme
    val neumorphicColors = if (isDark) DarkNeumorphicColors else LightNeumorphicColors

    CompositionLocalProvider(
        LocalNeumorphicColors provides neumorphicColors,
        LocalAppleColors provides if (isDark) DarkAppleColors else LightAppleColors,
        LocalTextStyle provides TextStyle(fontFamily = PlusJakartaSansFamily)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = EmeraldTypography,
            content = content
        )
    }
}

@Composable
fun WhatsAppBulkTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    KirimrasaTheme(
        themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
        content = content
    )
}
