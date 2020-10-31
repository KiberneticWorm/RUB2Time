package ru.rubt.rubttimetable.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import ru.rubt.rubttimetable.App
import ru.rubt.rubttimetable.R
import ru.rubt.rubttimetable.notifications.CheckWorker
import ru.rubt.rubttimetable.viewmodel.TimeViewModel
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    private lateinit var timeViewModel: TimeViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // uses only portrait orientation, don't supports landscape
        
        timeViewModel = ((application as App).getTimeViewModelFactory().create())

        timeViewModel.isLoaded.observe(this, Observer<Boolean> {
            val workManager = WorkManager.getInstance(applicationContext)
            workManager.cancelAllWork()
            workManager.enqueue(PeriodicWorkRequestBuilder<CheckWorker>(5, TimeUnit.MINUTES).build())
            startActivity(Intent(this, SelectGroupActivity::class.java))
            finish()
        })
    }
}