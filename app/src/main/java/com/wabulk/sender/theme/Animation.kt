package com.wabulk.sender.theme

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Reusable spring physics configurations engineered for high-tactility interactions.
 */
object EmeraldSpring {
    // Damping ratio: low bouncy (~0.65f), stiffness: medium low for organic fluid feel
    val Bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    fun <T> bouncy() = spring<T>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val Tactile = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    val Smooth = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

/**
 * Tactile spring scale modifier on touch/click.
 * Provides micro-elevation state shift under touch as specified in Stitch DESIGN.md.
 */
@Composable
fun Modifier.springClickable(
    enabled: Boolean = true,
    pressedScale: Float = 0.96f,
    haptic: Boolean = false,
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    // Quick squeeze on press, bouncy release, like iOS buttons
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = if (isPressed) spring(stiffness = Spring.StiffnessHigh) else EmeraldSpring.Tactile,
        label = "pressScale"
    )
    val hapticFeedback = LocalHapticFeedback.current

    return this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = {
                if (haptic) hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
        )
}

/** Odometer-style transition for changing numbers: rolls up when the value grows, down when it shrinks. */
fun countRollTransition(initial: Int, target: Int): ContentTransform {
    val up = target > initial
    return (slideInVertically(spring(stiffness = Spring.StiffnessMediumLow)) { h -> if (up) h else -h } + fadeIn())
        .togetherWith(slideOutVertically(spring(stiffness = Spring.StiffnessMediumLow)) { h -> if (up) -h else h } + fadeOut())
}

/**
 * Cybernetic pulsating halo effect for active nodes, radar, and anti-ban indicators.
 */
@Composable
fun Modifier.pulseGlow(
    minScale: Float = 0.85f,
    maxScale: Float = 1.15f,
    durationMs: Int = 1400
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseGlowTransition")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlowScale"
    )

    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
