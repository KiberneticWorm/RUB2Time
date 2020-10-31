package ru.rubt.rubttimetable.viewmodel

import android.content.Context
import androidx.room.Room
import okhttp3.OkHttpClient
import ru.rubt.rubttimetable.R
import ru.rubt.rubttimetable.db.AppDatabase

class AppContainer(private val ctx: Context) {

    private val appDatabase = Room
            .databaseBuilder(ctx, AppDatabase::class.java, ctx.getString(R.string.db_name))
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    private val lessonDao = appDatabase.lessonDao()

    private val appSettings = ctx.getSharedPreferences(ctx.getString(R.string.app_preferences), Context.MODE_PRIVATE)
    private val client = OkHttpClient()

    private val timeLocalDataSource = TimeLocalDataSource(ctx, lessonDao, appSettings)
    private val timeRemoteDataSource = TimeRemoteDataSource(ctx, client, appSettings)

    private val timeRepository = TimeRepository(ctx, timeLocalDataSource, timeRemoteDataSource)
    val timeViewModelFactory = TimeViewModelFactory(timeRepository)

}