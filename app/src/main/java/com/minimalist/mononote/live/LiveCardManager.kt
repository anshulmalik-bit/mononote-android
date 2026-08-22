package com.minimalist.mononote.live

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.minimalist.mononote.MainActivity
import com.minimalist.mononote.R
import com.minimalist.mononote.data.NoteEntity
import com.minimalist.mononote.data.NoteRepository

class LiveCardManager(
    private val context: Context,
    private val repository: NoteRepository
) {

    companion object {
        const val CHANNEL_ID = "mononote_live_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_COMPLETE_NOTE = "com.minimalist.mononote.ACTION_COMPLETE_NOTE"
        const val ACTION_COPY_NOTE = "com.minimalist.mononote.ACTION_COPY_NOTE"
        const val ACTION_STOP_LIVE = "com.minimalist.mononote.ACTION_STOP_LIVE"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.live_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.live_channel_desc)
                setShowBadge(false)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                enableVibration(false)
                setSound(null, null)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun updateLiveCard(note: NoteEntity) {
        if (!note.isLive) {
            dismissLiveCard()
            return
        }

        val contentText = note.content.ifBlank { context.getString(R.string.note_placeholder) }

        // Intent to open editor on tap
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Dismiss Live Action Intent
        val stopIntent = Intent(context, LiveActionReceiver::class.java).apply {
            action = ACTION_STOP_LIVE
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            3,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mononote)
            .setContentText(contentText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(contentText)
            )
            .setOngoing(true)
            .setAutoCancel(false)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(openPendingIntent)
            .addAction(0, "Dismiss", stopPendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Android 13+ notification permission not granted yet
        }
    }

    fun dismissLiveCard() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
}
