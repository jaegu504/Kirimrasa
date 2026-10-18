package com.wabulk.sender.ui.tutorial

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.theme.LocalNeumorphicColors
import com.wabulk.sender.theme.PlusJakartaSansFamily

object TutorialManager {
    private const val PREFS_NAME = "kirimrasa_onboarding"
    private const val KEY_SEEN = "has_seen_tutorial"

    fun hasSeenTutorial(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SEEN, false)
    }

    fun setTutorialSeen(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SEEN, true).apply()
    }
}

@Composable
fun OnboardingTutorialDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val neuColors = LocalNeumorphicColors.current
    var currentStep by remember { mutableIntStateOf(0) }

    val steps = listOf(
        TutorialStep(
            title = strings.tutorialStep1Title,
            description = strings.tutorialStep1Desc,
            icon = Icons.Default.AutoAwesome,
            accentGradient = listOf(Color(0xFF10B981), Color(0xFF06B6D4))
        ),
        TutorialStep(
            title = strings.tutorialStep2Title,
            description = strings.tutorialStep2Desc,
            icon = Icons.Default.Hub,
            accentGradient = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))
        ),
        TutorialStep(
            title = strings.tutorialStep3Title,
            description = strings.tutorialStep3Desc,
            icon = Icons.Default.Security,
            accentGradient = listOf(Color(0xFF10B981), Color(0xFF8B5CF6))
        ),
        TutorialStep(
            title = strings.tutorialStep4Title,
            description = strings.tutorialStep4Desc,
            icon = Icons.Default.Group,
            accentGradient = listOf(Color(0xFF10B981), Color(0xFF14B8A6))
        )
    )

    Dialog(
        onDismissRequest = {
            TutorialManager.setTutorialSeen(context)
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp))
                .background(neuColors.surface)
                .border(1.dp, neuColors.borderBevel, RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Step Indicators (Apple Glass Pills)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        steps.indices.forEach { index ->
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(if (index == currentStep) 24.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == currentStep) neuColors.accentPrimary
                                        else neuColors.surfaceInset
                                    )
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            TutorialManager.setTutorialSeen(context)
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = strings.tutorialSkipBtn,
                            color = neuColors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = PlusJakartaSansFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Animated Slide Content
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tutorial_slide"
                ) { stepIdx ->
                    val step = steps[stepIdx]
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icon with vibrant Glass glow
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(Brush.linearGradient(step.accentGradient.map { it.copy(alpha = 0.2f) }))
                                .border(1.dp, Brush.linearGradient(step.accentGradient.map { it.copy(alpha = 0.4f) }), RoundedCornerShape(22.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = step.accentGradient.first(),
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        Text(
                            text = step.title,
                            color = neuColors.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontFamily = PlusJakartaSansFamily
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = step.description,
                            color = neuColors.textSecondary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = PlusJakartaSansFamily,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Action Button (Apple Glass style)
                Button(
                    onClick = {
                        if (currentStep < steps.lastIndex) {
                            currentStep++
                        } else {
                            TutorialManager.setTutorialSeen(context)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = neuColors.accentPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentStep < steps.lastIndex) strings.tutorialNextBtn else strings.tutorialStartBtn,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlusJakartaSansFamily
                        )
                        Icon(
                            imageVector = if (currentStep < steps.lastIndex) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class TutorialStep(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentGradient: List<Color>
)
