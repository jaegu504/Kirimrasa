package com.wabulk.sender.ui.screens.composer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.wabulk.sender.data.locale.composerTexts
import com.wabulk.sender.domain.model.MediaAttachment
import com.wabulk.sender.domain.model.MediaType
import com.wabulk.sender.theme.AppleTheme
import com.wabulk.sender.theme.IconTile
import com.wabulk.sender.theme.SegmentedControl
import com.wabulk.sender.theme.TileGradients

internal val MediaType.icon: ImageVector
    get() = when (this) {
        MediaType.IMAGE -> Icons.Default.Image
        MediaType.VIDEO -> Icons.Default.PlayArrow
        MediaType.AUDIO -> Icons.Default.AudioFile
        MediaType.DOCUMENT -> Icons.Default.Description
    }

internal val MediaType.gradient: List<Color>
    get() = when (this) {
        MediaType.IMAGE -> TileGradients.Pink
        MediaType.VIDEO -> TileGradients.Indigo
        MediaType.AUDIO -> TileGradients.Orange
        MediaType.DOCUMENT -> TileGradients.Blue
    }

/**
 * One attached file shown inside the bubble it is sent with: thumbnail, name, remove button
 * and a "send file in: Bubble 1 | Bubble 2 | Separate" picker.
 */
@Composable
fun AttachmentCard(
    attachment: MediaAttachment,
    bubbleCount: Int,
    onPlacementChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = AppleTheme.colors
    val t = composerTexts()
    // Same rule as the dispatcher: any index outside the bubbles is sent on its own after them
    val separate = attachment.bubbleIndex !in 0 until bubbleCount

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.cardInset)
            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (attachment.mediaType == MediaType.IMAGE) {
                AsyncImage(
                    model = attachment.uriString,
                    contentDescription = attachment.fileName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(c.fill)
                )
            } else {
                IconTile(icon = attachment.mediaType.icon, gradient = attachment.mediaType.gradient, size = 44.dp)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = attachment.fileName,
                    color = c.label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${t.mediaLabel(attachment.mediaType)} · ${attachment.formattedSize} · " +
                        if (separate) t.sentSeparately else t.sentWithBubble,
                    color = c.secondaryLabel,
                    fontSize = 13.sp,
                    lineHeight = 17.sp
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = t.removeAttachment,
                    tint = c.tertiaryLabel,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(text = t.sendFileIn, color = c.secondaryLabel, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        SegmentedControl(
            // Up to 5 bubbles + "separate" would not fit as "Balon N", so use bare numbers then
            options = (1..bubbleCount).map { if (bubbleCount > 3) "$it" else t.bubble(it) } + t.separate,
            selectedIndex = if (separate) bubbleCount else attachment.bubbleIndex,
            onSelect = { onPlacementChange(if (it == bubbleCount) MediaAttachment.SEPARATE_AFTER else it) },
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}
