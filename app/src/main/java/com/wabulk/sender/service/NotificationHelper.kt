package com.wabulk.sender.service

import com.wabulk.sender.data.locale.LocalizationManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.wabulk.sender.MainActivity
import com.wabulk.sender.R

object NotificationHelper {
    const val CHANNEL_ID = "wa_bulk_broadcast_channel"
    const val CHANNEL_NAME = "Pengiriman Massal WhatsApp"
    const val NOTIFICATION_ID = 1001

    const val UPDATE_CHANNEL_ID = "kirimrasa_update_channel"
    const val UPDATE_CHANNEL_NAME = "Pembaruan Kirimrasa"
    const val UPDATE_NOTIFICATION_ID = 2002

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val broadcastChannel = NotificationChannel(
                CHANNEL_ID,
                LocalizationManager.activeStrings.notifSendingChannel,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = LocalizationManager.activeStrings.notifSendingChannelDesc
                setShowBadge(false)
            }
            manager.createNotificationChannel(broadcastChannel)

            val updateChannel = NotificationChannel(
                UPDATE_CHANNEL_ID,
                LocalizationManager.activeStrings.notifUpdateChannel,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = LocalizationManager.activeStrings.notifUpdateChannelDesc
                setShowBadge(true)
                enableVibration(true)
            }
            manager.createNotificationChannel(updateChannel)
        }
    }

    fun showUpdateAvailableNotification(
        context: Context,
        latestVersion: String,
        changelog: String
    ) {
        createNotificationChannel(context)

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_OPEN_UPDATE_DIALOG", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            UPDATE_NOTIFICATION_ID,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snippet = if (changelog.isNotBlank()) {
            changelog.lines().firstOrNull { it.isNotBlank() } ?: LocalizationManager.activeStrings.updateDefaultChangelog
        } else {
            LocalizationManager.activeStrings.updateDefaultChangelog
        }

        val notification = NotificationCompat.Builder(context, UPDATE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(LocalizationManager.activeStrings.notifUpdateTitle(latestVersion))
            .setContentText(snippet)
            .setStyle(NotificationCompat.BigTextStyle().bigText(LocalizationManager.activeStrings.notifUpdateBody(latestVersion, snippet)))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(UPDATE_NOTIFICATION_ID, notification)
    }

    fun buildNotification(
        context: Context,
        title: String = "WhatsApp Bulk Sender",
        content: String = LocalizationManager.activeStrings.notifServiceRunning,
        progressMax: Int = 0,
        progressCurrent: Int = 0,
        isIndeterminate: Boolean = false
    ): Notification {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (progressMax > 0) {
            builder.setProgress(progressMax, progressCurrent, isIndeterminate)
        }

        return builder.build()
    }
}
