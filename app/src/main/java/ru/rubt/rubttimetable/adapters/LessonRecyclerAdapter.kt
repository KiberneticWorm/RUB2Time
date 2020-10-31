package ru.rubt.rubttimetable.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.rubt.rubttimetable.R
import ru.rubt.rubttimetable.db.Lesson

class LessonRecyclerAdapter internal constructor(
        private val lessons: List<Lesson>?,
        private val date: String) : RecyclerView.Adapter<ViewHolder>() {

    inner class HeaderHolder(itemView: View) : ViewHolder(itemView) {
        init {
            val currentTv = itemView.findViewById<TextView>(R.id.activity_timetable__current_date)
            currentTv.text = date
        }
    }

    class LessonViewHolder internal constructor(v: View) : ViewHolder(v) {

        var tvName: TextView = v.findViewById(R.id.tv_lesson_name)
        var tvTime: TextView = v.findViewById(R.id.tv_lesson_time)
        var tvCampus: TextView = v.findViewById(R.id.tv_lesson_campus)

        fun bind(lesson: Lesson) {
            tvName.text = lesson.name
            tvTime.text = lesson.time
            tvCampus.text = lesson.campus
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == HEADER_VIEW) {
            return HeaderHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.header_recycler_view, parent, false)
            )
        }
        return LessonViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item_lesson, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is LessonViewHolder) {
            holder.bind(lessons!![position - 1])
        }
    }

    override fun getItemCount(): Int {
        if (lessons == null) {
            return 0
        }
        return if (lessons.size == 0) {
            1
        } else lessons.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            HEADER_VIEW
        } else super.getItemViewType(position)
    }

    companion object {
        private const val HEADER_VIEW = 1
    }

}