package utils

import androidx.annotation.Keep

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
    val todayMaxTemp: Int,
    val todayMinTemp: Int,
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

object AirQualityThresholds {
    const val EXCELLENT = 20
    const val FAIR = 50
    const val POOR = 100
    const val UNHEALTHY = 150
    const val VERY_UNHEALTHY = 250
    const val DANGEROUS = Int.MAX_VALUE
}