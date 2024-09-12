package data

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

class DataStoreManager {
    companion object {
        private const val DATASTORE_NAME = "anki_datastore"

        val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

        val complicationTemplateKey = stringPreferencesKey("complication_template")

        val widgetDueKey = stringPreferencesKey("widget_due")
        val widgetEtaKey = stringPreferencesKey("widget_eta")


    }
}