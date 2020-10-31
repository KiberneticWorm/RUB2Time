package ru.rubt.rubttimetable.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.artifex.mupdf.viewer.DocumentActivity
import ru.rubt.rubttimetable.R
import java.io.File

// superclass for another fragments
// list_view is used to view PDF files
open class PageFragment(private val ctx: Context, val date: String,
                   private val isCurrentDayOfWeek: Boolean,
                   private val headersOfPDFFiles: Array<String>,
                   private val namesOfPDFFiles: Array<String?>) : Fragment() {

    private lateinit var intents: Array<Intent?>

    // filling list
    fun enablePDFListView(root: View) {
        if (isCurrentDayOfWeek) {
            val simpleAdapter = ArrayAdapter<String>(ctx, R.layout.layout_change_list_item, headersOfPDFFiles)
            val lvChanges = root.findViewById<ListView>(R.id.list_changes)
            lvChanges.adapter = simpleAdapter
            lvChanges.visibility = View.VISIBLE
            lvChanges.onItemClickListener = OnItemClickListener { _, _, position, _ -> ctx.startActivity(intents[position]) }
        }
    }

    // return array of intents, in the future you will launch intents to view PDF files
    private fun setIntentsForPDFView(namesOfPDFFiles: Array<String?>): Array<Intent?> {
        // Usually we have a PDF file, rarely several
        val intents = arrayOfNulls<Intent>(namesOfPDFFiles.size)
        for ((numberOfIntent, nameOfPDFFile) in namesOfPDFFiles.withIndex()) {
            val intentForPDFView = Intent(ctx, DocumentActivity::class.java)
            intentForPDFView.action = Intent.ACTION_VIEW
            intentForPDFView.data = Uri.fromFile(File(ctx.filesDir, nameOfPDFFile))
            intents[numberOfIntent] = intentForPDFView
        }
        return intents
    }

    init {
        // show PDF files today
        if (isCurrentDayOfWeek) {
            intents = setIntentsForPDFView(namesOfPDFFiles)
        }
    }
}