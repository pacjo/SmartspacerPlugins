package targets

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kieronquinn.app.smartspacer.sdk.SmartspacerConstants
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceTarget
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import com.kieronquinn.app.smartspacer.sdk.utils.TargetTemplate
import data.BluetoothTargetDataStoreManager.Companion.DATASTORE_NAME
import data.BluetoothTargetDataStoreManager.Companion.dismissedMACsKey
import data.BluetoothTargetDataStoreManager.Companion.getBluetoothDevices
import data.BluetoothTargetDataStoreManager.Companion.removeBluetoothDevice
import nodomain.pacjo.smartspacer.plugin.BuildConfig
import nodomain.pacjo.smartspacer.plugin.R
import nodomain.pacjo.smartspacer.plugin.utils.get
import nodomain.pacjo.smartspacer.plugin.utils.packageHasPermission
import nodomain.pacjo.smartspacer.plugin.utils.save
import ui.activities.BluetoothTargetConfigurationActivity
import utils.iconMap

class BluetoothBatteryTarget: SmartspacerTargetProvider() {

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun getSmartspaceTargets(smartspacerId: String): List<SmartspaceTarget> {

        // show error if we're missing permissions
        val isPluginMissingPermissions = ActivityCompat.checkSelfPermission(
            provideContext(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                 Manifest.permission.BLUETOOTH_CONNECT
            else
                Manifest.permission.BLUETOOTH
        ) != PackageManager.PERMISSION_GRANTED

        val isSmartspacerMissingPermission = !provideContext().packageManager.packageHasPermission(
            SmartspacerConstants.SMARTSPACER_PACKAGE_NAME,
            when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> Manifest.permission.BLUETOOTH_CONNECT
                else -> Manifest.permission.BLUETOOTH
            }
        )

        if (isPluginMissingPermissions || isSmartspacerMissingPermission) {
            return listOf(TargetTemplate.Basic(
                id = "bluetooth_battery_target_$smartspacerId",
                componentName = ComponentName(provideContext(), BluetoothBatteryTarget::class.java),
                title = Text("Missing bluetooth permission"),
                subtitle = Text("Click here to open settings"),
                icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                    Icon.createWithResource(
                        provideContext(),
                        R.drawable.alert_circle
                    )
                ),
                onClick = TapAction(
                    intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                        Uri.fromParts("package",
                            when {
                                isPluginMissingPermissions -> BuildConfig.APPLICATION_ID
                                else -> SmartspacerConstants.SMARTSPACER_PACKAGE_NAME
                            },
                            null
                        )
                    )
                )
            ).create().apply {
                canBeDismissed = false
            })
        }

        val dismissedMACs = provideContext().dataStore.get(dismissedMACsKey) ?: emptySet()

        val devices = getBluetoothDevices(provideContext())

        return devices
            .filterNot { device ->
                dismissedMACs.contains(device.macAddress)
            }.map { device ->
            TargetTemplate.Basic(
                id = "bluetooth_battery_target_${smartspacerId}_${device.macAddress}",
                componentName = ComponentName(provideContext(), BluetoothBatteryTarget::class.java),
                title = Text(device.bluetoothName),
                subtitle = Text("${device.batteryLevel}%"),
                icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                    Icon.createWithResource(
                        provideContext(),
                        iconMap.getOrDefault(device.bluetoothClass, R.drawable.bluetooth)
                    )
                )
            ).create()
        }
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Bluetooth Battery",
            description = "Provides battery from bluetooth devices",
            icon = Icon.createWithResource(provideContext(), R.drawable.battery_unknown_bluetooth),
            configActivity = Intent(context, BluetoothTargetConfigurationActivity::class.java),
            broadcastProvider = "${BuildConfig.APPLICATION_ID}.broadcast.bluetooth_battery"
        )
    }

    override fun onDismiss(smartspacerId: String, targetId: String): Boolean {
        val deviceMAC = targetId.substring(targetId.lastIndexOf('_') + 1)

        removeBluetoothDevice(
            context = provideContext(),
            macAddress =  deviceMAC
        )

        val dismissedMACs = provideContext().dataStore.get(dismissedMACsKey) ?: emptySet()
        provideContext().dataStore.save(dismissedMACsKey, dismissedMACs.plus(deviceMAC))

        notifyChange(smartspacerId)

        return true
    }
}