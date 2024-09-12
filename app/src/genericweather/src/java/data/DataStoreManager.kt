package data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

// TODO: add weather alerts when WeatherSpec v5 comes out
// https://codeberg.org/Freeyourgadget/Gadgetbridge/pulls/3273#issuecomment-1075497
enum class TargetMode {
    FORECAST_TODAY,
    FORECAST_TOMORROW,
    EXPECTED_PRECIPITATION
}

val Context.dataStore by preferencesDataStore(name = "genericweather_datastore")

object PreferencesKeys {
    val ICON_PACK_PACKAGE_NAME = stringPreferencesKey("icon_pack_package_name")

    val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
    val LAUNCH_PACKAGE = stringPreferencesKey("launch_package")

    val TODAY_FORECAST_DISMISSED = booleanPreferencesKey("today_forecast_dismissed")
    val TODAY_FORECAST_DISMISS_DATE = stringPreferencesKey("today_forecast_dismiss_date")

    val TOMORROW_FORECAST_DISMISSED = booleanPreferencesKey("tomorrow_forecast_dismissed")
    val TOMORROW_FORECAST_DISMISS_DATE = stringPreferencesKey("tomorrow_forecast_dismiss_date")

    val EXPECTED_PRECIPITATION_DISMISSED = booleanPreferencesKey("dismissed_rain")
    val EXPECTED_PRECIPITATION_DISMISS_DATE = stringPreferencesKey("dismiss_date_rain")
}

fun getDismissedKey(targetMode: TargetMode): Preferences.Key<Boolean> {
    return when (targetMode) {
        TargetMode.FORECAST_TODAY -> PreferencesKeys.TODAY_FORECAST_DISMISSED
        TargetMode.FORECAST_TOMORROW -> PreferencesKeys.TOMORROW_FORECAST_DISMISSED
        TargetMode.EXPECTED_PRECIPITATION -> PreferencesKeys.EXPECTED_PRECIPITATION_DISMISSED
    }
}

fun getDismissDateKey(targetMode: TargetMode): Preferences.Key<String> {
    return when (targetMode) {
        TargetMode.FORECAST_TODAY -> PreferencesKeys.TODAY_FORECAST_DISMISS_DATE
        TargetMode.FORECAST_TOMORROW -> PreferencesKeys.TOMORROW_FORECAST_DISMISS_DATE
        TargetMode.EXPECTED_PRECIPITATION -> PreferencesKeys.EXPECTED_PRECIPITATION_DISMISS_DATE
    }
}