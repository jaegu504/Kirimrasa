package com.wabulk.sender.ui.screens.composer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.wabulk.sender.data.locale.LocalAppStrings
import com.wabulk.sender.domain.model.MediaAttachment
import com.wabulk.sender.domain.model.MediaType
import com.wabulk.sender.domain.model.MessageBubble
import com.wabulk.sender.domain.spintax.SpintaxParser
import com.wabulk.sender.theme.EmeraldPrimary
import com.wabulk.sender.theme.EmeraldSecondaryContainer
import com.wabulk.sender.theme.LocalNeumorphicColors
import com.wabulk.sender.theme.StatusError
import com.wabulk.sender.theme.WhatsAppBlueTicks
import com.wabulk.sender.theme.WhatsAppChatBubbleGreen
import com.wabulk.sender.theme.WhatsAppChatBubbleText

@Composable
fun WhatsAppPreviewBubble(
    bubbles: List<MessageBubble>,
    attachments: List<MediaAttachment>,
    modifier: Modifier = Modifier,
    contactName: String = "Sarah Miller",
    contactInitials: String = "S",
    customVariables: Map<String, String> = emptyMap()
) {
    val strings = LocalAppStrings.current
    val neuColors = LocalNeumorphicColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(neuColors.surface)
    ) {
        // WhatsApp Top Recipient Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(neuColors.surfaceRaised)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(EmeraldSecondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contactInitials,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = contactName,
                        color = neuColors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.composerOnlinePreview,
                        color = neuColors.primary,
                        fontSize = 9.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(neuColors.surfaceInset)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = neuColors.primary,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = strings.composerLivePreviewTitle,
                    color = neuColors.textSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // WhatsApp Wallpaper Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF080D11))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth(0.90f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Media preview if attachments exist
                if (attachments.isNotEmpty()) {
                    val firstAttachment = attachments.first()
                    PreviewMediaCard(attachment = firstAttachment)
                }

                // Render each message bubble
                bubbles.forEachIndexed { index, bubble ->
                    val resolvedText = remember(bubble.text, contactName, customVariables) {
                        val spun = SpintaxParser.spin(bubble.text)
                        val nick = contactName.split(" ").firstOrNull() ?: "Sarah"
                        SpintaxParser.interpolateVariables(
                            text = spun,
                            nickname = nick,
                            variables = customVariables
                        )
                    }

                    SingleWhatsAppBubble(
                        text = resolvedText.ifEmpty { "..." },
                        timestamp = "10:${42 + index} AM",
                        isFirst = index == 0,
                        isLast = index == bubbles.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewMediaCard(attachment: MediaAttachment) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        when (attachment.mediaType) {
            MediaType.IMAGE -> {
                if (attachment.uriString.isNotBlank() && !attachment.uriString.startsWith("dummy")) {
                    AsyncImage(
                        model = attachment.uriString,
                        contentDescription = attachment.fileName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = attachment.fileName.uppercase(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            MediaType.VIDEO -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "${attachment.fileName} (${attachment.formattedSize})",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            MediaType.DOCUMENT, MediaType.AUDIO -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = StatusError,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "${attachment.fileName} • ${attachment.formattedSize}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleWhatsAppBubble(
    text: String,
    timestamp: String,
    isFirst: Boolean,
    isLast: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = if (isFirst) 4.dp else 12.dp,
                    bottomStart = 14.dp,
                    bottomEnd = if (isLast) 14.dp else 6.dp
                )
            )
            .background(WhatsAppChatBubbleGreen)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = text,
            color = WhatsAppChatBubbleText,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )

        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = timestamp,
                color = Color(0xFF8696A0),
                fontSize = 10.sp
            )
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Read",
                tint = WhatsAppBlueTicks,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}
