package kz.iitu.alarm.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kz.iitu.alarm.AlarmClock
import kz.iitu.alarm.R
import kz.iitu.alarm.data.Alarm
import kz.iitu.alarm.fragments.FragmentAlarmPreview
import kz.iitu.alarm.util.Constants

import kotlinx.android.synthetic.main.activity_alarm_preview.*

class AlarmPreview : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_preview)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val intent = Intent(this, AlarmCreate::class.java)
            startActivity(intent)
        }

        refreshAlarmList()
    }

    fun refreshAlarmList() {
        val preferences: SharedPreferences = applicationContext.getSharedPreferences(Constants.PreferencesAlarms, Context.MODE_PRIVATE)

        alarms.removeAllViewsInLayout()

        val fm = fragmentManager
        val ft = fm.beginTransaction()

        val alarmList = preferences.getStringSet(Constants.AlarmList, mutableSetOf())
        alarmList.forEach {
            val frag = FragmentAlarmPreview.create(it)
            ft.add(alarms.id, frag, "preview$it")
        }

        ft.commit()
    }

    override fun onResume() {
        super.onResume()
        refreshAlarmList()
    }




}
