package data

import androidx.datastore.preferences.core.booleanPreferencesKey

class ChargingComplicationDataStoreManager {
    companion object {
        const val DATASTORE_NAME = "charging_complication_settings"

        val disableTrimmingKey = booleanPreferencesKey("disable_trimming")
    }
}

class StatusTargetDataStoreManager {
    companion object {
        const val DATASTORE_NAME = "status_target_settings"

        val showEstimateKey = booleanPreferencesKey("show_estimate")
    }
}