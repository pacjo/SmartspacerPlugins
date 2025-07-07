package utils

import androidx.annotation.Keep

/*
Removing @Keep breaks minification and proguard
This is an issue with GSON as mentioned here:
  - https://github.com/google/gson/issues/2379
  - https://issuetracker.google.com/issues/112386012
*/

@Keep
data class Weather(
    val timestamp: Long,
    val location: String,
    val currentTemp: Int,
    val currentConditionCode: Int,
    val currentCondition: String,
    val currentHumidity: Int,
    val todayMaxTemp: Int,
    val todayMinTemp: Int,
    val windSpeed: Float,
    val windDirection: Int,
    val uvIndex: Float,
    val precipProbability: Int,
    val dewPoint: Int,
    val pressure: Float,
    val cloudCover: Int,
    val visibility: Float,
    val sunRise: Long,
    val sunSet: Long,
    val moonRise: Long,
    val moonSet: Long,
    val moonPhase: Int,
    val feelsLikeTemp: Int,

    val forecasts: List<Daily>,
    val hourly: List<Hourly>,
    val airQuality: AirQuality
)

@Keep
data class Daily(
    val minTemp: Int,
    val maxTemp: Int,
    val conditionCode: Int,
    val windSpeed: Float,
    val windDirection: Int,
    val uvIndex: Float,
    val precipProbability: Int,
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
    val windSpeed: Float,
    val windDirection: Int,
    val uvIndex: Float,
    val precipProbability: Int
)

@Keep
data class AirQuality(
    val aqi: Int,
    val co: Float,
    val no2: Float,
    val o3: Float,
    val pm10: Float,
    val pm25: Float,
    val so2: Float,
    val coAqi: Int,
    val no2Aqi: Int,
    val o3Aqi: Int,
    val pm10Aqi: Int,
    val pm25Aqi: Int,
    val so2Aqi: Int
) {
    companion object {
        const val EXCELLENT = 20
        const val FAIR = 50
        const val POOR = 100
        const val UNHEALTHY = 150
        const val VERY_UNHEALTHY = 250
        const val DANGEROUS = Int.MAX_VALUE
    }
}

@Keep
data class UvIndex(
    val uvIndex: Float
) {
    companion object {
        // https://en.wikipedia.org/wiki/Ultraviolet_index#Index_usage
        const val LOW = 2
        const val MODERATE = 5
        const val HIGH = 7
        const val VERY_HIGH = 10
        const val EXTREME = Int.MAX_VALUE
    }
}