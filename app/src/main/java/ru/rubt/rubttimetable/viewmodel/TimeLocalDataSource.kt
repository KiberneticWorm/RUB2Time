package ru.rubt.rubttimetable.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.lifecycle.LiveData
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSDocument
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import ru.rubt.rubttimetable.R
import ru.rubt.rubttimetable.db.Lesson
import ru.rubt.rubttimetable.db.LessonDao

import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class TimeLocalDataSource(
        private val ctx: Context,
        private val lessonDao: LessonDao,
        private val appSettings: SharedPreferences,
) {

    object Constants {

        const val SAVED_GROUP = "_saved_group_"
        const val ROW_GROUPS = 1
        const val DELIMITER_ADULTS = ","

    }

    private val beforeTimes = ctx.resources.getStringArray(R.array.before_times)
    private val afterTimes = ctx.resources.getStringArray(R.array.after_times)
    private val beforeTimesUn = ctx.resources.getStringArray(R.array.before_times_un)
    private val afterTimesUn = ctx.resources.getStringArray(R.array.after_times_un)

    fun isEmptyDB() : Boolean = lessonDao.all.isEmpty()

    fun getGroups() : LiveData<List<String>> = lessonDao.groups

    fun getPdfHeaders() : Set<String> = appSettings.getStringSet(ctx.getString(R.string.pdf_headers), HashSet<String>())!!

    fun saveGroup(group: String) {
        with (appSettings.edit()) {
            putString(Constants.SAVED_GROUP, group)
            apply()
        }
    }

    fun getSavedGroup() : String = appSettings.getString(Constants.SAVED_GROUP, ctx.getString(R.string.saved_group_default))!!

    fun getByGroupAndWeekdayOdd(group: String, weekday: String) : List<Lesson> = lessonDao.getByGroupAndWeekdayOdd(group, weekday)

    fun getByGroupAndWeekdayEven(group: String, weekday: String): List<Lesson> = lessonDao.getByGroupAndWeekdayEven(group, weekday)

    // Working with XLS files
    fun loadXLSFile(bytes: ByteArray) {
        val groups: MutableList<String> = ArrayList()

        val workbook = HSSFWorkbook(bytes.inputStream())
        val sheet = workbook.getSheetAt(0)
        val groupRowIterator = sheet.getRow(Constants.ROW_GROUPS).iterator() as Iterator<HSSFCell>

        // get first part time of second lesson
        // for example 9:50 (usually)
        // for example 9:35 (unusually)
        val isUsuallyTime = TextUtils.equals(sheet.getRow(3).getCell(2)
                .stringCellValue.split("-".toRegex()).toTypedArray()[0]
                .replace(".", ":").trim(), afterTimes[0])
        while (groupRowIterator.hasNext()) {
            val cell = groupRowIterator.next()
            if (cell.cellType == Cell.CELL_TYPE_STRING) {
                groups.add(cell.stringCellValue.trim())
            }
        }
        val iterator: Iterator<Row> = sheet.iterator()
        iterator.next()
        iterator.next()
        var numberLesson = 1
        var numberWeekday = 0
        var currRow: HSSFRow
        val lessons = ArrayList<Lesson>()
        while (iterator.hasNext()) {
            currRow = iterator.next() as HSSFRow
            if (numberLesson == 5) {
                numberLesson = 1
                numberWeekday++
            }
            loadOneRow(lessons, groups, currRow, numberLesson, numberWeekday, isUsuallyTime)
            numberLesson++
        }
        lessonDao.clear()
        lessonDao.insertAll(*lessons.toTypedArray())
    }

    private fun formatTimetable(timetable: String): String {
        return timetable.trim { it <= ' ' }.replace("[\\s]{2,}".toRegex(), " ")
    }

    private fun loadOneRow(lessons: MutableList<Lesson>, groups: List<String>, currRow: HSSFRow, numberLesson: Int, numberWeekday: Int, isUsuallyTime: Boolean) {
        var timetable: Array<String>
        var campus: String
        var time: String
        var index = 3

        // definite schedule to use logical variable "isUsuallyTime"
        val befTimes = if (isUsuallyTime) beforeTimes else beforeTimesUn
        val aftTimes = if (isUsuallyTime) afterTimes else afterTimesUn
        for (group in groups) {
            val cellCampus = currRow.getCell(index)
            val cellTimetable = currRow.getCell(index + 1)
            if (cellCampus != null && cellTimetable != null) {
                timetable = cellTimetable.stringCellValue.split(Constants.DELIMITER_ADULTS.toRegex(), 2).toTypedArray()
                campus = getCampus(cellCampus)
                val numLesson = if (isFive(cellCampus)) 5 else numberLesson
                time = if (numberLesson >= 3 && numLesson <= 5 && numberWeekday == 5) {
                    "${befTimes[numberLesson + 2]}\n-\n${aftTimes[numberLesson + 2]}"
                } else {
                    "${befTimes[numLesson - 1]}\n-\n${aftTimes[numLesson - 1]}"
                }

                val weekdays = ctx.resources.getStringArray(R.array.weekdays)

                if (timetable.size == 1) { // lesson without a fraction
                    if (!TextUtils.isEmpty(timetable[0].trim())) {
                        lessons.add(Lesson(time, formatTimetable(timetable[0]), campus, numLesson,  // add lesson
                                group, weekdays[numberWeekday], CellEnum.USUALLY_CELL.value))
                    }
                } else if (timetable.size == 2) { // lesson with a fraction
                    when {
                        TextUtils.isEmpty(timetable[0].trim()) -> { // upper fraction lesson is empty
                            lessons.add(Lesson(time, formatTimetable(timetable[1]), campus, numLesson, // add lesson
                                    group, weekdays[numberWeekday], CellEnum.EVEN_CELL.value))
                        }
                        TextUtils.isEmpty(timetable[1].trim()) -> { // lower fraction lesson is empty
                            lessons.add(Lesson(time, formatTimetable(timetable[0]), campus, numLesson, // add lesson
                                    group, weekdays[numberWeekday], CellEnum.ODD_CELL.value))
                        }
                        else -> { // both lessons are not empty
                            val campusArr = arrayOfNulls<String>(2)
                            campusArr[1] = campus
                            campusArr[0] = campusArr[1]
                            if (campus != "") {
                                val campusTempArr = campus.split(" ".toRegex(), 2).toTypedArray()
                                if (campusTempArr.size == 2) {
                                    campusArr[0] = campusTempArr[0]
                                    campusArr[1] = campusTempArr[1]
                                }
                            }
                            lessons.add(Lesson(time, formatTimetable(timetable[0]), campusArr[0]!!, numLesson, // add upper fraction lesson
                                    group, weekdays[numberWeekday], CellEnum.ODD_CELL.value))
                            lessons.add(Lesson(time, formatTimetable(timetable[1]), campusArr[1]!!, numLesson, // add lower fraction lesson
                                    group, weekdays[numberWeekday], CellEnum.EVEN_CELL.value))
                        }
                    }
                }
            }
            index += 2
        }
    }

    private fun isFive(cellCampus: HSSFCell): Boolean {
        if (cellCampus.cellType != Cell.CELL_TYPE_BLANK) {
            val campus = cellCampus.stringCellValue
            if (campus.trim { it <= ' ' }.matches("5\\s*пара\\s*.*".toRegex())) return true
        }
        return false
    }

    private fun getCampus(cellCampus: HSSFCell): String {
        return if (cellCampus.cellType == Cell.CELL_TYPE_BLANK ||
                TextUtils.isEmpty(cellCampus.stringCellValue)) {
            ""
        } else {
            cellCampus.stringCellValue.replace("5\\s*пара\\s*".toRegex(), "").trim { it <= ' ' }
        }
    }



}