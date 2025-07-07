package utils.icons

import android.content.Context
import android.graphics.drawable.Icon
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColorInt
import nodomain.pacjo.smartspacer.plugin.R
import utils.UvIndex

object UvIndex {
    fun createUvIndexIcon(context: Context, uvIndex: Float): Icon {
        return createSunIcon(context, getUvIndexColor(uvIndex))
    }

    private fun createSunIcon(context: Context, color: Int): Icon {
        // couldn't get it to tint otherwise,
        // might need a proper rewrite in the future
        val icon = ContextCompat.getDrawable(
            context,
            R.drawable.weather_sunny_variant
        )!!

        icon.setTint(color)

        return Icon.createWithBitmap(icon.toBitmap())
    }

    private fun getUvIndexColor(uvIndex: Float): Int {
        // colors from wikipedia
        // https://en.wikipedia.org/wiki/Ultraviolet_index#Index_usage

        return when {
            uvIndex <= UvIndex.LOW -> "#328624"
            uvIndex <= UvIndex.MODERATE -> "#999203"
            uvIndex <= UvIndex.HIGH -> "#C16F00"
            uvIndex <= UvIndex.VERY_HIGH -> "#B7280D"
            else -> "#7E3D6F"
        }.toColorInt()
    }
}