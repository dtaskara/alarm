package kz.iitu.alarm.util

import android.content.Context
import android.text.format.DateFormat
import android.media.RingtoneManager
import android.net.Uri


object Util {
    val ringtones = mutableMapOf<Uri, String>()

    fun getDisplayTime(context: Context, h: Int, m: Int): String {
        val displayH: String
        val displayM = String.format("%02d", m)
        var suffix = ""
        if (!DateFormat.is24HourFormat(context)) {
            suffix = if (h >= 12) {
                displayH = (h - 12).toString()
                " PM"
            } else {
                displayH = h.toString()
                " AM"
            }
        } else {
            displayH = h.toString()
        }

        return "$displayH:$displayM$suffix"
    }

}