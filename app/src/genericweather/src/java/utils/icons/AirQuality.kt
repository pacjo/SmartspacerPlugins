package utils.icons

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Icon
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import utils.AirQuality

object AirQuality {
    fun createAqiIcon(aqi: Int): Icon {
        return createCircleIcon(getAqiColor(aqi))
    }

    private fun createCircleIcon(color: Int): Icon {
        val size = 48
        val bitmap = createBitmap(size, size)
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
            aqi <= AirQuality.EXCELLENT -> "#50F0E6"
            aqi <= AirQuality.FAIR -> "#50CCAA"
            aqi <= AirQuality.POOR -> "#F0E641"
            aqi <= AirQuality.UNHEALTHY -> "#FF5050"
            aqi <= AirQuality.VERY_UNHEALTHY -> "#960032"
            else -> "#7D2181"
        }.toColorInt()
    }
}