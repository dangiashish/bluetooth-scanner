package com.dangiashish.bluetoothscanner.utils

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import com.dangiashish.bluetoothscanner.R

object UIUtils {
    fun isDarkMode(context: Context): Boolean {
        return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    fun deviceIcon(it: BluetoothDevice): Int{
        Log.d("UIUtils", "deviceIcon: ${it.bluetoothClass.deviceClass}")
        return when (it.bluetoothClass?.deviceClass) {
            BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES -> R.drawable.baseline_headphones_24
            BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET -> R.drawable.baseline_headset_mic_24
            BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER -> R.drawable.baseline_speaker_24
            BluetoothClass.Device.COMPUTER_LAPTOP -> R.drawable.baseline_laptop_24
            BluetoothClass.Device.PHONE_SMART -> R.drawable.baseline_phone_android_24
            BluetoothClass.Device.WEARABLE_WRIST_WATCH -> R.drawable.baseline_watch_24
            else -> {
                when (it.bluetoothClass?.majorDeviceClass) {
                    BluetoothClass.Device.Major.IMAGING -> R.drawable.baseline_print_24
                    else -> R.drawable.baseline_bluetooth_24
                }
            }
        }
    }
}
