package ru.dikoresearch.blesimplecontrollerapp.service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

sealed interface BleBondingManagerResult<T> {
    fun isBonded(): Boolean{
        return this is BondingSuccessResult
    }

    fun isBondingFailed(): Boolean{
        return this is BondingRequiredResult || this is BondingFailedResult
    }

}

sealed class BondingStateHolder(val device: BluetoothDevice){
    @SuppressLint("MissingPermission")
    fun deviceName(): String = device.name ?: device.address
}

class BondingIdleResult<T>: BleBondingManagerResult<T>
class BondingRequiredResult<T>(device: BluetoothDevice): BondingStateHolder(device), BleBondingManagerResult<T>
class BondingSuccessResult<T>(device: BluetoothDevice): BondingStateHolder(device), BleBondingManagerResult<T>
class BondingFailedResult<T>(device: BluetoothDevice): BondingStateHolder(device), BleBondingManagerResult<T>