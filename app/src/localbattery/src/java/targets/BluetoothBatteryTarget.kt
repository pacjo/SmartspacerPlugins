package targets

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.packageHasPermission
import org.json.JSONObject
import java.io.File

class BluetoothBatteryTarget: SmartspacerTargetProvider() {

    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {
        // Show error if we're missing permissions
        if ((ActivityCompat.checkSelfPermission(
                provideContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) || (!provideContext().packageManager.packageHasPermission("com.kieronquinn.app.smartspacer", Manifest.permission.BLUETOOTH_CONNECT))) {
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
                            (ActivityCompat.checkSelfPermission(
                                provideContext(),
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED) -> "nodomain.pacjo.smartspacer.plugin.localbattery"
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
                                R.drawable.headset
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
//            configActivity = Intent(context, ConfigurationActivity::class.java),
            broadcastProvider = "nodomain.pacjo.smartspacer.plugin.localbattery.broadcast.bluetooth_battery"
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        return false
    }

}