package providers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerBroadcastProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONArray
import org.json.JSONObject
import targets.BluetoothBatteryTarget
import java.io.File

fun removeDeviceFromArray(deviceArray: JSONArray, deviceAddress: String): JSONArray {
    val newArray = JSONArray()

    // Remove ALL devices with the same address
    for (i in 0 until deviceArray.length()) {
        if (deviceAddress != deviceArray.getJSONObject(i).getString("deviceAddress")) {
            newArray.put(newArray.length(), deviceArray.getJSONObject(i))
        }
    }

    return newArray
}

fun deduplicateJSONArray(dataArray: JSONArray): JSONArray {
    val uniqueDevicesMap = mutableMapOf<String, JSONObject>()

    // Iterate over each JSONObject in the JSONArray
    for (i in 0 until dataArray.length()) {
        val jsonObject = dataArray.getJSONObject(i)
        val deviceAddress = jsonObject.getString("deviceAddress")
        val modifiedTime = jsonObject.getLong("modifiedTime")

        // Check if the deviceAddress is already present in the map
        if (uniqueDevicesMap.containsKey(deviceAddress)) {
            // If present, compare the modifiedTime
            val existingObject = uniqueDevicesMap[deviceAddress]!!
            val existingModifiedTime = existingObject.getLong("modifiedTime")

            // If the current object has a newer modifiedTime, replace the existing one
            if (modifiedTime > existingModifiedTime) {
                uniqueDevicesMap[deviceAddress] = jsonObject
            }
        } else {
            // If deviceAddress is not present, add it to the map
            uniqueDevicesMap[deviceAddress] = jsonObject
        }
    }

    // Convert the map back to a JSONArray
    val newArray = JSONArray()
    uniqueDevicesMap.values.forEach { newArray.put(it) }

    return newArray
}


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
                newDevice.put("deviceClass", device.bluetoothClass)
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