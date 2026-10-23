package com.wabulk.sender.ui.update

import com.wabulk.sender.data.locale.LocalAppStrings
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wabulk.sender.theme.EmeraldPrimary
import com.wabulk.sender.theme.LocalNeumorphicColors
import com.wabulk.sender.theme.PlusJakartaSansFamily
import com.wabulk.sender.theme.StatusError
import com.wabulk.sender.update.AppUpdateInfo
import com.wabulk.sender.update.UpdateManager
import com.wabulk.sender.update.UpdateState

@Composable
fun AppUpdateDialog(
    onDismiss: () -> Unit = { UpdateManager.dismissDialog() }
) {
    val context = LocalContext.current
    val neuColors = LocalNeumorphicColors.current
    val updateState by UpdateManager.updateState.collectAsState()

    Dialog(
        onDismissRequest = {
            if (updateState !is UpdateState.Downloading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = updateState !is UpdateState.Downloading,
            dismissOnClickOutside = updateState !is UpdateState.Downloading,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp))
                .background(neuColors.surface)
                .border(1.dp, neuColors.borderBevel, RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            AnimatedContent(
                targetState = updateState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "UpdateDialogContent"
            ) { state ->
                when (state) {
                    is UpdateState.Available -> {
                        AvailableContent(
                            info = state.info,
                            onUpdate = { UpdateManager.startDownloadAndInstall(context, state.info) },
                            onDismiss = onDismiss
                        )
                    }
                    is UpdateState.Downloading -> {
                        DownloadingContent(
                            progressPercent = state.progressPercent,
                            downloadedBytes = state.downloadedBytes,
                            totalBytes = state.totalBytes
                        )
                    }
                    is UpdateState.ReadyToInstall -> {
                        ReadyToInstallContent(
                            onInstall = { UpdateManager.installApk(context, state.apkFile) }
                        )
                    }
                    is UpdateState.Error -> {
                        ErrorContent(
                            errorMessage = state.message,
                            onRetry = { UpdateManager.checkForUpdates(context, isManual = true) },
                            onDismiss = onDismiss
                        )
                    }
                    is UpdateState.UpToDate -> {
                        UpToDateContent(onDismiss = onDismiss)
                    }
                    UpdateState.Checking -> {
                        CheckingContent()
                    }
                    UpdateState.Idle -> {
                        // Empty/hidden
                    }
                }
            }
        }
    }
}

@Composable
private fun AvailableContent(
    info: AppUpdateInfo,
    onUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val neuColors = LocalNeumorphicColors.current
    val mbSize = if (info.apkSize > 0) String.format("%.1f MB", info.apkSize / (1024f * 1024f)) else strings.updateApkLabel

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing Icon Badge
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(EmeraldPrimary.copy(alpha = 0.35f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RocketLaunch,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = strings.updateAvailableTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaSansFamily,
            color = neuColors.textPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Version tags
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(neuColors.surfaceInset)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "v${UpdateManager.CURRENT_VERSION} → ${info.latestVersion}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(neuColors.surfaceInset)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = mbSize,
                    fontSize = 12.sp,
                    color = neuColors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Changelog Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(neuColors.surfaceInset)
                .padding(14.dp)
        ) {
            Text(
                text = strings.updateReleaseNotes,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = neuColors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .heightIn(max = 140.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = info.changelog.ifBlank { strings.updateDefaultChangelog },
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = neuColors.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Button(
            onClick = onUpdate,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = strings.updateNowBtn,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = strings.updateLaterBtn,
                fontSize = 13.sp,
                color = neuColors.textSecondary
            )
        }
    }
}

@Composable
private fun DownloadingContent(
    progressPercent: Int,
    downloadedBytes: Long,
    totalBytes: Long
) {
    val strings = LocalAppStrings.current
    val neuColors = LocalNeumorphicColors.current
    val downloadedMb = String.format("%.1f", downloadedBytes / (1024f * 1024f))
    val totalMb = if (totalBytes > 0) String.format("%.1f", totalBytes / (1024f * 1024f)) else "?"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progressPercent / 100f },
                color = EmeraldPrimary,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = strings.updateDownloading,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = neuColors.textPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "$downloadedMb MB / $totalMb MB ($progressPercent%)",
            fontSize = 12.sp,
            color = neuColors.textSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        LinearProgressIndicator(
            progress = { progressPercent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = EmeraldPrimary,
            trackColor = neuColors.surfaceInset
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = strings.updateDownloadHint,
            fontSize = 11.sp,
            color = neuColors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReadyToInstallContent(
    onInstall: () -> Unit
) {
    val strings = LocalAppStrings.current
    val neuColors = LocalNeumorphicColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = strings.updateReadyTitle,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = neuColors.textPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = strings.updateReadyDesc,
            fontSize = 12.sp,
            color = neuColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onInstall,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
            Text(
                text = strings.updateInstallBtn,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val neuColors = LocalNeumorphicColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(StatusError.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = StatusError,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = strings.updateDownloadFailedTitle,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = neuColors.textPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = errorMessage,
            fontSize = 12.sp,
            color = neuColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = strings.retryBtn,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = strings.contactsCloseBtn,
                fontSize = 13.sp,
                color = neuColors.textSecondary
            )
        }
    }
}

@Composable
private fun UpToDateContent(
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val neuColors = LocalNeumorphicColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = strings.updateUpToDateTitle,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = neuColors.textPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = strings.updateUpToDateDesc(UpdateManager.CURRENT_VERSION),
            fontSize = 12.sp,
            color = neuColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
            Text(
                text = strings.understoodBtn,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun CheckingContent() {
    val strings = LocalAppStrings.current
    val neuColors = LocalNeumorphicColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = EmeraldPrimary,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = strings.updateCheckingTitle,
            fontSize = 14.sp,
            color = neuColors.textPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}
