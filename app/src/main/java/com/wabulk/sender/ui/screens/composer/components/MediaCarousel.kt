package com.wabulk.sender.ui.screens.composer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.domain.model.MediaAttachment
import com.wabulk.sender.domain.model.MediaType
import com.wabulk.sender.theme.EmeraldDarkSurfaceContainer
import com.wabulk.sender.theme.EmeraldDarkSurfaceHigh
import com.wabulk.sender.theme.EmeraldDarkSurfaceLow
import com.wabulk.sender.theme.EmeraldPrimary
import com.wabulk.sender.theme.EmeraldPrimaryContainer
import com.wabulk.sender.theme.EmeraldSecondaryFixedVariant
import com.wabulk.sender.theme.StatusError
import com.wabulk.sender.theme.TextCrispPrimary
import com.wabulk.sender.theme.TextMutedBody
import com.wabulk.sender.theme.springClickable

@Composable
fun MediaCarousel(
    attachments: List<MediaAttachment>,
    onAddAttachment: () -> Unit,
    onRemoveAttachment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = strings.composerMediaQueue,
                    color = TextCrispPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = strings.composerAutoCompressed,
                    color = TextMutedBody,
                    fontSize = 11.sp
                )
            }
            Text(
                text = strings.composerAttachedCount(attachments.size),
                color = EmeraldPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Horizontal Attachment Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // + Attach Button (enabled if < 5 attachments)
            val canAttach = attachments.size < 5
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 108.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (canAttach) EmeraldDarkSurfaceLow else EmeraldDarkSurfaceLow.copy(alpha = 0.5f))
                    .border(1.dp, EmeraldDarkSurfaceHigh, RoundedCornerShape(16.dp))
                    .then(
                        if (canAttach) Modifier.springClickable { onAddAttachment() }
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldDarkSurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = if (canAttach) EmeraldPrimary else TextMutedBody,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = strings.composerAttachButton,
                        color = if (canAttach) TextMutedBody else TextMutedBody.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Attached Media Items
            for (attachment in attachments) {
                MediaAttachmentCard(
                    attachment = attachment,
                    onRemove = { onRemoveAttachment(attachment.id) }
                )
            }
        }
    }
}

@Composable
private fun MediaAttachmentCard(
    attachment: MediaAttachment,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 96.dp, height = 108.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(EmeraldDarkSurfaceHigh)
    ) {
        when (attachment.mediaType) {
            MediaType.IMAGE -> {
                if (attachment.uriString.isNotBlank() && !attachment.uriString.startsWith("dummy")) {
                    AsyncImage(
                        model = attachment.uriString,
                        contentDescription = attachment.fileName,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF003824), Color(0xFF001F13))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = EmeraldPrimary.copy(alpha = 0.7f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // File size badge at bottom
                Text(
                    text = attachment.formattedSize,
                    color = TextCrispPrimary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }

            MediaType.VIDEO -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0A2218), Color(0xFF04140D))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = attachment.formattedSize,
                        color = TextCrispPrimary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                    Text(
                        text = "MP4",
                        color = EmeraldPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(EmeraldSecondaryFixedVariant)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            MediaType.DOCUMENT, MediaType.AUDIO -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF381414)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = StatusError,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column {
                        Text(
                            text = attachment.fileName,
                            color = TextCrispPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${attachment.formattedSize} • Doc",
                            color = TextMutedBody,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        // Close / Remove button
        Box(
            modifier = Modifier
                .padding(6.dp)
                .size(20.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(StatusError)
                .springClickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.Black,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
