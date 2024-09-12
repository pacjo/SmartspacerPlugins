package data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

class DataStoreManager {
    companion object {
        private const val DATASTORE_NAME = "livelygreeting_datastore"

        val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

        val hideTargetWithoutComplicationsKey = booleanPreferencesKey("target_hide_no_complications")
    }
}