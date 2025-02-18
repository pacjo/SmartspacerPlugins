package utils.icons

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Icon
import utils.AirQualityThresholds

object AirQuality {
    fun createAqiIcon(aqi: Int): Icon {
        return createCircleIcon(getAqiColor(aqi))
    }

    private fun createCircleIcon(color: Int): Icon {
        val size = 48
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        canvas.drawCircle(size / 2f, size / 2f, size / 4f, paint)

        return Icon.createWithBitmap(bitmap)
    }

    private fun getAqiColor(aqi: Int): Int {
        // colors are taken from EAQI from google maps API, because I liked them
        // https://developers.google.com/maps/documentation/air-quality/laqis

        return when {
            aqi <= AirQualityThresholds.EXCELLENT -> Color.parseColor("#50F0E6")
            aqi <= AirQualityThresholds.FAIR -> Color.parseColor("#50CCAA")
            aqi <= AirQualityThresholds.POOR -> Color.parseColor("#F0E641")
            aqi <= AirQualityThresholds.UNHEALTHY -> Color.parseColor("#FF5050")
            aqi <= AirQualityThresholds.VERY_UNHEALTHY -> Color.parseColor("#960032")
            else -> Color.parseColor("#7D2181")
        }
    }
}