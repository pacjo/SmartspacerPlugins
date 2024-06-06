package nodomain.pacjo.smartspacer.plugin.utils

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageInfo.REQUESTED_PERMISSION_GRANTED
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kieronquinn.app.smartspacer.sdk.model.CompatibilityState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import nodomain.pacjo.smartspacer.plugin.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import kotlin.random.Random

fun isFirstRun(context: Context) {
    val file = File(context.filesDir, "data.json")

    // If file doesn't exist, so
    //   - it's the first run after installation / data reset
    //   - something went wrong, but we can blame that on the user
    if (!file.exists()) {
        val outputStream: OutputStream = FileOutputStream(file)

        context.resources.openRawResource(R.raw.default_data).use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }
}

// TODO: maybe write it in a cleaner way?
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
                    // ... and that's all we need for now
                }
            }
        }
    }
}

fun getBoolFromDataStore(dataStore: DataStore<Preferences>, key: String): Boolean? {
    var result: Boolean?
    runBlocking {
        result = dataStore.data.first()[booleanPreferencesKey(key)]
    }
    return result
}

fun getIntFromDataStore(dataStore: DataStore<Preferences>, key: String): Int? {
    var result: Int?
    runBlocking {
        result = dataStore.data.first()[intPreferencesKey(key)]
    }
    return result
}

fun getLongFromDataStore(dataStore: DataStore<Preferences>, key: String): Long? {
    var result: Long?
    runBlocking {
        result = dataStore.data.first()[longPreferencesKey(key)]
    }
    return result
}

fun getStringFromDataStore(dataStore: DataStore<Preferences>, key: String): String? {
    var result: String?
    runBlocking {
        result = dataStore.data.first()[stringPreferencesKey(key)]
    }
    return result
}

@SuppressLint("QueryPermissionsNeeded")
fun getCompatibilityState(context: Context?, packageName: String, incompatibilityMessage: String): CompatibilityState {
    // https://stackoverflow.com/questions/6758841/how-can-i-find-if-a-particular-package-exists-on-my-android-device
    return if (context?.packageManager?.getInstalledApplications(0)?.find { info -> info.packageName == packageName } == null) {
        CompatibilityState.Incompatible(incompatibilityMessage)
    } else CompatibilityState.Compatible
}

fun getProvider(context: Context, packageName: String, providerClass: String): AppWidgetProviderInfo? {
    val manager = context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager

    return manager.installedProviders.firstOrNull {
        it.provider.packageName == packageName && it.provider.className == providerClass
    }
}

// https://github.com/KieronQuinn/Smartspacer/blob/main/app/src/main/java/com/kieronquinn/app/smartspacer/utils/extensions/Extensions+PackageManager.kt
fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo {
    return if (Build.VERSION.SDK_INT >= 33) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        getPackageInfo(packageName, flags)
    }
}

fun PackageManager.packageHasPermission(packageName: String, permission: String): Boolean {
    return try {
        val info = getPackageInfoCompat(packageName, PackageManager.GET_PERMISSIONS)
        val permissions = info.requestedPermissions.zip(info.requestedPermissionsFlags.toTypedArray())
        permissions.any { it.first == permission && it.second and REQUESTED_PERMISSION_GRANTED != 0 }
    } catch (e: PackageManager.NameNotFoundException){
        false
    }
}

fun convertTimeTo(timeInMilliseconds: Long, shortStyle: Boolean = false): String {
    val hours = timeInMilliseconds / 3600000
    val minutes = timeInMilliseconds % 3600 / 60

    val hoursWord = when (shortStyle) {
        true -> "hrs"
        else -> "hours"
    }

    val minutesWord = when (shortStyle) {
        true -> "m"
        else -> "minutes"
    }

    return if (hours > 0 && minutes > 0) {
        "$hours $hoursWord and $minutes $minutesWord"
    } else if (hours > 0) {
        "$hours $hoursWord"
    } else if (minutes > 0) {
        "$minutes $minutesWord"
    } else {
        "less than a minute"
    }
}

@SuppressLint("SimpleDateFormat")
fun SimpleDateFormatWrapper(timeInMilliseconds: Long, shortStyle: Boolean = false): String {        // TODO: respect locale 12/24h
    // probably should add check for number of days
    return if (shortStyle) {
        when (SimpleDateFormat("H").format(timeInMilliseconds).toInt() > 0) {
            true -> SimpleDateFormat("H 'hrs' m 'm'").format(timeInMilliseconds)
            else -> SimpleDateFormat("m 'm'").format(timeInMilliseconds)
        }
    } else {
        when (SimpleDateFormat("H").format(timeInMilliseconds).toInt() > 0) {
            true -> SimpleDateFormat("H 'hours' m 'minutes'").format(timeInMilliseconds)
            else -> SimpleDateFormat("m 'minutes'").format(timeInMilliseconds)
        }
    }
}

fun imageTargetAdjustDrawable(context: Context, drawableResId: Int): Icon {
    val vectorDrawable = ContextCompat.getDrawable(context, drawableResId)!!

    val canvasWidth = vectorDrawable.intrinsicHeight / 9 * 16
    val canvasHeight = vectorDrawable.intrinsicHeight

    val bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    canvas.save()
    // we should divide by 2 to be exact, but 4 looks better due to left padding
    canvas.translate((canvasWidth - vectorDrawable.intrinsicWidth)/4f, 0f)
    vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    vectorDrawable.draw(canvas)
    canvas.restore()

    return Icon.createWithBitmap(BitmapDrawable(context.resources, bitmap).bitmap)
}

fun getRandomFromList(elements: List<Any>): Any {
    val randomIndex = Random.nextInt(elements.size);

    return elements[randomIndex]
}

fun <T> List<T>.getRandom(): T {
    val randomIndex = Random.nextInt(size);
    return this[randomIndex]
}

fun <A, B> List<Pair<A, B>>.getRandomFromPairs(): Pair<A, B> {
    val randomIndex = Random.nextInt(size);
    return this[randomIndex]
}