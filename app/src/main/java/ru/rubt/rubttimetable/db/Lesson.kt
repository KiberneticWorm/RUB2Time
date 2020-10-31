package ru.rubt.rubttimetable.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_lesson")
class Lesson(@field:ColumnInfo(name = "lessonTime") var time: String,
                  @field:ColumnInfo(name = "lessonName") var name: String,
                  @field:ColumnInfo(name = "lessonCampus") var campus: String,
                  @field:ColumnInfo(name = "lessonNumber") var number: Int,
                  @field:ColumnInfo(name = "lessonGroupName") var groupName: String,
                  @field:ColumnInfo(name = "lessonWeekday") var weekday: String,
                  @field:ColumnInfo(name = "lessonLocation") var location: Int) {
    @PrimaryKey(autoGenerate = true)
    var lessonId: Long = 0
}