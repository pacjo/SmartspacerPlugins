package data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import nodomain.pacjo.smartspacer.plugin.localbattery.data.BluetoothProto
import nodomain.pacjo.smartspacer.plugin.localbattery.data.BluetoothProto.BluetoothDeviceStore
import providers.BluetoothDevice
import java.io.InputStream
import java.io.OutputStream

class ChargingComplicationDataStoreManager {
    companion object {
        const val DATASTORE_NAME = "charging_complication_settings"

        val disableTrimmingKey = booleanPreferencesKey("disable_trimming")
    }
}

class StatusTargetDataStoreManager {
    companion object {
        const val DATASTORE_NAME = "status_target_settings"

        val showEstimateKey = booleanPreferencesKey("show_estimate")
    }
}

class BluetoothTargetDataStoreManager {
    companion object {
        // this actually has two uses
        // one for storing bluetooth devices (in proto datastore)
        // and one for storing dismissed MACs (in preferences datastore)
        const val DATASTORE_NAME = "bluetooth_target_settings"

        val dismissedMACsKey = stringSetPreferencesKey("show_estimate")

        private val Context.bluetoothDevicesDataStore: DataStore<BluetoothDeviceStore> by dataStore(
            fileName = "$DATASTORE_NAME.pb",
            serializer = BluetoothDevicesSerializer
        )

        fun saveBluetoothDevice(context: Context, macAddress: String, device: BluetoothDevice) {
            runBlocking {
                context.bluetoothDevicesDataStore.updateData { currentData ->
                    val updatedDataMap = currentData.toBuilder()
                        .putBluetoothDevices(macAddress, device.toProto())
                        .build()

                    updatedDataMap
                }
            }
        }

        fun getBluetoothDevices(context: Context): List<BluetoothDevice> {
            return runBlocking {
                val dataStore = context.bluetoothDevicesDataStore.data.first()
                val protoBluetoothDevices = dataStore.bluetoothDevicesMap.values

                protoBluetoothDevices.map { protoDevice ->
                    protoDevice.toBluetoothDevice()
                }
            }
        }

        fun removeBluetoothDevice(context: Context, macAddress: String) {
            runBlocking {
                context.bluetoothDevicesDataStore.updateData { currentData ->
                    val updatedDataMap = currentData.toBuilder()
                        .removeBluetoothDevices(macAddress)
                        .build()

                    updatedDataMap
                }
            }
        }

        private fun BluetoothDevice.toProto(): BluetoothProto.BluetoothDevice {
            return BluetoothProto.BluetoothDevice.newBuilder()
                .setMacAddress(this.macAddress)
                .setBluetoothClass(this.bluetoothClass)
                .setBluetoothName(this.bluetoothName)
                .setBatteryLevel(this.batteryLevel)
                .setModifiedTime(this.modifiedTime)
                .build()
        }

        private fun BluetoothProto.BluetoothDevice.toBluetoothDevice(): BluetoothDevice {
            return BluetoothDevice(
                macAddress = macAddress,
                bluetoothClass = bluetoothClass,
                bluetoothName = bluetoothName,
                batteryLevel = batteryLevel,
                modifiedTime = modifiedTime
            )
        }
    }

    object BluetoothDevicesSerializer : Serializer<BluetoothDeviceStore> {
        override val defaultValue: BluetoothDeviceStore = BluetoothDeviceStore.getDefaultInstance()

        override suspend fun readFrom(input: InputStream): BluetoothDeviceStore {
            try {
                return BluetoothDeviceStore.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }

        override suspend fun writeTo(
            t: BluetoothDeviceStore,
            output: OutputStream
        ) = t.writeTo(output)
    }
}