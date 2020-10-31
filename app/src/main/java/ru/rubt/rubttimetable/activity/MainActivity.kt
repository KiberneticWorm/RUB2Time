package ru.rubt.rubttimetable.activity

import android.content.pm.ActivityInfo
import android.os.Bundle

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.rubt.rubttimetable.*
import ru.rubt.rubttimetable.adapters.WeekdayPagerAdapter
import ru.rubt.rubttimetable.fragment.CallScheduleFragment
import ru.rubt.rubttimetable.viewmodel.TimeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var timeViewModel: TimeViewModel

    public override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        timeViewModel = ((application as App)).getTimeViewModelFactory().create()

        val group = intent.getStringExtra(EXTRA_GROUP)

        initGUI(group)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("E, dd.MM.yy")

        val currentDayOfWeek = getCurrentDayOfWeek(calendar)
        val weekdaysStr = getWeekdayStr(calendar, dateFormat)
        val isOdds = getIsOdds(calendar)

        val navBetweenDays = findViewById<BottomNavigationView>(R.id.nav_between_days)
        settingBottomNavigationEventHandler(navBetweenDays, currentDayOfWeek) // Setting handlers  for following buttons: btnClock, btnPrev and btnNext

        timeViewModel.saveGroup(group!!)

        timeViewModel.getLessons(group, isOdds).observe(this, Observer {
            viewPager.adapter = WeekdayPagerAdapter(this@MainActivity, supportFragmentManager, it, timeViewModel.getPdfHeaders(), weekdaysStr, currentDayOfWeek, calendar)
            viewPager.currentItem = currentDayOfWeek
        })

    }

    private fun initGUI(group: String?) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = group

        viewPager = findViewById(R.id.vp_days)
    }

    private fun settingBottomNavigationEventHandler(navBetweenDays: BottomNavigationView, currentDayOfWeek: Int) {
        navBetweenDays.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_prev_day -> {
                    val currItem = viewPager.currentItem
                    if (currItem > 0) {
                        viewPager.currentItem = currItem - 1
                    }
                    true
                }
                R.id.item_today -> {
                    viewPager.currentItem = currentDayOfWeek
                    true
                }
                R.id.item_next_day -> {
                    val currItem = viewPager.currentItem
                    if (currItem < resources.getInteger(R.integer.num_pages) - 1) {
                        viewPager.currentItem = currItem + 1
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actions, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.call_schedule -> {
                CallScheduleFragment().show(supportFragmentManager, "")
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }

    private fun getCurrentDayOfWeek(calendar: Calendar) : Int {
        // defining current day of weekday, currentDayOfWeek - index from 0(Monday) to 6(Sunday)
        val day = calendar[Calendar.DAY_OF_WEEK]
        return if (day == Calendar.SUNDAY) {
            resources.getInteger(R.integer.num_pages) / 2 - 1
        } else {
            day - 2
        }
    }

    private fun getWeekdayStr(calendar: Calendar, dateFormat: SimpleDateFormat) : Array<String?> {
        calendar[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        val weekdaysStr = arrayOfNulls<String>(resources.getInteger(R.integer.num_pages)) // For example weekdaysStr[0] - Monday, 20.10.2019
        for (i in weekdaysStr.indices) {
            weekdaysStr[i] = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return weekdaysStr
    }

    private fun getIsOdds(calendar: Calendar) : Array<Boolean?> {
        calendar[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        val isOdds = arrayOfNulls<Boolean>(resources.getInteger(R.integer.num_pages)) // For example weekdaysStr[0] - Monday, 20.10.2019
        for (i in isOdds.indices) {
            val month = calendar[Calendar.MONTH] // index from 0 to 11
            isOdds[i] = (month + 1) % 2 == 1 // if month is odd, than isOdds[number of month] return true
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return isOdds

    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    companion object {
        const val EXTRA_GROUP = "group"
    }
}