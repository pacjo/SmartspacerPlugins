package utils.icons

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import com.kieronquinn.app.smartspacer.sdk.model.weather.WeatherData.WeatherStateIcon
import nodomain.pacjo.smartspacer.plugin.BuildConfig
import utils.Weather

object BuiltinIconProvider {
    private const val TAG = "BuiltinIconProvider"

    @SuppressLint("DiscouragedApi")
    fun getWeatherIcon(
        context: Context,
        data: Weather,
        type: Int,
        index: Int = 0
    ): Int {
        // type:
        // 0 - current
        // 1 - hourly
        // 2 - daily

        // by default return dark mode, day icon

        val conditionCode = when (type) {
            0 -> data.currentConditionCode
            1 -> data.hourly[index].conditionCode
            2 -> data.forecasts[index].conditionCode

            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        val timestamp = when (type) {
            1 -> data.hourly[index].timestamp

            else -> System.currentTimeMillis() / 1000
        }

        val time = when (timestamp) {
            in data.sunRise..data.sunSet -> "day"
            in data.forecasts[index].sunRise..data.forecasts[index].sunSet -> "day"

            else -> "night"
        }

        val theme = when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> "dark"

            else -> "light"
        }

        // this mapping is wrong and should be changed
        val drawableName = when (conditionCode) {
            200, 201, 202 -> "google_<theme>_isolated_scattered_thunderstorms_<time>"
            210, 211, 212, 221, 230, 231, 232 -> "google_<theme>_isolated_thunderstorms"
            300, 301, 302 -> "google_<theme>_scattered_showers_<time>"
            310, 311, 312, 313, 314 -> "google_<theme>_showers_rain"
            321 -> "google_<theme>_scattered_showers_<time>"
            500 -> "google_<theme>_cloudy_with_rain"
            501, 502 -> "google_<theme>_showers_rain"
            503, 504 -> "google_<theme>_heavy_rain"
            511, 520 -> "google_<theme>_showers_rain"
            521, 522, 531 -> "google_<theme>_scattered_showers_<time>"
            600, 601 -> "google_<theme>_scattered_snow_showers_<time>"
            602 -> "google_<theme>_heavy_snow"
            611, 612, 613 -> "google_<theme>_sleet_hail"
            615, 616, 620, 621 -> "google_<theme>_scattered_snow_showers_<time>"
            622 -> "google_<theme>_heavy_snow"
            701, 711, 721, 731, 741, 751, 761, 762, 771 -> "google_<theme>_haze_fog_dust_smoke"
            781 -> "google_<theme>_tornado"
            800 -> "google_<theme>_clear_<time>"
            801 -> "google_<theme>_mostly_clear_<time>"
            802, 803 -> "google_<theme>_partly_cloudy_<time>"
            804 -> "google_<theme>_cloudy"

            else -> throw IllegalArgumentException("Unknown condition code: $conditionCode")
        }.replace("<theme>", theme).replace("<time>", time)

        return context.resources.getIdentifier(drawableName, "drawable", BuildConfig.APPLICATION_ID)
    }

    // this mapping is wrong and should be changed
    fun getSmartspacerWeatherIcon(data: Weather, type: Int, index: Int = 0): WeatherStateIcon {
        // type:
        // 0 - current
        // 1 - hourly
        // 2 - daily

        val conditionCode = when (type) {
            0 -> data.currentConditionCode
            1 -> data.hourly[index].conditionCode
            2 -> data.forecasts[index].conditionCode
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        val timestamp = when (type) {
            1 -> data.hourly[index].timestamp
            else -> System.currentTimeMillis() / 1000
        }

        val time = when (timestamp) {
            in data.sunRise..data.sunSet -> "day"
            in data.forecasts[index].sunRise..data.forecasts[index].sunSet -> "day"
            else -> "night"
        }

        if (time == "day") {
            if (conditionCode in 801..802) return WeatherStateIcon.MOSTLY_CLEAR_NIGHT
            else if (conditionCode == 800) return WeatherStateIcon.CLEAR_NIGHT
        }

        return when (conditionCode) {
            200, 201, 202, 210, 211, 212, 221, 230, 231 -> WeatherStateIcon.STRONG_TSTORMS
            232 -> WeatherStateIcon.STRONG_TSTORMS
            300, 301 -> WeatherStateIcon.HAZE_FOG_DUST_SMOKE
            302 -> WeatherStateIcon.HEAVY_RAIN
            310 -> WeatherStateIcon.HAZE_FOG_DUST_SMOKE
            311, 312, 313, 314, 321 -> WeatherStateIcon.HEAVY_RAIN
            500, 501 -> WeatherStateIcon.SHOWERS_RAIN
            502, 503, 504, 511, 520, 521, 522, 531 -> WeatherStateIcon.HEAVY_RAIN
            600, 601, 602, 611, 612, 613 -> WeatherStateIcon.HEAVY_SNOW
            615, 616 -> WeatherStateIcon.BLOWING_SNOW
            620, 621, 622 -> WeatherStateIcon.HEAVY_SNOW
            701, 711, 721, 731, 741, 751, 761, 762, 771, 781 -> WeatherStateIcon.HAZE_FOG_DUST_SMOKE
            800 -> WeatherStateIcon.SUNNY
            801, 802 -> WeatherStateIcon.MOSTLY_SUNNY
            803 -> WeatherStateIcon.CLOUDY
            804 -> WeatherStateIcon.CLOUDY

            else -> throw IllegalArgumentException("Unknown condition code: $conditionCode")
        }
    }
}