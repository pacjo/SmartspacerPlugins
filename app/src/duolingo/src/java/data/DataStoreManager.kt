package data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

class DataStoreManager {
    companion object {
        private const val DATASTORE_NAME = "duolingo_datastore"

        val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

        val hideWhenLessonCompletedKey = booleanPreferencesKey("hide_when_completed")

        val widgetSubtitleKey = stringPreferencesKey("widget_subtitle")
    }
}