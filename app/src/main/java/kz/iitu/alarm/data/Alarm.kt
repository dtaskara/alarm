package kz.iitu.alarm.data

import kz.iitu.alarm.util.Day
import java.util.*
import kotlin.collections.HashMap

class Alarm {
    val id = UUID.randomUUID().toString()

    var name: String = ""
    var enabled: Boolean = true
    var lastTime: String = ""

    var timeH: Int = 0
    var timeM: Int = 0

    var status = true;


    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Alarm) return false
        return id == other.id
    }
}