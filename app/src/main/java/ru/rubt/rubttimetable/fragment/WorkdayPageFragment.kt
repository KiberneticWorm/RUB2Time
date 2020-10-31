package ru.rubt.rubttimetable.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.rubt.rubttimetable.R
import ru.rubt.rubttimetable.adapters.LessonRecyclerAdapter
import ru.rubt.rubttimetable.db.Lesson

// shows schedule of lessons + PDF files
// days of week - Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
class WorkdayPageFragment(private val lessons: List<Lesson>,
                          private val ctx: Context,
                          date: String?,
                          private val isCurrentDayOfWeek: Boolean,
                          private val headersOfPDFFiles: Array<String>,
                          private val namesOfPDFFiles: Array<String?>) : PageFragment(ctx, date!!, isCurrentDayOfWeek, headersOfPDFFiles, namesOfPDFFiles) {

    override fun onCreateView(layoutInflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View {
        val root: View = layoutInflater.inflate(R.layout.fragment_list_lessons, parent, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.list_lessons)
        recyclerView.adapter = LessonRecyclerAdapter(lessons, date)
        recyclerView.layoutManager = LinearLayoutManager(ctx)
        enablePDFListView(root)
        return root
    }

}
