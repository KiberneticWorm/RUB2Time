package ru.rubt.rubttimetable.fragment

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.rubt.rubttimetable.R

class CallScheduleFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ths: Activity? = activity
        val builder = AlertDialog.Builder(ths!!)
        val view: View = ths.layoutInflater.inflate(R.layout.fragment_call_schedule, null)
        builder.setView(view)
        builder.setPositiveButton("OK", null)
        return builder.create()
    }
}