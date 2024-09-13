package data

class BluetoothDevice(
    val macAddress: String,
    val bluetoothClass: Int,
    val bluetoothName: String,
    val batteryLevel: Int,
    val modifiedTime: Long
)