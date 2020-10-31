package ru.rubt.rubttimetable.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.rubt.rubttimetable.R

// shows only PDF files, day of week - Sunday
class FreePageFragment(ctx: Context, date: String,
                       isCurrentDayOfWeek: Boolean,
                       headersOfPDFFiles: Array<String>,
                       namesOfPDFFiles: Array<String?>) : PageFragment(ctx, date, isCurrentDayOfWeek, headersOfPDFFiles, namesOfPDFFiles) {

    override fun onCreateView(layoutInflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View {
        val root: View = layoutInflater.inflate(R.layout.fragment_offtime, parent, false)
        val currentDateTv = root.findViewById<TextView>(R.id.activity_timetable__current_date)
        currentDateTv.text = date

        enablePDFListView(root)

        return root
    }
}