package ru.rubt.rubttimetable.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.jsoup.Jsoup
import ru.rubt.rubttimetable.R
import ru.rubt.rubttimetable.activity.SplashActivity
import ru.rubt.rubttimetable.viewmodel.TimeRemoteDataSource
import java.util.HashSet

class CheckWorker(
        @NonNull appContext: Context,
        @NonNull params: WorkerParameters) : Worker(appContext, params) {

    object Constants {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "rub2time_channel_1"
        const val CHANNEL_NAME = "rub2time_channel"
        const val CHANNEL_DESC = "This is channel tells about changes"
    }

    private var notification: Notification
    private var notificationManager: NotificationManager
    private val sharedPreferences = applicationContext.getSharedPreferences(
            applicationContext.getString(R.string.app_preferences), Context.MODE_PRIVATE)
    private val pdfHeaders = HashSet<String>(sharedPreferences.getStringSet(appContext.getString(R.string.pdf_headers), HashSet()))

    init {
        notification = createNotification()
        notificationManager = createNotificationManager()
        createNotificationChannel(notificationManager)
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

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = Constants.CHANNEL_DESC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun isUpdate() : Boolean {
        val document = Jsoup.connect(applicationContext.getString(R.string.rubt_url)).get()
        val links = document.select(".contentpaneopen:has(td:contains(Замены в расписании)) + .contentpaneopen td > p > a")

        var header: String

        for (link in links) {
            header = link.text().trim { it <= ' ' }
            if (!pdfHeaders.contains(header)) {
                pdfHeaders.add(header)
                return true
            }
        }
        return false
    }

    override fun doWork(): Result {

        try {
            if (isUpdate()) {
                notificationManager.notify(Constants.NOTIFICATION_ID, notification)
            }
        } catch (exc: Exception) {
            // Internet is denied
        }

        return Result.success()
    }
}