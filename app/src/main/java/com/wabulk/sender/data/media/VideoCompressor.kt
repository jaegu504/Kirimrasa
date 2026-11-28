package com.wabulk.sender.data.media

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Presentation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.DefaultEncoderFactory
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.Transformer
import androidx.media3.transformer.VideoEncoderSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.coroutineScope
import java.io.File
import kotlin.coroutines.resume

/** Sizes for shrinking a video under the upload limit. Pure, so it is unit tested. */
data class VideoPlan(val videoBps: Int, val width: Int, val height: Int)

object VideoCompressor {
    private const val TARGET_BYTES = 15L * 1024 * 1024
    private const val AUDIO_BPS = 128_000
    private const val MIN_VIDEO_BPS = 300_000
    private const val LOW_VIDEO_BPS = 1_000_000

    /** Null when the video is too long to fit even at a low bitrate. Width and height are as displayed. */
    fun plan(durationMs: Long, width: Int, height: Int): VideoPlan? {
        if (durationMs <= 0 || width <= 0 || height <= 0) return null
        val videoBps = (TARGET_BYTES * 8 * 1000 / durationMs - AUDIO_BPS).toInt()
        if (videoBps < MIN_VIDEO_BPS) return null
        val shortSide = minOf(width, height)
        val targetShort = minOf(if (videoBps < LOW_VIDEO_BPS) 480 else 720, shortSide)
        val k = targetShort.toDouble() / shortSide
        fun even(n: Double) = maxOf(2, (Math.round(n / 2) * 2).toInt())
        return VideoPlan(videoBps, even(width * k), even(height * k))
    }

    sealed interface Result {
        data class Done(val file: File) : Result
        data object TooLong : Result
        data object Failed : Result
    }

    /**
     * Re-encodes [uri] to H.264/AAC MP4 small enough for the upload limit and reports progress 0–100.
     * Cancelling the coroutine cancels the export.
     */
    @OptIn(UnstableApi::class)
    suspend fun compress(context: Context, uri: Uri, onProgress: (Int) -> Unit): Result {
        val meta = withContext(Dispatchers.IO) { readMeta(context, uri) } ?: return Result.Failed
        val plan = plan(meta.durationMs, meta.width, meta.height) ?: return Result.TooLong
        val dir = File(context.filesDir, "compressed").apply { mkdirs() }
        dir.listFiles()?.filter { System.currentTimeMillis() - it.lastModified() > 7L * 24 * 3600 * 1000 }?.forEach { it.delete() }
        val out = File(dir, "video_${System.currentTimeMillis()}.mp4")

        // Transformer must be built and driven from a thread with a Looper: the main thread
        return withContext(Dispatchers.Main) {
            coroutineScope {
                val transformer = Transformer.Builder(context)
                    .setVideoMimeType(MimeTypes.VIDEO_H264)
                    .setAudioMimeType(MimeTypes.AUDIO_AAC)
                    .setEncoderFactory(
                        DefaultEncoderFactory.Builder(context)
                            .setRequestedVideoEncoderSettings(VideoEncoderSettings.Builder().setBitrate(plan.videoBps).build())
                            .build()
                    )
                    .build()
                val item = EditedMediaItem.Builder(MediaItem.fromUri(uri))
                    .setEffects(
                        Effects(
                            emptyList(),
                            listOf(Presentation.createForWidthAndHeight(plan.width, plan.height, Presentation.LAYOUT_SCALE_TO_FIT))
                        )
                    )
                    .build()
                val poll = launch {
                    val holder = ProgressHolder()
                    while (isActive) {
                        if (transformer.getProgress(holder) == Transformer.PROGRESS_STATE_AVAILABLE) onProgress(holder.progress)
                        delay(300)
                    }
                }
                val ok = suspendCancellableCoroutine { cont ->
                    transformer.addListener(object : Transformer.Listener {
                        override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                            if (cont.isActive) cont.resume(true)
                        }

                        override fun onError(composition: Composition, exportResult: ExportResult, exportException: ExportException) {
                            if (cont.isActive) cont.resume(false)
                        }
                    })
                    cont.invokeOnCancellation { transformer.cancel(); out.delete() }
                    transformer.start(item, out.absolutePath)
                }
                poll.cancel()
                if (ok && out.length() > 0) Result.Done(out) else { out.delete(); Result.Failed }
            }
        }
    }

    private data class Meta(val durationMs: Long, val width: Int, val height: Int)

    private fun readMeta(context: Context, uri: Uri): Meta? = try {
        MediaMetadataRetriever().run {
            try {
                setDataSource(context, uri)
                val duration = extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0
                val w = extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
                val h = extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
                val rotation = extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
                if (rotation == 90 || rotation == 270) Meta(duration, h, w) else Meta(duration, w, h)
            } finally {
                release()
            }
        }
    } catch (_: Exception) {
        null
    }
}
