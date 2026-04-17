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

        val drawableName = when (conditionCode) {
            200, 201, 210, 211, 230, 231 -> "google_<theme>_isolated_thunderstorms"
            202, 212, 232 -> "google_<theme>_strong_thunderstorms"
            221 -> "google_<theme>_isolated_scattered_thunderstorms_<time>"
            300, 301, 302 -> "google_<theme>_drizzle"
            310, 311, 312, 313, 314, 321 -> "google_<theme>_showers_rain"
            500, 501 -> "google_<theme>_showers_rain"
            502, 503, 504, 522 -> "google_<theme>_heavy_rain"
            511 -> "google_<theme>_mixed_rain_sleet_hail"
            520, 521 -> "google_<theme>_showers_rain"
            531 -> "google_<theme>_scattered_showers_<time>"
            600, 601, 620, 621 -> "google_<theme>_showers_snow"
            602, 622 -> "google_<theme>_heavy_snow"
            611, 612, 613 -> "google_<theme>_sleet_hail"
            615, 616 -> "google_<theme>_mixed_rain_snow"
            701, 711, 721, 731, 741, 751, 761, 762 -> "google_<theme>_haze_fog_dust_smoke"
            771 -> "google_<theme>_windy_breezy"
            781 -> "google_<theme>_tornado"
            800 -> "google_<theme>_clear_<time>"
            801 -> "google_<theme>_mostly_clear_<time>"
            802 -> "google_<theme>_partly_cloudy_<time>"
            803 -> "google_<theme>_mostly_cloudy_<time>"
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
            if (conditionCode in setOf(200, 201, 210, 211, 221, 230, 231)) return WeatherStateIcon.ISOLATED_SCATTERED_TSTORMS_NIGHT
            else if (conditionCode == 531) return WeatherStateIcon.SCATTERED_SHOWERS_NIGHT
            else if (conditionCode == 801) return WeatherStateIcon.MOSTLY_CLEAR_NIGHT
            else if (conditionCode == 802) return WeatherStateIcon.PARTLY_CLOUDY_NIGHT
            else if (conditionCode == 803) return WeatherStateIcon.MOSTLY_CLOUDY_NIGHT
            else if (conditionCode == 800) return WeatherStateIcon.CLEAR_NIGHT
        }

        return when (conditionCode) {
            200, 201, 210, 211, 221, 230, 231 -> WeatherStateIcon.ISOLATED_SCATTERED_TSTORMS_DAY
            202, 212, 232 -> WeatherStateIcon.STRONG_TSTORMS
            300, 301, 302 -> WeatherStateIcon.DRIZZLE
            310, 311, 312, 313, 314, 321 -> WeatherStateIcon.SHOWERS_RAIN
            500, 501, 520, 521 -> WeatherStateIcon.SHOWERS_RAIN
            502, 503, 504, 522 -> WeatherStateIcon.HEAVY_RAIN
            511 -> WeatherStateIcon.MIXED_RAIN_HAIL_RAIN_SLEET
            531 -> WeatherStateIcon.SCATTERED_SHOWERS_DAY
            600, 601, 620, 621 -> WeatherStateIcon.SNOW_SHOWERS_SNOW
            602, 622 -> WeatherStateIcon.HEAVY_SNOW
            611, 612, 613 -> WeatherStateIcon.SLEET_HAIL
            615, 616 -> WeatherStateIcon.WINTRY_MIX_RAIN_SNOW
            701, 711, 721, 731, 741, 751, 761, 762 -> WeatherStateIcon.HAZE_FOG_DUST_SMOKE
            771 -> WeatherStateIcon.WINDY_BREEZY
            781 -> WeatherStateIcon.TORNADO
            800 -> WeatherStateIcon.SUNNY
            801 -> WeatherStateIcon.MOSTLY_SUNNY
            802 -> WeatherStateIcon.PARTLY_CLOUDY
//            803 -> WeatherStateIcon.MOSTLY_CLOUDY // TODO: should use DAY/NIGHT versions
            803, 804 -> WeatherStateIcon.CLOUDY

            else -> throw IllegalArgumentException("Unknown condition code: $conditionCode")
        }
    }
}
