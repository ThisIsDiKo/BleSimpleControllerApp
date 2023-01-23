package ru.dikoresearch.blesimplecontrollerapp.repository

import android.bluetooth.BluetoothDevice
import android.util.Log
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import ru.dikoresearch.blesimplecontrollerapp.data.DevicesDataStore
import ru.dikoresearch.blesimplecontrollerapp.ui.scanner.ScannedBluetoothDevice
import javax.inject.Inject

@ViewModelScoped
class DevicesRepository @Inject constructor(
    private val devicesDataStore: DevicesDataStore
) {

    fun getDevices(): Flow<List<ScannedBluetoothDevice>> =
        callbackFlow<DeviceResource<List<ScannedBluetoothDevice>>> {
            val scanCallback: ScanCallback = object : ScanCallback(){
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    //Log.e("Device repos", "Got device ${result.device.address} rssi = ${result.rssi}")
                    if (result.isConnectable){
                        devicesDataStore.addNewDevice(result)
                        //Log.e("Device repos", "Got connectable device ${result.device.address} rssi = ${result.rssi} total devices ${devicesDataStore.devices.size}")
                        val res = trySend(DeviceResource.createSuccess(devicesDataStore.devices))
//                        Log.e("Device repos", "Got connectable device ${result.device.address} rssi = ${result.rssi} total devices ${devicesDataStore.devices.size}" +
//                        "sensing result is ok ${res.isSuccess}")
                    }
                }

                override fun onBatchScanResults(results: MutableList<ScanResult>) {
                    // This callback will be called only if the report delay set above is greater then 0.
                    // If the packet has been obtained while Location was disabled, mark Location as not required
                    //Log.e("Device repos", "scan batched")
                    val newResults = results.filter { it.isConnectable }
                    newResults.forEach{
                        devicesDataStore.addNewDevice(it)
                    }
                    if (newResults.isNotEmpty()){
                        trySend(DeviceResource.createSuccess(devicesDataStore.devices))
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    //Log.e("Device repos", "Scan failer $errorCode")
                    trySend(DeviceResource.createError(errorCode))
                }
            }

            trySend(DeviceResource.createLoading())

            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setLegacy(false)
                //.setReportDelay(500)
                .setUseHardwareBatchingIfSupported(false)
                .build()
            val scanner = BluetoothLeScannerCompat.getScanner()

            Log.e("Device repos", "Starting scanning ${scanner}")

            scanner.startScan(null, settings, scanCallback)

            Log.e("Device repos", "Scan started")

            awaitClose {
                Log.e("Device repos", " stopping device search")
                scanner.stopScan(scanCallback)
            }

        }.map{
            (it as? SuccessResult<List<ScannedBluetoothDevice>>)?.let { result ->
                result.value
            } ?: emptyList()
        }

    fun clear() {
        Log.e("Device repos", "clearing devices search")
        devicesDataStore.clear()
    }
}