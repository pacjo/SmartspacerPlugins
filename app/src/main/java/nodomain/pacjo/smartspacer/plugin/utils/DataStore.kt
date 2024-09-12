package nodomain.pacjo.smartspacer.plugin.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Allows for saving (and removing) data, data type is
 * detected automatically based on the key provided.
 * @param key preferences key under which data should be saved (e.g. [stringPreferencesKey], [booleanPreferencesKey]...)
 * @param value data to save (pass `null` to remove data associated with provided key)
 */
fun <T> DataStore<Preferences>.save(key: Preferences.Key<T>, value: T? = null) {
    val dataStore = this

    runBlocking {
        dataStore.edit { settings ->
            if (value == null)
                settings.remove(key)
            else
                settings[key] = value
        }
    }
}

/**
 * Allows retriving data from preferences datastore.
 * **Blocking**.
 * @param key preferences key under which data should be saved (e.g. [stringPreferencesKey], [booleanPreferencesKey]...)
 */
fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): T? {
    val dataStore = this

    return runBlocking {
        dataStore.data.first()[key]
    }
}