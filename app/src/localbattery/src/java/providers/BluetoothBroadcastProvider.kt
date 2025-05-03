package providers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerBroadcastProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import data.BluetoothTargetDataStoreManager.Companion.removeBluetoothDevice
import data.BluetoothTargetDataStoreManager.Companion.saveBluetoothDevice
import targets.BluetoothBatteryTarget

class BluetoothBroadcastProvider: SmartspacerBroadcastProvider() {

    @SuppressLint("MissingPermission")      // we're checking this in the target code
    override fun onReceive(intent: Intent) {
        // https://stackoverflow.com/questions/53002816/how-to-get-bluetooth-headset-battery-level

        // Get bluetooth device
        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }

        device?.let {
            val batteryLevel = intent.extras?.getInt("android.bluetooth.device.extra.BATTERY_LEVEL")

            if (batteryLevel != -1) {
                val bluetoothDevice = data.BluetoothDevice(
                    macAddress = device.address,
                    bluetoothClass = device.bluetoothClass.deviceClass,
                    bluetoothName = device.name,
                    batteryLevel = batteryLevel!!,
                    modifiedTime = System.currentTimeMillis()
                )

                saveBluetoothDevice(provideContext(), device.address, bluetoothDevice)
            } else {
                // Remove device, since we we get '-1' when device is disconnected
                removeBluetoothDevice(provideContext(), device.address)
            }
        }

        SmartspacerTargetProvider.notifyChange(provideContext(), BluetoothBatteryTarget::class.java)
    }

    override fun getConfig(smartspacerId: String): Config {
        return Config(
            intentFilters = listOf(
                IntentFilter("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED")
            )
        )
    }
}