package ru.rubt.rubttimetable

import android.app.Application
import androidx.room.Room
import ru.rubt.rubttimetable.db.AppDatabase
import ru.rubt.rubttimetable.viewmodel.AppContainer
import ru.rubt.rubttimetable.viewmodel.TimeRepository
import ru.rubt.rubttimetable.viewmodel.TimeViewModelFactory

class App : Application() {

    private lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
    }

    fun getTimeViewModelFactory() : TimeViewModelFactory {
        return appContainer.timeViewModelFactory
    }
}