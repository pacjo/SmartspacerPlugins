package nodomain.pacjo.smartspacer.plugin.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): T? {
    val dataStore = this

    return runBlocking {
        dataStore.data.first()[key]
    }
}

@Deprecated("Use generic DataStore<Preferences>.save instead")
fun saveToDataStore(dataStore: DataStore<Preferences>, key: String, value: Any? = null) {
    CoroutineScope(Dispatchers.IO).launch {
        dataStore.edit { settings ->
            if (value == null) {
                settings.remove(booleanPreferencesKey(key))
                settings.remove(intPreferencesKey(key))
                settings.remove(longPreferencesKey(key))
                settings.remove(stringPreferencesKey(key))
            } else {
                when (value) {
                    is Boolean -> settings[booleanPreferencesKey(key)] = value
                    is Int -> settings[intPreferencesKey(key)] = value
                    is Long -> settings[longPreferencesKey(key)] = value
                    is String -> settings[stringPreferencesKey(key)] = value.toString()
                    is Set<*> -> settings[stringSetPreferencesKey(key)] = value as Set<String>
                    // ... and that's all we need for now
                }
            }
        }
    }
}

@Deprecated("Use generic DataStore<Preferences>.get instead")
fun getBoolFromDataStore(dataStore: DataStore<Preferences>, key: String): Boolean? {
    var result: Boolean?
    runBlocking {
        result = dataStore.data.first()[booleanPreferencesKey(key)]
    }
    return result
}

@Deprecated("Use generic DataStore<Preferences>.get instead")
fun getIntFromDataStore(dataStore: DataStore<Preferences>, key: String): Int? {
    var result: Int?
    runBlocking {
        result = dataStore.data.first()[intPreferencesKey(key)]
    }
    return result
}

@Deprecated("Use generic DataStore<Preferences>.get instead")
fun getLongFromDataStore(dataStore: DataStore<Preferences>, key: String): Long? {
    var result: Long?
    runBlocking {
        result = dataStore.data.first()[longPreferencesKey(key)]
    }
    return result
}

@Deprecated("Use generic DataStore<Preferences>.get instead")
fun getStringFromDataStore(dataStore: DataStore<Preferences>, key: String): String? {
    var result: String?
    runBlocking {
        result = dataStore.data.first()[stringPreferencesKey(key)]
    }
    return result
}

@Deprecated("Use generic DataStore<Preferences>.get instead")
fun getStringSetFromDataStore(dataStore: DataStore<Preferences>, key: String): Set<String>? {
    var result: Set<String>?
    runBlocking {
        result = dataStore.data.first()[stringSetPreferencesKey(key)]
    }

    return result
}