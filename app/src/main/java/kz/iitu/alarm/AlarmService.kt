package kz.iitu.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import kz.iitu.alarm.data.Alarm
import kz.iitu.alarm.util.Constants
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AlarmService : Service(), SensorEventListener {

    private lateinit var preferences: SharedPreferences

    val timer = Timer("AlarmClock")
    val alarms = mutableListOf<Alarm>()

    var current: Alarm? = null
    var mp: MediaPlayer? = null
    val handler = Handler()
    var sm: SensorManager? = null

    override fun onBind(intent: Intent): IBinder {
        preferences = applicationContext.getSharedPreferences(Constants.PreferencesAlarms, Context.MODE_PRIVATE)
        when (intent.action) {
            Constants.ActionInit -> {
                val alarmList = preferences.getStringSet(Constants.AlarmList, emptySet())
                alarms += alarmList.mapNotNull { gson.fromJson(preferences.getString(it, ""), Alarm::class.java) }

                var c = Calendar.getInstance()
                val delay = 60 - c.get(Calendar.SECOND)

                timer.scheduleAtFixedRate(object: TimerTask() {
                    override fun run() {
                        c = Calendar.getInstance()
                        val h = c.get(Calendar.HOUR_OF_DAY)
                        val m = c.get(Calendar.MINUTE)
                        val d = c.get(Calendar.DAY_OF_WEEK)

                    }
                }, delay * 1000L, 1000)
            }
        }

        return AlarmBinder(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        sm?.unregisterListener(this)
    }

    fun startAlarm(id: String) {
        mp = MediaPlayer().also {
            it.isLooping = true
        }
        val alarm = gson.fromJson(preferences.getString(id, ""), Alarm::class.java)

        try {
            mp?.prepare()
            mp?.start()
        } catch (e: IOException) {
            mp = null
        }



        val timestamp = SimpleDateFormat("mm-hh-dd-MM-yyyy", Locale.US)
        alarm.lastTime = timestamp.format(Date())


        val alarmData = AlarmClock.gson.toJson(alarm)
        preferences.edit().also {
            it.putString(id, alarmData)
        }.apply()
        alarms.removeAll { it.id == id }
        alarms += alarm

    }

    class AlarmBinder(val service: AlarmService) : Binder() {
        var snoozeListener: SnoozeListener? = null

        fun refreshAlarms() {
            service.alarms.clear()
            val alarmList = service.preferences.getStringSet(Constants.AlarmList, emptySet())
            service.alarms += alarmList.mapNotNull { gson.fromJson(service.preferences.getString(it, ""), Alarm::class.java) }
        }



    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val diff = Math.sqrt((x*x + y*y + z*z).toDouble())
            if (diff > 0.5) {
                AlarmClock.instance.doWithService {
                }
            }
        }
    }

    interface SnoozeListener {
        fun onSnooze()
    }

    companion object {
        val gson = Gson()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}