package ru.dikoresearch.blesimplecontrollerapp.data

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.MutableStateFlow
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.ui.scanner.DiscoveredBluetoothDevice
import no.nordicsemi.ui.scanner.toDiscoveredBluetoothDevice
import ru.dikoresearch.blesimplecontrollerapp.ui.scanner.ScannedBluetoothDevice
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevicesDataStore @Inject constructor(
    //private val bleAdapter: BluetoothAdapter
) {
    val devices = mutableListOf<ScannedBluetoothDevice>()
    val data = MutableStateFlow(devices.toList())

    fun addNewDevice(scanResult: ScanResult) {
        devices.firstOrNull { it.device.address == scanResult.device.address }?.let {
            val i = devices.indexOf(it)
            val l = devices[i].copy(rssi = scanResult.rssi)
            devices.set(i, l)
        } ?: devices.add(ScannedBluetoothDevice(device = scanResult.device, rssi = scanResult.rssi))

        data.value = devices.toList()
    }

    fun clear() {
        devices.clear()
        data.value = devices
    }
}