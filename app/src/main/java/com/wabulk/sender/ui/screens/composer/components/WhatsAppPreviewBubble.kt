package com.wabulk.sender.ui.screens.composer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.wabulk.sender.data.locale.composerTexts
import com.wabulk.sender.domain.model.MediaAttachment
import com.wabulk.sender.domain.model.MediaType
import com.wabulk.sender.domain.model.MessageBubble
import com.wabulk.sender.domain.spintax.SpintaxParser
import com.wabulk.sender.theme.AppleTheme
import com.wabulk.sender.theme.IconTile
import com.wabulk.sender.theme.RowSeparator
import com.wabulk.sender.theme.TileGradients
import com.wabulk.sender.theme.WhatsAppBlueTicks
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Live WhatsApp-style preview: each bubble as the contact will see it, files inside their bubble. */
@Composable
fun WhatsAppPreviewBubble(
    bubbles: List<MessageBubble>,
    attachments: List<MediaAttachment>,
    contactName: String,
    customVariables: Map<String, String>,
    modifier: Modifier = Modifier
) {
    val c = AppleTheme.colors
    val t = composerTexts()
    val time = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }
    val initials = contactName.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1).uppercase() }

    // (text, files) per chat bubble; files outside the bubbles go last on their own
    val entries = bubbles.mapIndexed { i, b -> b.text to attachments.filter { it.bubbleIndex == i } } +
        attachments.filter { it.bubbleIndex !in bubbles.indices }.map { "" to listOf(it) }
    val visible = entries.filter { (text, files) -> text.isNotBlank() || files.isNotEmpty() }

    Column(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(c.card)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(TileGradients.Orange)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Column {
                Text(text = contactName, color = c.label, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = t.typing, color = c.greenOnTint, fontSize = 13.sp)
            }
        }
        RowSeparator(startInset = 0.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(c.chatWallpaperTop, c.chatWallpaperBottom)))
                .padding(start = 12.dp, end = 12.dp, top = 14.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (visible.isEmpty()) {
                Text(
                    text = t.previewEmpty,
                    color = c.secondaryLabel,
                    fontSize = 15.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    textAlign = TextAlign.Center
                )
            }
            visible.forEachIndexed { index, (text, files) ->
                val resolved = remember(text, contactName, customVariables) {
                    SpintaxParser.interpolateVariables(SpintaxParser.spin(text), contactName, customVariables)
                }
                ChatBubble(text = resolved, files = files, time = time, isFirst = index == 0)
            }
        }
    }
}

@Composable
private fun ChatBubble(text: String, files: List<MediaAttachment>, time: String, isFirst: Boolean) {
    val c = AppleTheme.colors
    Column(
        modifier = Modifier
            .widthIn(max = 280.dp)
            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = if (isFirst) 4.dp else 14.dp, bottomStart = 14.dp, bottomEnd = 14.dp))
            .background(c.chatBubble)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        files.forEach { file ->
            if (file.mediaType == MediaType.IMAGE) {
                AsyncImage(
                    model = file.uriString,
                    contentDescription = file.fileName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .widthIn(min = 220.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(c.fill)
                )
            } else {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(c.fill)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconTile(icon = file.mediaType.icon, gradient = file.mediaType.gradient, size = 36.dp)
                    Text(
                        text = file.fileName,
                        color = c.label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        if (text.isNotBlank()) {
            Text(
                text = text,
                color = c.label,
                fontSize = 15.sp,
                lineHeight = 21.sp,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp)
            )
        }
        Row(
            modifier = Modifier.align(Alignment.End).padding(end = 6.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = time, color = c.secondaryLabel, fontSize = 11.sp)
            Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, tint = WhatsAppBlueTicks, modifier = Modifier.size(15.dp))
        }
    }
}
