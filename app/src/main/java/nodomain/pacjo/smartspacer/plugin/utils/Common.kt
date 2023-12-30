package nodomain.pacjo.smartspacer.plugin.utils

import android.annotation.SuppressLint
import android.content.Context
import nodomain.pacjo.smartspacer.plugin.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat

fun isFirstRun(context: Context) {
    val file = File(context.filesDir, "data.json")

    // If file doesn't exist, so
    //   - it's the first run after installation / data reset
    //   - something went wrong, but we can blame that on the user
    if (!file.exists()) {
        val outputFile = File(context.filesDir, "data.json")
        val outputStream: OutputStream = FileOutputStream(outputFile)

        context.resources.openRawResource(R.raw.default_data).use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }
}

fun convertTimeTo(timeInMilliseconds: Long, shortStyle: Boolean = false): String {
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

@SuppressLint("SimpleDateFormat")
fun SimpleDateFormatWrapper(timeInMilliseconds: Long, shortStyle: Boolean = false): String {        // TODO: respect locale 12/24h
    // probably should add check for number of days
    return if (shortStyle) {
        when (SimpleDateFormat("H").format(timeInMilliseconds).toInt() > 0) {
            true -> SimpleDateFormat("H 'hrs' m 'm'").format(timeInMilliseconds)
            else -> SimpleDateFormat("m 'm'").format(timeInMilliseconds)
        }
    } else {
        when (SimpleDateFormat("H").format(timeInMilliseconds).toInt() > 0) {
            true -> SimpleDateFormat("H 'hours' m 'minutes'").format(timeInMilliseconds)
            else -> SimpleDateFormat("m 'minutes'").format(timeInMilliseconds)
        }
    }
}