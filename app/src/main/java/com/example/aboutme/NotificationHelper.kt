// 1. Öncelikle NotificationHelper sınıfını ekleyelim
package com.example.aboutme

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_ID = "daily_notes_channel"
        private const val CHANNEL_NAME = "Günlük Notlar"
        private const val CHANNEL_DESCRIPTION = "Günlük notlar ve görevler için bildirimler"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Not için bildirim planla
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(note: DailyNote) {
        // Bildirim aktif ve zamanı ayarlanmışsa
        if (!note.hasNotification || note.notificationTime.isNullOrEmpty()) {
            return
        }

        try {
            // Bildirim zamanını ayrıştır
            val timeParts = note.notificationTime.split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            // Not tarihini ayrıştır
            val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("tr", "TR"))
            val noteDate = dateFormat.parse(note.day)

            // Bildirim için takvimi ayarla
            val calendar = Calendar.getInstance()
            if (noteDate != null) {
                calendar.time = noteDate
            }
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            // Geçmiş zamansa planlamayı iptal et
            if (calendar.timeInMillis < System.currentTimeMillis()) {
                return
            }

            // Bildirim içeriğini hazırla
            val notificationContent = if (note.hasLocationAlert && note.locationName != null) {
                "${note.note} (${note.locationName} konumunda)"
            } else {
                note.note
            }

            // Alarm için intent oluştur
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("NOTE_ID", note.id)
                putExtra("NOTE_TITLE", note.note)
                putExtra("NOTE_CONTENT", note.extra_column ?: "")
                putExtra("NOTE_LOCATION", note.locationName ?: "")
                putExtra("HAS_LOCATION", note.hasLocationAlert)
            }

            // Not ID'sine göre benzersiz request code oluştur
            val requestCode = note.id.toInt()

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Alarmı planla
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Planlanmış bildirimi iptal et
    fun cancelNotification(noteId: Long) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val requestCode = noteId.toInt()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    // Test için hemen bildirim göster
    fun showTestNotification(title: String, content: String, location: String? = null) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationText = if (!location.isNullOrEmpty()) {
            "$content\nKonum: $location"
        } else {
            content
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
