package com.wabulk.sender.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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

// Light Palette option matching Stitch DESIGN.md
private val LightEmeraldColorScheme = lightColorScheme(
    primary = Color(0xFF016D2F),
    onPrimary = Color.White,
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = EmeraldOnPrimaryContainer,

    secondary = Color(0xFF006C49),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF9AF2C5),
    onSecondaryContainer = Color(0xFF0C714D),

    tertiary = Color(0xFF006C4A),
    onTertiary = Color.White,

    background = LightCanvasBg,
    onBackground = LightTextPrimary,
    surface = LightSurface1,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceHigh,
    onSurfaceVariant = LightTextMuted,

    outline = Color(0xFF6C7B6B),
    outlineVariant = Color(0xFFBBCBB9),

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A)
)

@Composable
fun WhatsAppBulkTheme(
    darkTheme: Boolean = true, // Default to cybernetic dark theme as shown in Stitch screens
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkEmeraldColorScheme else LightEmeraldColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EmeraldTypography,
        content = content
    )
}
