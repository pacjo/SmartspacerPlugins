package nodomain.pacjo.smartspacer.plugin.utils

import android.content.Context
import android.text.format.DateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class Time {
    private val eventInstant: Instant
    private val context: Context     

    private val locale = Locale.getDefault()
    private val zoneId = ZoneId.systemDefault()

    constructor(context: Context, instant: Instant) {
        this.context = context
        eventInstant = instant
    }

    constructor(context: Context, secondsTimestamp: Long) {
        this.context = context
        eventInstant = Instant.ofEpochSecond(secondsTimestamp)
    }
    
    fun getEventTime(format: String? = null): String {
        val formatter = DateTimeFormatter
            .ofPattern(
                when {
                    format != null -> format
                    DateFormat.is24HourFormat(context) -> "H:mm"
                    else -> "h:mm"
                }
            )
            .withLocale(locale)

        return LocalDateTime
            .ofInstant(eventInstant, zoneId)
            .format(formatter)
    }

    fun getEventDate(format: String? = null): String {
        val formatter = DateTimeFormatter
            .ofPattern(format ?: "d MMM y")
            .withLocale(locale)

        return LocalDateTime
            .ofInstant(eventInstant, zoneId)
            .format(formatter)
    }

    fun getTimeToEvent(): String {
        return getTimeToEvent(eventInstant)
    }

    companion object {
        fun getTimeToEvent(eventInstant: Instant): String {
            val duration = Duration.between(Instant.now(), eventInstant)
            val seconds = duration.seconds

            val formatedTime: String
            val suffix: String

            if (seconds < 60) {
                formatedTime = String.format(Locale.getDefault(), "%d", seconds)
                suffix = "s"
            } else if (seconds < 3600) {
                formatedTime = String.format(Locale.getDefault(), "%d:%02d", seconds / 60, seconds % 60)
                suffix = "min"
            } else {
                formatedTime = String.format(Locale.getDefault(), "%d:%02d", seconds / 3600, (seconds % 3600) / 60)
                suffix = "hrs"
            }

            return "$formatedTime $suffix"
        }

        fun getTimeToEvent(timeInMilliseconds: Long, shortStyle: Boolean = false): String {
            val hours = timeInMilliseconds / 3600000
            val minutes = timeInMilliseconds % 3600 / 60

            val hoursWord = when (shortStyle) {
                true -> "hrs"
                else -> "hours"
            }

            val minutesWord = when (shortStyle) {
                true -> "m"
                else -> "minutes"
            }

            return if (hours > 0 && minutes > 0) {
                "$hours $hoursWord and $minutes $minutesWord"
            } else if (hours > 0) {
                "$hours $hoursWord"
            } else if (minutes > 0) {
                "$minutes $minutesWord"
            } else {
                "less than a minute"
            }
        }

        fun getCurrentDate(): String {
            val dateFormat = DateTimeFormatter.ISO_LOCAL_DATE
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())

            return dateFormat.format(Instant.now())
        }
    }
}