package data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import data.DataStoreManager.Companion.expectedPrecipitationDismissDateKey
import data.DataStoreManager.Companion.expectedPrecipitationDismissedKey
import data.DataStoreManager.Companion.todayForecastDismissDateKey
import data.DataStoreManager.Companion.todayForecastDismissedKey
import data.DataStoreManager.Companion.tomorrowForecastDismissDateKey
import data.DataStoreManager.Companion.tomorrowForecastDismissedKey

// TODO: add weather alerts when WeatherSpec v5 comes out
// https://codeberg.org/Freeyourgadget/Gadgetbridge/pulls/3273#issuecomment-1075497
enum class TargetMode {
    FORECAST_TODAY,
    FORECAST_TOMORROW,
    EXPECTED_PRECIPITATION
}

enum class ForecastTargetDataType {
    TEMPERATURE_HOURLY,         // hourly temperature + condition
    TEMPERATURE_DAILY,          // daily min and max temperature + condition
    AIR_QUALITY_DAILY,          // daily air quality + corresponding color
//    WIND_HOURLY,                // hourly only
//    HUMIDITY_HOURLY             // hourly only
}

class DataStoreManager {
    companion object {
        private const val DATASTORE_NAME = "genericweather_datastore"

        val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

        val weatherDataKey = stringPreferencesKey("weather_data")

        val temperatureUnitKey = stringPreferencesKey("temperature_unit")
        val launchPackageKey = stringPreferencesKey("launch_package")
        val iconPackPackageNameKey = stringPreferencesKey("icon_pack_package_name")

        val conditionTargetDataPointsKey = intPreferencesKey("condition_target_data_points")
        val conditionTargetStyleKey = stringPreferencesKey("condition_target_style")
        val conditionTargetCarouselContentKey = intPreferencesKey("condition_carousel_content")

        val conditionComplicationStyleKey = stringPreferencesKey("condition_complication_style")
        val conditionComplicationTrimToFitKey = booleanPreferencesKey("condition_complication_trim_to_fit")

        val sunTimesComplicationTrimToFitKey = booleanPreferencesKey("suntimes_complication_trim_to_fit")

        val airQualityComplicationShowThresholdKey = intPreferencesKey("air_quality_complication_show_threshold")
        val airQualityComplicationShowAlways = booleanPreferencesKey("air_quality_complication_show_always")

        val uvIndexComplicationShowThresholdKey = intPreferencesKey("uv_index_complication_show_threshold")
        val uvIndexComplicationShowAlways = booleanPreferencesKey("uv_index_complication_show_always")

        val todayForecastDismissedKey = booleanPreferencesKey("today_forecast_dismissed")
        val todayForecastDismissDateKey = stringPreferencesKey("today_forecast_dismiss_date")

        val tomorrowForecastDismissedKey = booleanPreferencesKey("tomorrow_forecast_dismissed")
        val tomorrowForecastDismissDateKey = stringPreferencesKey("tomorrow_forecast_dismiss_date")

        val expectedPrecipitationDismissedKey = booleanPreferencesKey("dismissed_rain")
        val expectedPrecipitationDismissDateKey = stringPreferencesKey("dismiss_date_rain")
    }
}

fun getDismissedKey(targetMode: TargetMode): Preferences.Key<Boolean> {
    return when (targetMode) {
        TargetMode.FORECAST_TODAY -> todayForecastDismissedKey
        TargetMode.FORECAST_TOMORROW -> tomorrowForecastDismissedKey
        TargetMode.EXPECTED_PRECIPITATION -> expectedPrecipitationDismissedKey
    }
}

fun getDismissDateKey(targetMode: TargetMode): Preferences.Key<String> {
    return when (targetMode) {
        TargetMode.FORECAST_TODAY -> todayForecastDismissDateKey
        TargetMode.FORECAST_TOMORROW -> tomorrowForecastDismissDateKey
        TargetMode.EXPECTED_PRECIPITATION -> expectedPrecipitationDismissDateKey
    }
}