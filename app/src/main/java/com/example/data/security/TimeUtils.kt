package com.example.data.security

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    private val pktTimeZone = TimeZone.getTimeZone("GMT+5:00") // Pakistan Standard Time (PKT, UTC+5)

    fun getCurrentPktDate(): String {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US)
        sdf.timeZone = pktTimeZone
        return sdf.format(Date())
    }

    fun getCurrentPktTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.US)
        sdf.timeZone = pktTimeZone
        return "${sdf.format(Date())} PKT"
    }

    fun getCurrentPktFullDateTime(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
        sdf.timeZone = pktTimeZone
        return "${sdf.format(Date())} PKT"
    }

    fun formatCustomDate(date: Date): String {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US)
        sdf.timeZone = pktTimeZone
        return sdf.format(date)
    }
}
