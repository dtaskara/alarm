package kz.iitu.alarm.activities

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kz.iitu.alarm.AlarmClock
import kz.iitu.alarm.R
import kz.iitu.alarm.data.Alarm
import kz.iitu.alarm.fragments.FragmentTimePicker
import kz.iitu.alarm.util.Constants

import kotlinx.android.synthetic.main.activity_alarm_create.*
import java.util.*
import kz.iitu.alarm.util.Util

class AlarmCreate : AppCompatActivity() {

    lateinit var alarm: Alarm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_create)

        val preferences: SharedPreferences = applicationContext.getSharedPreferences(Constants.PreferencesAlarms, Context.MODE_PRIVATE)

        val args: Bundle? = intent.extras
        val id = args?.getString(Constants.AlarmID, "") ?: ""
        if (id.isNotBlank()) {
            alarm = AlarmClock.gson.fromJson<Alarm>(preferences.getString(id, ""), Alarm::class.java)

            textName.text.clear()
            textName.text.insert(0, alarm.name)

        } else {
            alarm = Alarm()

            val c = Calendar.getInstance()
            alarm.timeH = c.get(Calendar.HOUR_OF_DAY)
            alarm.timeM = c.get(Calendar.MINUTE)
        }
        labelTime.text = Util.getDisplayTime(this, alarm.timeH, alarm.timeM)

        textName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                alarm.name = s?.toString() ?: "Alarm"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        buttonSave.setOnClickListener {
            val alarmData = AlarmClock.gson.toJson(alarm)
            val set = preferences.getStringSet(Constants.AlarmList, mutableSetOf())
            preferences.edit().also {
                it.putString(alarm.id, alarmData)
                set.add(alarm.id)
                it.putStringSet(Constants.AlarmList, set)
            }.apply()

            AlarmClock.instance.doWithService {
                it.refreshAlarms()
            }

            finish()
        }
    }

    fun showTimePicker(view: View) {
        val frag = FragmentTimePicker()
        val bundle = Bundle()
        bundle.putInt(Constants.ARGUMENT_HOUR, alarm.timeH)
        bundle.putInt(Constants.ARGUMENT_MINUTE, alarm.timeM)
        frag.arguments = bundle
        frag.listener = TimePickerDialog.OnTimeSetListener { _, h, m ->
            alarm.timeH = h
            alarm.timeM = m
            labelTime.text = Util.getDisplayTime(this, h, m)
        }
        frag.show(supportFragmentManager, "timePicker")
    }


}
