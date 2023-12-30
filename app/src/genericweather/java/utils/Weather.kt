package utils

import nodomain.pacjo.smartspacer.plugin.R

// TODO: add missing data from weather json

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

data class Hourly(
    val timestamp: Long,
    val temp: Int,
    val conditionCode: Int,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Int
)

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

fun weatherDataToIcon(data: WeatherData, type: Int, index: Int = 0): Int {
    // type:
    // 0 - current
    // 1 - hourly
    // 2 - daily

    val iconMap = mapOf(
        200 to R.drawable.thunderstorm,
        201 to R.drawable.thunderstorm,
        202 to R.drawable.thunderstorm,
        210 to R.drawable.thunder,
        211 to R.drawable.thunderstorm,
        212 to R.drawable.thunderstorm,
        221 to R.drawable.thunderstorm,
        230 to R.drawable.thunder,
        231 to R.drawable.thunder,
        232 to R.drawable.thunderstorm,
        300 to R.drawable.haze,
        301 to R.drawable.haze,
        302 to R.drawable.rain,
        310 to R.drawable.haze,
        311 to R.drawable.rain,
        312 to R.drawable.rain,
        313 to R.drawable.rain,
        314 to R.drawable.rain,
        321 to R.drawable.rain,
        500 to R.drawable.rain,
        501 to R.drawable.rain,
        502 to R.drawable.rain,
        503 to R.drawable.rain,
        504 to R.drawable.rain,
        511 to R.drawable.snow,
        520 to R.drawable.rain,
        521 to R.drawable.rain,
        522 to R.drawable.rain,
        531 to R.drawable.rain,
        600 to R.drawable.snow,
        601 to R.drawable.snow,
        602 to R.drawable.snow,
        611 to R.drawable.snow,
        612 to R.drawable.snow,
        613 to R.drawable.snow,
        615 to R.drawable.sleet,
        616 to R.drawable.sleet,
        620 to R.drawable.snow,
        621 to R.drawable.snow,
        622 to R.drawable.snow,
        701 to R.drawable.fog,
        711 to R.drawable.fog,
        721 to R.drawable.fog,
        731 to R.drawable.fog,
        741 to R.drawable.fog,
        751 to R.drawable.fog,
        761 to R.drawable.fog,
        762 to R.drawable.fog,
        771 to R.drawable.fog,
        781 to R.drawable.fog,
        800 to R.drawable.clear_day,
        801 to R.drawable.partly_cloudy_day,
        802 to R.drawable.partly_cloudy_day,
        803 to R.drawable.cloudy,
        804 to R.drawable.cloudy
    )

    val conditionCode = when (type) {
        0 -> data.currentConditionCode
        1 -> data.hourly[index].conditionCode
        2 -> data.forecasts[index].conditionCode
        else -> throw IllegalArgumentException("Unknown type: $type")
    }

    if (System.currentTimeMillis() > data.sunSet * 1000L && System.currentTimeMillis() < data.forecasts[0].sunRise * 1000L) {
        if (conditionCode == 800) return R.drawable.clear_night
        else if (conditionCode in 801..802) return R.drawable.partly_cloudy_night
    }

    return iconMap.getOrElse(conditionCode) {
        throw IllegalArgumentException("Unknown condition code: $conditionCode")
    }
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