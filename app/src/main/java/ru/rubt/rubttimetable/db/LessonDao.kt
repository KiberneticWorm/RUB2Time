package ru.rubt.rubttimetable.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update

@Dao
interface LessonDao {

    @get:Query("SELECT * FROM table_lesson")
    val all: List<Lesson>

    @Insert
    fun insertAll(vararg lessons: Lesson)

    @Query("SELECT * FROM table_lesson WHERE lessonGroupName LIKE :group")
    fun getByGroup(group: String): List<Lesson>

    @Query("SELECT * FROM table_lesson WHERE lessonGroupName LIKE :group AND lessonWeekday LIKE :weekday ORDER BY lessonNumber")
    fun getByGroupAndWeekday(group: String, weekday: String?): List<Lesson>

    @get:Query("SELECT DISTINCT lessonGroupName FROM table_lesson")
    val groups: LiveData<List<String>>

    @get:Query("SELECT COUNT(lessonGroupName) AS COUNT FROM table_lesson")
    val countGroups: Int

    @Query("SELECT * FROM table_lesson WHERE lessonGroupName LIKE :group AND lessonWeekday LIKE :weekday AND (lessonLocation LIKE 1 OR lessonLocation LIKE 0) ORDER BY lessonNumber")
    fun getByGroupAndWeekdayOdd(group: String, weekday: String): List<Lesson>

    @Query("SELECT * FROM table_lesson WHERE lessonGroupName LIKE :group AND lessonWeekday LIKE :weekday AND (lessonLocation LIKE 2 OR lessonLocation LIKE 0) ORDER BY lessonNumber")
    fun getByGroupAndWeekdayEven(group: String, weekday: String): List<Lesson>

    @Query("DELETE FROM table_lesson")
    fun clear()

    @Update
    fun update(lesson: Lesson)

    @Delete
    fun delete(lesson: Lesson)
}