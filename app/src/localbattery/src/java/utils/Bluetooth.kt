package utils

import android.bluetooth.BluetoothClass
import android.os.Build
import androidx.annotation.RequiresApi
import nodomain.pacjo.smartspacer.plugin.R

// sources we can use:
//   - https://developer.android.com/reference/android/bluetooth/BluetoothClass.Device
//   - https://developer.android.com/reference/android/bluetooth/BluetoothClass.Device.Major
// and we use BluetoothClass.Device as it's more specific
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val iconMap: Map<Int, Int> = mapOf(
    BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER to R.drawable.video_image,
    BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO to R.drawable.car_connected,
    BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE to R.drawable.bluetooth,
    BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES to R.drawable.headset,
    BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO to R.drawable.speaker_bluetooth,
    BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER to R.drawable.speaker_bluetooth,
    BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE to R.drawable.microphone,
    BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO to R.drawable.headset,
    BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX to R.drawable.set_top_box,
    BluetoothClass.Device.AUDIO_VIDEO_UNCATEGORIZED to R.drawable.bluetooth,
    BluetoothClass.Device.AUDIO_VIDEO_VCR to R.drawable.bluetooth,
    BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CAMERA to R.drawable.video_image,
    BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CONFERENCING to R.drawable.video_image,
    BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER to R.drawable.television,
    BluetoothClass.Device.AUDIO_VIDEO_VIDEO_GAMING_TOY to R.drawable.controller,
    BluetoothClass.Device.AUDIO_VIDEO_VIDEO_MONITOR to R.drawable.monitor,
    BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET to R.drawable.headset,
    BluetoothClass.Device.COMPUTER_DESKTOP to R.drawable.desktop_tower_monitor,
    BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA to R.drawable.devices,
    BluetoothClass.Device.COMPUTER_LAPTOP to R.drawable.laptop,
    BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA to R.drawable.devices,
    BluetoothClass.Device.COMPUTER_SERVER to R.drawable.server,
    BluetoothClass.Device.COMPUTER_UNCATEGORIZED to R.drawable.bluetooth,
    BluetoothClass.Device.COMPUTER_WEARABLE to R.drawable.watch,
    BluetoothClass.Device.HEALTH_BLOOD_PRESSURE to R.drawable.medical_bag,
    BluetoothClass.Device.HEALTH_DATA_DISPLAY to R.drawable.medical_bag,
    BluetoothClass.Device.HEALTH_GLUCOSE to R.drawable.medical_bag,
    BluetoothClass.Device.HEALTH_PULSE_OXIMETER to R.drawable.medical_bag,
    BluetoothClass.Device.HEALTH_PULSE_RATE to R.drawable.medical_bag,
    BluetoothClass.Device.HEALTH_THERMOMETER to R.drawable.medical_bag,
    BluetoothClass.Device.HEALTH_UNCATEGORIZED to R.drawable.bluetooth,
    BluetoothClass.Device.HEALTH_WEIGHING to R.drawable.medical_bag,
    BluetoothClass.Device.PERIPHERAL_KEYBOARD to R.drawable.keyboard,
    BluetoothClass.Device.PERIPHERAL_KEYBOARD_POINTING to R.drawable.keyboard,
    BluetoothClass.Device.PERIPHERAL_NON_KEYBOARD_NON_POINTING to R.drawable.bluetooth,
    BluetoothClass.Device.PERIPHERAL_POINTING to R.drawable.mouse_bluetooth,
    BluetoothClass.Device.PHONE_CELLULAR to R.drawable.devices,
    BluetoothClass.Device.PHONE_CORDLESS to R.drawable.devices,
    BluetoothClass.Device.PHONE_ISDN to R.drawable.devices,
    BluetoothClass.Device.PHONE_MODEM_OR_GATEWAY to R.drawable.server,
    BluetoothClass.Device.PHONE_SMART to R.drawable.devices,
    BluetoothClass.Device.PHONE_UNCATEGORIZED to R.drawable.bluetooth,
    BluetoothClass.Device.TOY_CONTROLLER to R.drawable.controller,
    BluetoothClass.Device.TOY_DOLL_ACTION_FIGURE to R.drawable.teddy_bear,
    BluetoothClass.Device.TOY_GAME to R.drawable.controller,
    BluetoothClass.Device.TOY_ROBOT to R.drawable.robot,
    BluetoothClass.Device.TOY_UNCATEGORIZED to R.drawable.bluetooth,
    BluetoothClass.Device.TOY_VEHICLE to R.drawable.car_connected,
    BluetoothClass.Device.WEARABLE_GLASSES to R.drawable.safety_goggles,
    BluetoothClass.Device.WEARABLE_HELMET to R.drawable.safety_goggles,     // eeee.
    BluetoothClass.Device.WEARABLE_JACKET to R.drawable.tshirt_crew,        // eeee too.
    BluetoothClass.Device.WEARABLE_PAGER to R.drawable.devices,
    BluetoothClass.Device.WEARABLE_UNCATEGORIZED to R.drawable.bluetooth,
    BluetoothClass.Device.WEARABLE_WRIST_WATCH to R.drawable.watch,

    // custom
    1288 to R.drawable.controller //xbox controller
)