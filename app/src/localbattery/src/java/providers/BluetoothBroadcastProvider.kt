package providers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerBroadcastProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import nodomain.pacjo.smartspacer.plugin.utils.isFirstRun
import org.json.JSONArray
import org.json.JSONObject
import targets.BluetoothBatteryTarget
import java.io.File

fun removeDeviceFromArray(deviceArray: JSONArray, deviceAddress: String): JSONArray {
    for (i in 0 until deviceArray.length()) {
        Log.i("pacjodebug", "removefun: checking $i from $deviceArray, with len: ${deviceArray.length()}")
        if (deviceAddress == deviceArray.getJSONObject(i).getString("deviceAddress")) {
//            Log.i("pacjodebug", "removing: $i from $deviceArray of length: ${deviceArray.length()}")
            deviceArray.remove(i)
            break
        }
    }

    return deviceArray
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

        Log.i("pacjodebug", "${intent.action} for ${device?.address}, array: $dataArray")

        if (device != null) {
            // Save battery level
            if (intent.extras?.getInt("android.bluetooth.device.extra.BATTERY_LEVEL") != -1) {
                for (i in 0 until dataArray.length()) {
                    if (device.address == dataArray.getJSONObject(i).getString("deviceAddress")) {

                        val newDevice = JSONObject()
                        newDevice.put("deviceAddress", device.address)
                        newDevice.put("deviceClass", device.bluetoothClass)
                        newDevice.put("deviceName", device.name)
                        newDevice.put("deviceBattery", intent.extras?.getInt("android.bluetooth.device.extra.BATTERY_LEVEL"))
                        dataArray.put(dataArray.length(), newDevice)

                        break
                    }
                }

                // Do the same if we don't already have this device saved
                val newDevice = JSONObject()
                newDevice.put("deviceAddress", device.address)
                newDevice.put("deviceClass", device.bluetoothClass)
                newDevice.put("deviceName", device.name)
                newDevice.put("deviceBattery", intent.extras?.getInt("android.bluetooth.device.extra.BATTERY_LEVEL"))
                dataArray.put(dataArray.length(), newDevice)
            } else if (dataArray.length() != 0) {
                dataArray = removeDeviceFromArray(dataArray, device.address)
            }
        }

        jsonObject.put("bluetooth_data", dataArray)
        file.writeText(jsonObject.toString())
        Log.i("pacjodebug", "saved: $dataArray")

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