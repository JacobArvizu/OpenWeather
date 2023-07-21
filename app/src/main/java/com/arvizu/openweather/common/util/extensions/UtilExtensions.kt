package com.arvizu.openweather.common.util.extensions

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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

fun Int?.toTimeOfTheDay(timezoneOffsetSeconds: Int): String {
    if (this == null || this < 0) return "N/A"

    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(this.toLong()),
        ZoneOffset.ofHours(timezoneOffsetSeconds / 3600)
    )
    return dateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
}

fun String?.capitalize(): String {
    if (this == null) return ""
    val words = this.trim().split(" ")
    return when (words.size) {
        2 -> "${words[0].replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} ${words[1]}"
        else -> this
    }
}