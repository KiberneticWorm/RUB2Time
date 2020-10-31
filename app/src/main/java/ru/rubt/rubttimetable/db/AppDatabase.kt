package ru.rubt.rubttimetable.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Lesson::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lessonDao(): LessonDao
}
