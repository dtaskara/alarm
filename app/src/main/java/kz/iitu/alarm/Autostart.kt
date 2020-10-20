package kz.iitu.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kz.iitu.alarm.util.Constants

class Autostart: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val i = Intent(context, AlarmService::class.java).apply {
            action = Constants.ActionInit
        }
        context.startService(i)
    }
}