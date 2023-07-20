package com.arvizu.openweather.common.util.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Simple extension to convert datetime int into a more user-friendly format
fun Int?.toFormattedDateString(): String {
    if (this == null || this < 0) return "N/A"

    val timestampInMillis = this.toLong() * 1000
    val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.US)

    return try {
        dateFormat.format(Date(timestampInMillis))
    } catch (e: Exception) {
        "N/A"
    }
}