package targets

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.packageHasPermission
import org.json.JSONObject
import utils.iconMap
import java.io.File

class BluetoothBatteryTarget: SmartspacerTargetProvider() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        // Show error if we're missing permissions
        val isPluginMissingPermissions = ActivityCompat.checkSelfPermission(
            provideContext(),
            (when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> Manifest.permission.BLUETOOTH_CONNECT
                else -> Manifest.permission.BLUETOOTH
            })
        ) != PackageManager.PERMISSION_GRANTED

        val isSmartspacerMissingPermission = !provideContext().packageManager.packageHasPermission(
            "com.kieronquinn.app.smartspacer",
            when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> Manifest.permission.BLUETOOTH_CONNECT
                else -> Manifest.permission.BLUETOOTH
            }
        )

        if (isPluginMissingPermissions || isSmartspacerMissingPermission) {
            return listOf(TargetTemplate.Basic(
                id = "example_$smartspacerId",
                componentName = ComponentName(provideContext(), LocalBatteryTarget::class.java),
                title = Text("Missing bluetooth permission"),
                subtitle = Text("Click here to open settings"),
                icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                    Icon.createWithResource(
                        provideContext(),
                        R.drawable.alert_circle
                    )
                ),
                onClick = TapAction(intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                    Uri.fromParts("package",
                        when {
                            isPluginMissingPermissions -> "nodomain.pacjo.smartspacer.plugin.localbattery"
                            else -> "com.kieronquinn.app.smartspacer"
                        },
                        null
                    )
                ))
            ).create().apply {
                canBeDismissed = false
            })
        } else {
            val file = File(context?.filesDir, "data.json")

            val jsonObject = JSONObject(file.readText())
            val dataArray = jsonObject.getJSONArray("bluetooth_data")

            val targetList = mutableListOf<SmartspaceTarget>()

            for (i in 0 until dataArray.length()) {
                val device = dataArray.getJSONObject(i)

                targetList.add(
                    TargetTemplate.Basic(
                        id = "example_$smartspacerId",
                        componentName = ComponentName(provideContext(), LocalBatteryTarget::class.java),
                        title = Text(device.getString("deviceName")),
                        subtitle = Text("${device.getString("deviceBattery")}%"),
                        icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                            Icon.createWithResource(
                                provideContext(),
                                iconMap.getOrDefault(device.getInt("deviceClass"), R.drawable.bluetooth)
                            )
                        )
                    ).create().apply {
                        canBeDismissed = false      // TODO: change
//                        canTakeTwoComplications = true
                    }
                )
            }

            return targetList
        }
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Bluetooth Battery",
            description = "Provides battery from bluetooth devices",
            icon = Icon.createWithResource(provideContext(), R.drawable.battery_unknown_bluetooth),
            broadcastProvider = "nodomain.pacjo.smartspacer.plugin.localbattery.broadcast.bluetooth_battery"
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        return false
    }

}