package data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class DataStoreManager {
    companion object {
        const val DATASTORE_NAME = "sleepasandroid_settings"

        val broadcastEventKey = stringPreferencesKey("event")

        val simpleStyleKey = booleanPreferencesKey("simple_style")
        val showTimeToBedKey = booleanPreferencesKey("show_time_to_bed")
        val showAlarmDismissedKey = booleanPreferencesKey("show_alarm_dismissed")
    }
}