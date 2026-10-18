package com.wabulk.sender.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

// Dark Emerald Cybernetic Palette (Primary App Theme)
private val DarkEmeraldColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color(0xFF003915),
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = EmeraldOnPrimaryContainer,
    inversePrimary = Color(0xFF006D2F),

    secondary = EmeraldSecondary,
    onSecondary = Color(0xFF003824),
    secondaryContainer = EmeraldSecondaryContainer,
    onSecondaryContainer = EmeraldOnSecondaryContainer,

    tertiary = EmeraldTertiary,
    onTertiary = Color(0xFF003825),
    tertiaryContainer = EmeraldTertiaryContainer,
    onTertiaryContainer = EmeraldOnTertiaryContainer,

    background = EmeraldDarkSurface,
    onBackground = TextCrispPrimary,
    surface = EmeraldDarkSurface,
    onSurface = TextCrispPrimary,
    surfaceVariant = EmeraldDarkSurfaceHighest,
    onSurfaceVariant = TextMutedBody,
    surfaceTint = EmeraldPrimary,

    outline = OutlineColor,
    outlineVariant = OutlineVariantColor,

    error = StatusError,
    onError = StatusOnError,
    errorContainer = StatusErrorContainer,
    onErrorContainer = Color(0xFFFFDAD6)
)

// Light Palette option matching Neumorphic Light Design
private val LightEmeraldColorScheme = lightColorScheme(
    primary = Color(0xFF059669),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF10B981),
    onPrimaryContainer = Color.White,

    secondary = Color(0xFF0D9488),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF115E59),

    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,

    background = Color(0xFFE6EDF5),
    onBackground = Color(0xFF1E293B),
    surface = Color(0xFFE6EDF5),
    onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFEDF3FA),
    onSurfaceVariant = Color(0xFF475569),

    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFFCBD5E1),

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

    val colorScheme = if (isDark) DarkEmeraldColorScheme else LightEmeraldColorScheme
    val neumorphicColors = if (isDark) DarkNeumorphicColors else LightNeumorphicColors

    androidx.compose.runtime.CompositionLocalProvider(
        LocalNeumorphicColors provides neumorphicColors
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
