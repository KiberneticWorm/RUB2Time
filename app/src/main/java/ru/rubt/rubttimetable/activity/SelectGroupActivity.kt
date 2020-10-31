package ru.rubt.rubttimetable.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener

import androidx.lifecycle.Observer
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

import ru.rubt.rubttimetable.*
import ru.rubt.rubttimetable.viewmodel.TimeViewModel

class SelectGroupActivity : AppCompatActivity() {

    private lateinit var timeViewModel: TimeViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_group)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        timeViewModel = (application as App).getTimeViewModelFactory().create()

        val group = timeViewModel.getSavedGroup()

        if (!TextUtils.equals(group, getString(R.string.saved_group_default))) {
            startViewActivity(group)
        }
        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        settingUInterfaceAndHandlers()
    }

    private fun startViewActivity(group: String?) {
        val intent = Intent(this@SelectGroupActivity, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_GROUP, group)
        startActivity(intent)
    }

    private fun settingUInterfaceAndHandlers() {
        val avGroupName = findViewById<MaterialAutoCompleteTextView>(R.id.av_group_name)
        val tilGroupName = findViewById<TextInputLayout>(R.id.til_group_name)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        avGroupName.threshold = 1

        timeViewModel.getGroups().observe(this, Observer {
            val groups = it.toTypedArray()
            avGroupName.setAdapter(ArrayAdapter(this, R.layout.layout_select_group, groups))

            avGroupName.addTextChangedListener {
                hideError(tilGroupName)
            }

            avGroupName.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->

                if (event.action == KeyEvent.ACTION_DOWN) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            click(tilGroupName, avGroupName, groups)
                            avGroupName.dismissDropDown()
                            hideKeyboard(this@SelectGroupActivity)
                            return@OnKeyListener true
                        }
                        else -> {
                        }
                    }
                }
                false
            })

            tilGroupName.setEndIconOnClickListener { click(tilGroupName, avGroupName, groups) }
            btnLogin.setOnClickListener { click(tilGroupName, avGroupName, groups) }
        })
    }
    private fun showError(tilGroupName: TextInputLayout, avGroupName: MaterialAutoCompleteTextView) {
        tilGroupName.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
        avGroupName.requestFocus()
    }

    private fun hideError(tilGroupName: TextInputLayout) {
        tilGroupName.boxStrokeColor = ContextCompat.getColor(this, R.color.colorPrimary)
    }

    private fun click(tilGroupName: TextInputLayout, avGroupName: MaterialAutoCompleteTextView, groups: Array<String>) {
        val group = avGroupName.text.toString().trim { it <= ' ' }
        if (isValueInArray(group, groups)) {
            startViewActivity(group)
        } else {
            showError(tilGroupName, avGroupName)
        }
    }

    private fun <T> isValueInArray(value: T, arr: Array<T>): Boolean {
        for (i in arr.indices) {
            if (arr[i] == value) {
                return true
            }
        }
        return false
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}