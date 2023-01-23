package ru.dikoresearch.blesimplecontrollerapp.ui.scanner

import android.bluetooth.BluetoothDevice

data class ScannedBluetoothDevice(
    val device: BluetoothDevice,
    val rssi: Int
)
