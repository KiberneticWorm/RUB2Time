package ru.rubt.rubttimetable.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.rubt.rubttimetable.R
import ru.rubt.rubttimetable.activity.SplashActivity
import java.util.HashSet

class TimeMessageService : FirebaseMessagingService() {

    object Constants {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "rub2time_channel_1"
        const val CHANNEL_NAME = "rub2time_channel"
        const val CHANNEL_DESC = "This is channel tells about changes"
    }

    private lateinit var notification: Notification
    private lateinit var pdfHeaders : HashSet<String>

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = applicationContext.getSharedPreferences(
                applicationContext.getString(R.string.app_preferences), Context.MODE_PRIVATE)

        pdfHeaders = HashSet(
                sharedPreferences.getStringSet(applicationContext.getString(R.string.pdf_headers),
                HashSet()))

        createNotificationChannel()
        notification = createNotification()
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        if (msg.data.isNotEmpty()) {
            val changes = msg.data.getValue("changes")
            if (!changes.isNullOrEmpty()) {
                val changesArray = changes.split("_");
                for (change in changesArray) {
                    if (!pdfHeaders.contains(change)) {
                        pdfHeaders.add(change)
                        sendNotification()
                    }
                }
            }
        }
    }

    private fun sendNotification() {
        createNotificationManager().notify(Constants.NOTIFICATION_ID, notification)
    }

    private fun createNotification() : Notification {
        val intent = Intent(applicationContext, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        return NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.stopwatch)
                .setContentTitle("RUB2Time")
                .setContentText("Появились замены")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
    }

    private fun createNotificationManager() : NotificationManager {
        return applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = Constants.CHANNEL_DESC
            }
            createNotificationManager().createNotificationChannel(channel)
        }
    }
}