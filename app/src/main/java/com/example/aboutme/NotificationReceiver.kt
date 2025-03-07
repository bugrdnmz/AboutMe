package com.example.aboutme

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getLongExtra("NOTE_ID", 0)
        val noteTitle = intent.getStringExtra("NOTE_TITLE") ?: "Not Hatırlatıcı"
        val noteContent = intent.getStringExtra("NOTE_CONTENT") ?: ""
        val noteLocation = intent.getStringExtra("NOTE_LOCATION") ?: ""
        val hasLocation = intent.getBooleanExtra("HAS_LOCATION", false)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId.toInt(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Konum bilgisi varsa içerik metnine ekle
        val notificationText = if (hasLocation && noteLocation.isNotEmpty()) {
            "$noteContent\nKonum: $noteLocation"
        } else {
            noteContent
        }

        val builder = NotificationCompat.Builder(context, "daily_notes_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(noteTitle)
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(noteId.toInt(), builder.build())
    }
}