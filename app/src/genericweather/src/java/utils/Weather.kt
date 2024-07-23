package utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.annotation.Keep
import com.kieronquinn.app.smartspacer.sdk.model.weather.WeatherData.WeatherStateIcon

// TODO: add missing data from weather json

/*
Removing @Keep breaks minification and proguard
This is an issue with GSON as mentioned here:
  - https://github.com/google/gson/issues/2379
  - https://issuetracker.google.com/issues/112386012
*/

@Keep
data class WeatherData(
    val timestamp: Long,
    val location: String,
    val currentTemp: Int,
    val currentConditionCode: Int,
    val currentCondition: String,
    val sunRise: Long,
    val sunSet: Long,
    val forecasts: List<Daily>,
    val hourly: List<Hourly>,
    val airQuality: AirQuality
)

@Keep
data class Daily(
    val minTemp: Int,
    val maxTemp: Int,
    val conditionCode: Int,
    val sunRise: Long,
    val sunSet: Long,
    val moonRise: Long,
    val moonSet: Long,
    val moonPhase: Int,
    val airQuality: AirQuality?
)
@Keep
data class Hourly(
    val timestamp: Long,
    val temp: Int,
    val conditionCode: Int,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Int
)
@Keep
data class AirQuality(
    val aqi: Int,
    val co: Double,
    val no2: Double,
    val o3: Double,
    val pm10: Double,
    val pm25: Double,
    val so2: Double,
    val coAqi: Int,
    val no2Aqi: Int,
    val o3Aqi: Int,
    val pm10Aqi: Int,
    val pm25Aqi: Int,
    val so2Aqi: Int
)

// this mapping is wrong and should be changed
fun weatherDataToSmartspacerToIcon(data: WeatherData, type: Int, index: Int = 0): WeatherStateIcon {
    // type:
    // 0 - current
    // 1 - hourly
    // 2 - daily

    val iconMap = mapOf(
        200 to WeatherStateIcon.STRONG_TSTORMS,
        201 to WeatherStateIcon.STRONG_TSTORMS,
        202 to WeatherStateIcon.STRONG_TSTORMS,
        210 to WeatherStateIcon.STRONG_TSTORMS,
        211 to WeatherStateIcon.STRONG_TSTORMS,
        212 to WeatherStateIcon.STRONG_TSTORMS,
        221 to WeatherStateIcon.STRONG_TSTORMS,
        230 to WeatherStateIcon.STRONG_TSTORMS,
        231 to WeatherStateIcon.STRONG_TSTORMS,

        232 to WeatherStateIcon.STRONG_TSTORMS,
        300 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,
        301 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,
        302 to WeatherStateIcon.HEAVY_RAIN,
        310 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,
        311 to WeatherStateIcon.HEAVY_RAIN,
        312 to WeatherStateIcon.HEAVY_RAIN,
        313 to WeatherStateIcon.HEAVY_RAIN,
        314 to WeatherStateIcon.HEAVY_RAIN,
        321 to WeatherStateIcon.HEAVY_RAIN,

        500 to WeatherStateIcon.SHOWERS_RAIN,
        501 to WeatherStateIcon.SHOWERS_RAIN,
        502 to WeatherStateIcon.HEAVY_RAIN,
        503 to WeatherStateIcon.HEAVY_RAIN,
        504 to WeatherStateIcon.HEAVY_RAIN,
        511 to WeatherStateIcon.HEAVY_SNOW,
        520 to WeatherStateIcon.HEAVY_RAIN,
        521 to WeatherStateIcon.HEAVY_RAIN,
        522 to WeatherStateIcon.HEAVY_RAIN,
        531 to WeatherStateIcon.HEAVY_RAIN,

        600 to WeatherStateIcon.HEAVY_SNOW,
        601 to WeatherStateIcon.HEAVY_SNOW,
        602 to WeatherStateIcon.HEAVY_SNOW,
        611 to WeatherStateIcon.HEAVY_SNOW,
        612 to WeatherStateIcon.HEAVY_SNOW,
        613 to WeatherStateIcon.HEAVY_SNOW,
        615 to WeatherStateIcon.BLOWING_SNOW,
        616 to WeatherStateIcon.BLOWING_SNOW,
        620 to WeatherStateIcon.HEAVY_SNOW,
        621 to WeatherStateIcon.HEAVY_SNOW,
        622 to WeatherStateIcon.HEAVY_SNOW,

        701 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,
        711 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,
        721 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,
        731 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,
        741 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,
        751 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,
        761 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,
        762 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,
        771 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,
        781 to WeatherStateIcon.HAZE_FOG_DUST_SMOKE,

        800 to WeatherStateIcon.SUNNY,
        801 to WeatherStateIcon.MOSTLY_SUNNY,
        802 to WeatherStateIcon.MOSTLY_SUNNY,
        803 to WeatherStateIcon.CLOUDY,
        804 to WeatherStateIcon.CLOUDY
    )

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

    return iconMap.getOrElse(conditionCode) {
        throw IllegalArgumentException("Unknown condition code: $conditionCode")
    }
}

// this mapping is wrong and should be changed
@SuppressLint("DiscouragedApi")
fun weatherDataToIcon(context: Context, data: WeatherData, type: Int, index: Int = 0): Int {
    // type:
    // 0 - current
    // 1 - hourly
    // 2 - daily

    // by default return dark mode, day icon
    val iconMap = mapOf(
        200 to "google_<theme>_isolated_scattered_thunderstorms_<time>",
        201 to "google_<theme>_isolated_scattered_thunderstorms_<time>",
        202 to "google_<theme>_isolated_scattered_thunderstorms_<time>",
        210 to "google_<theme>_isolated_thunderstorms",
        211 to "google_<theme>_isolated_thunderstorms",
        212 to "google_<theme>_isolated_thunderstorms",
        221 to "google_<theme>_isolated_thunderstorms",
        230 to "google_<theme>_isolated_thunderstorms",
        231 to "google_<theme>_isolated_thunderstorms",
        232 to "google_<theme>_isolated_thunderstorms",

        300 to "google_<theme>_scattered_showers_<time>",
        301 to "google_<theme>_scattered_showers_<time>",
        302 to "google_<theme>_scattered_showers_<time>",
        310 to "google_<theme>_showers_rain",
        311 to "google_<theme>_showers_rain",
        312 to "google_<theme>_showers_rain",
        313 to "google_<theme>_showers_rain",
        314 to "google_<theme>_showers_rain",
        321 to "google_<theme>_scattered_showers_<time>",

        500 to "google_<theme>_cloudy_with_rain",
        501 to "google_<theme>_showers_rain",
        502 to "google_<theme>_showers_rain",
        503 to "google_<theme>_heavy_rain",
        504 to "google_<theme>_heavy_rain",
        511 to "google_<theme>_showers_rain",
        520 to "google_<theme>_showers_rain",
        521 to "google_<theme>_scattered_showers_<time>",
        522 to "google_<theme>_scattered_showers_<time>",
        531 to "google_<theme>_scattered_showers_<time>",

        600 to "google_<theme>_scattered_snow_showers_<time>",
        601 to "google_<theme>_scattered_snow_showers_<time>",
        602 to "google_<theme>_heavy_snow",
        611 to "google_<theme>_sleet_hail",
        612 to "google_<theme>_sleet_hail",
        613 to "google_<theme>_sleet_hail",
        615 to "google_<theme>_scattered_snow_showers_<time>",
        616 to "google_<theme>_scattered_snow_showers_<time>",
        620 to "google_<theme>_scattered_snow_showers_<time>",
        621 to "google_<theme>_scattered_snow_showers_<time>",
        622 to "google_<theme>_heavy_snow",

        701 to "google_<theme>_haze_fog_dust_smoke",
        711 to "google_<theme>_haze_fog_dust_smoke",
        721 to "google_<theme>_haze_fog_dust_smoke",
        731 to "google_<theme>_haze_fog_dust_smoke",
        741 to "google_<theme>_haze_fog_dust_smoke",
        751 to "google_<theme>_haze_fog_dust_smoke",
        761 to "google_<theme>_haze_fog_dust_smoke",
        762 to "google_<theme>_haze_fog_dust_smoke",
        771 to "google_<theme>_haze_fog_dust_smoke",
        781 to "google_<theme>_tornado",

        800 to "google_<theme>_clear_<time>",
        801 to "google_<theme>_mostly_clear_<time>",
        802 to "google_<theme>_partly_cloudy_<time>",
        803 to "google_<theme>_partly_cloudy_<time>",
        804 to "google_<theme>_cloudy"
    )

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

    val drawableName = iconMap.getOrElse(conditionCode) {
        throw IllegalArgumentException("Unknown condition code: $conditionCode")
    }.replace("<theme>", theme).replace("<time>", time)

    return context.resources.getIdentifier(drawableName, "drawable", "nodomain.pacjo.smartspacer.plugin.genericweather")
}

fun temperatureUnitConverter(value: Int, preference: String): String {
    return when (preference) {
        "K" -> "$value K"
        "C" -> "${value - 273}°C"
        "F" -> "${(((value - 273) * 1.8) + 32).toInt()}°F"
        else -> {
            throw IllegalArgumentException("Unknown preference: $preference")
        }
    }
}