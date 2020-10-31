package ru.rubt.rubttimetable.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.rubt.rubttimetable.R
import ru.rubt.rubttimetable.db.Lesson
import ru.rubt.rubttimetable.fragment.FreePageFragment
import ru.rubt.rubttimetable.fragment.WorkdayPageFragment
import java.util.*

// shows the pages that are used to display the schedule
// it also allows you to switch between them
class WeekdayPagerAdapter(
        private val ctx: Context,
        private val manager: FragmentManager,
        private val lessonsByWeekday: Map<Int, List<Lesson>>,
        private val pdfHeaders: Set<String>,
        private val weekdaysStr: Array<String?>,
        private val currentDayOfWeek: Int, // needs to compare
        private val calendar: Calendar
) : FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var headersOfPDFFiles : Array<String>
    private var namesOfPDFFiles: Array<String?> // needs to get the accessing PDF files on local storage

    init {
        val size = pdfHeaders.size
        headersOfPDFFiles = pdfHeaders.toTypedArray()
        namesOfPDFFiles = arrayOfNulls(size)
        for ((index, headerOfPDFFile) in headersOfPDFFiles.withIndex()) {
            val nameOfPDFFile: String = translateHeaderToFilename(headerOfPDFFile)
            headersOfPDFFiles[index] = translateHeaderToTitle(headerOfPDFFile)
            namesOfPDFFiles[index] = nameOfPDFFile
        }
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 6 || position == ctx.resources.getInteger(R.integer.num_pages) - 1) { // if day of week is Sunday
            FreePageFragment(ctx, weekdaysStr[position]!!,
                    position == currentDayOfWeek, headersOfPDFFiles, namesOfPDFFiles)
        } else WorkdayPageFragment(lessonsByWeekday[position]!!, ctx, weekdaysStr[position],
                position == currentDayOfWeek, headersOfPDFFiles, namesOfPDFFiles)

        // for other days - Monday, Tuesday e.t.c
    }

    override fun getCount(): Int {
        return ctx.resources.getInteger(R.integer.num_pages)
    }

    // return filename in format "Расписаниезанятийнасубботу05сентября2020.pdf" to save file at the device
    private fun translateHeaderToFilename(header: String): String {
        return header.replace("\\s".toRegex(), "") + ".pdf"
    }

    // return title to select replacement of schedule lessons, which you will see at screen
    private fun translateHeaderToTitle(header: String): String {
        val prefixStr = "ПОКАЗАТЬ ЗАМЕНУ НА "
        val months = arrayOf("янв", "февр", "март", "апр", "ма", "июн", "июл", "авг", "сент", "окт", "нояб") // The list is using to find number of month
        var i = 0
        while (i < months.size) {
            val prefixDayOfWeek = months[i]
            if (header.toLowerCase().indexOf(prefixDayOfWeek) != -1) { // number of month is founded!
                break // break cycle
            }
            i++
        }

        // build date in format "06.09.2020"
        val dayOfMonth = header.replace("\\D+".toRegex(), "").toInt()
        val date = java.lang.String.format("%02d.%02d.%04d", dayOfMonth, i + 1, calendar.get(Calendar.YEAR))

        // return result in format "ПОКАЗАТЬ ЗАМЕНУ НА 06.09.2020"
        return prefixStr + date
    }

}