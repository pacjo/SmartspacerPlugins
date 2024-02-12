package providers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerBroadcastProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONObject
import targets.BluetoothBatteryTarget
import utils.deduplicateJSONArray
import utils.removeDeviceFromArray
import java.io.File

class BluetoothBroadcastProvider: SmartspacerBroadcastProvider() {

    @SuppressLint("MissingPermission")      // we're checking this in the target code
    override fun onReceive(intent: Intent) {
        // https://stackoverflow.com/questions/53002816/how-to-get-bluetooth-headset-battery-level

        isFirstRun(provideContext())

        val file = File(context?.filesDir, "data.json")

        val jsonObject = JSONObject(file.readText())
        var dataArray = jsonObject.getJSONArray("bluetooth_data")

        // Get BluetoothDevice
        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        } else {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }

        if (device != null) {
            if (intent.extras?.getInt("android.bluetooth.device.extra.BATTERY_LEVEL") != -1) {
                val newDevice = JSONObject()
                newDevice.put("deviceAddress", device.address)
                newDevice.put("deviceClass", device.bluetoothClass.deviceClass)
                newDevice.put("deviceName", device.name)
                newDevice.put("deviceBattery", intent.extras?.getInt("android.bluetooth.device.extra.BATTERY_LEVEL"))
                newDevice.put("modifiedTime", System.currentTimeMillis())
                dataArray.put(dataArray.length(), newDevice)

            // Remove device, since we we get '-1' when device is disconnected
            } else if (dataArray.length() != 0) {
                dataArray = removeDeviceFromArray(dataArray, device.address)
            }

            // this is important as some devices (ehm Xbox controller, ehm) don't always report proper stats
            dataArray = deduplicateJSONArray(dataArray)
        }

        jsonObject.put("bluetooth_data", dataArray)
        file.writeText(jsonObject.toString())

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