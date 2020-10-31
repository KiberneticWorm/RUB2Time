package ru.rubt.rubttimetable.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import ru.rubt.rubttimetable.R
import ru.rubt.rubttimetable.db.Lesson
import java.io.IOException
import java.util.HashMap

class TimeRepository(
    private val ctx: Context,
    private val timeLocalDataSource: TimeLocalDataSource,
    private val timeRemoteDataSource: TimeRemoteDataSource
) {

    suspend fun loadTimeTable() = withContext(Dispatchers.IO) {
            try {

                timeRemoteDataSource.updatedPdfFiles()

                val bytes = timeRemoteDataSource.getUpdatedXlsStream()

                if (bytes == null) {
                    loadXLSLocalFile()
                }  else {
                    timeLocalDataSource.loadXLSFile(bytes)
                }

            } catch(exc: IOException) {

                loadXLSLocalFile()

            }
            true
    }

    private fun loadXLSLocalFile() {
        if (timeLocalDataSource.isEmptyDB()) {

            timeLocalDataSource.loadXLSFile(ctx.assets.open(ctx.getString(R.string.local_xls_file)).readBytes())

        }
    }

    fun getGroups() : LiveData<List<String>> {
        return timeLocalDataSource.getGroups()
    }

    fun getPdfHeaders() : Set<String> = timeLocalDataSource.getPdfHeaders()

    suspend fun getLessons(group: String, isOdds: Array<Boolean?>) = withContext(Dispatchers.IO) {
        val lessons: MutableMap<Int, List<Lesson>> = HashMap()
        val weekdays = ctx.resources.getStringArray(R.array.weekdays)
        for (i in 0 until ctx.resources.getInteger(R.integer.num_pages) - 1) { // The Sunday has not lessons
            if (i == 6) {
                lessons[i] = ArrayList()
                continue
            }
            val list = if (isOdds[i]!!) {
                timeLocalDataSource.getByGroupAndWeekdayOdd(group, weekdays[i % 7])
            } else {
                timeLocalDataSource.getByGroupAndWeekdayEven(group, weekdays[i % 7])
            }
            lessons[i] = list
        }
        lessons
    }

    fun saveGroup(group: String) {
        GlobalScope.launch(Dispatchers.IO) {
            timeLocalDataSource.saveGroup(group)
        }
    }

    fun getSavedGroup() : String = timeLocalDataSource.getSavedGroup()
}