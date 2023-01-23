package ru.dikoresearch.blesimplecontrollerapp.service

import android.bluetooth.BluetoothDevice
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.nordicsemi.android.ble.observer.BondingObserver

class BondingObserverAdapter<T>: BondingObserver  {
    private val TAG = "BLE-BONDING"

    private val _bondingStatus = MutableStateFlow<BleBondingManagerResult<T>>(BondingIdleResult())
    val bondingStatus = _bondingStatus.asStateFlow()

    override fun onBondingRequired(device: BluetoothDevice) {
        Log.d(TAG, "onBondingRequired()")
        _bondingStatus.value = BondingRequiredResult(device)
    }

    override fun onBonded(device: BluetoothDevice) {
        Log.d(TAG, "onBonded()")
        _bondingStatus.value = BondingSuccessResult(device)
    }

    override fun onBondingFailed(device: BluetoothDevice) {
        Log.d(TAG, "onBondingFailed()")
        _bondingStatus.value = BondingFailedResult(device)
    }
}