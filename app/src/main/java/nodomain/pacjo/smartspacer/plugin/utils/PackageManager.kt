package nodomain.pacjo.smartspacer.plugin.utils

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.kieronquinn.app.smartspacer.sdk.model.CompatibilityState
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction

/**
 * Checks availability of a selected package and returns Smartspacer compatible [CompatibilityState]
 */
@SuppressLint("QueryPermissionsNeeded")
fun getCompatibilityState(context: Context?, packageName: String, incompatibilityMessage: String): CompatibilityState {
    // https://stackoverflow.com/questions/6758841/how-can-i-find-if-a-particular-package-exists-on-my-android-device
    return if (context?.packageManager?.getInstalledApplications(0)?.find { info -> info.packageName == packageName } == null) {
        CompatibilityState.Incompatible(incompatibilityMessage)
    } else CompatibilityState.Compatible
}

/**
 * Returns [AppWidgetProviderInfo] given [packageName] and [className] of that provider
 */
fun getProvider(context: Context, packageName: String, className: String): AppWidgetProviderInfo? {
    val appWidgetManager = AppWidgetManager.getInstance(context)

    return appWidgetManager.installedProviders.firstOrNull {
        it.provider.packageName == packageName && it.provider.className == className
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
    val info = getPackageInfoCompat(packageName, PackageManager.GET_PERMISSIONS)
    val permissions = info.requestedPermissionsFlags?.let { info.requestedPermissions?.zip(it.toTypedArray()) }

    return permissions?.any { it.first == permission && it.second and PackageInfo.REQUESTED_PERMISSION_GRANTED != 0 } == true
}

fun getPackageLaunchTapAction(context: Context, launchPackage: String): TapAction? {
    val launchIntent = context.packageManager.getLaunchIntentForPackage(launchPackage)

    return if (launchIntent != null) {
        TapAction(intent = Intent(context.packageManager.getLaunchIntentForPackage(launchPackage)))
    } else {
        null
    }
}