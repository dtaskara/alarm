package kz.iitu.alarm.fragments

import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import kz.iitu.alarm.AlarmClock
import kz.iitu.alarm.R
import kz.iitu.alarm.activities.AlarmCreate
import kz.iitu.alarm.data.Alarm
import kz.iitu.alarm.util.Constants
import kz.iitu.alarm.util.Util

import kotlinx.android.synthetic.main.fragment_alarm_preview.*
import java.util.*

class FragmentAlarmPreview: Fragment() {

    private var preferences: SharedPreferences? = null
    private var alarmID: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_alarm_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = activity.applicationContext.getSharedPreferences(Constants.PreferencesAlarms, Context.MODE_PRIVATE)

        alarmID = arguments.getString(Constants.AlarmID, "")
        val alarm = AlarmClock.gson.fromJson<Alarm>(preferences?.getString(alarmID, ""), Alarm::class.java)

        labelName.text = alarm.name
        labelTime.text = Util.getDisplayTime(activity, alarm.timeH, alarm.timeM)
        alarm_status.isChecked = alarm.status


        view.setOnClickListener {
            showLeftTimeAlert(alarm)
        }
        view.setOnLongClickListener {
           showDeleteDialog();
            return@setOnLongClickListener true;
        }

    }
    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Do you want to delete?")

        builder.setPositiveButton("Yes") { dialog, which ->
            val alarmList = preferences?.getStringSet(Constants.AlarmList, mutableSetOf())
            preferences?.edit()?.also {
                it.remove(alarmID)
                it.putStringSet(Constants.AlarmList, alarmList?.filter{it != alarmID}?.toSet())
            }?.apply()
            view.visibility = View.GONE
        }

        builder.setNegativeButton("No") { dialog, which ->

        }

        builder.show()
    }
    private fun showLeftTimeAlert(alarm: Alarm) {

        val c = Calendar.getInstance()

        val currentHour = c.get(Calendar.HOUR_OF_DAY)
        val currentMinute = c.get(Calendar.MINUTE)

        var leftTime = (alarm.timeH * 60 +  alarm.timeM) - (currentHour * 60 + currentMinute)

        if(leftTime < 0) {
            leftTime += 1440
        }

        val leftHour = leftTime / 60
        val leftMinute = leftTime % 60

        val builder = AlertDialog.Builder(activity)
        if(leftHour == 0) {
            if(leftMinute == 0) {
                builder.setTitle("Alarm now")
            } else {
                builder.setTitle("$leftMinute minutes left")
            }
        } else {
            if(leftMinute == 0) {
                builder.setTitle("$leftHour hours left")
            } else {
                builder.setTitle("$leftHour hours $leftMinute minutes left")
            }
        }

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
        }

        builder.show()

    }


    companion object {
        fun create(alarmID: String): FragmentAlarmPreview {
            val result = FragmentAlarmPreview()
            val bundle = Bundle()
            bundle.putString(Constants.AlarmID, alarmID)
            result.arguments = bundle
            return result
        }
    }
}