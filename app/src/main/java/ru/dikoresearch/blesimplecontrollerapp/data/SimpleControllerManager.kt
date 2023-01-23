package ru.dikoresearch.blesimplecontrollerapp.data

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.provider.ContactsContract.Data
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.WriteRequest
import no.nordicsemi.android.ble.ktx.asFlow
import no.nordicsemi.android.ble.ktx.suspend
import no.nordicsemi.android.ble.ktx.suspendForValidResponse
import ru.dikoresearch.blesimplecontrollerapp.service.BondingObserverAdapter
import ru.dikoresearch.blesimplecontrollerapp.service.ConnectionObserverAdapter
import ru.dikoresearch.blesimplecontrollerapp.utils.launchWithCatch
import java.util.*
import kotlin.collections.ArrayList

//object ServicesUUID {
//    val CONTROL_SERVICE_UUID = UUID.fromString("d2309610-80dd-11ec-a8a3-0242ac120002")
//    val OUTPUTS_CHAR_UUID = UUID.fromString("d2309611-80dd-11ec-a8a3-0242ac120002")
//    val SENSORS_CHAR_UUID = UUID.fromString("d2309612-80dd-11ec-a8a3-0242ac120002")
//    val CONFIG_CHAR_UUID = UUID.fromString("d2309613-80dd-11ec-a8a3-0242ac120002")
//    val NOTIFICATION_ALARM_CHAR_UUID = UUID.fromString("d2309614-80dd-11ec-a8a3-0242ac120002")
//    val PRESSURE_REGULATION_CHAR_UUID = UUID.fromString("d2309615-80dd-11ec-a8a3-0242ac120002")
//}

//val CONTROLLER_SERVICE_UUID = UUID.fromString("d2309610-80dd-11ec-a8a3-0242ac120002")
//private val OUTPUTS_CHARACTERISTIC_UUID = UUID.fromString("d2309611-80dd-11ec-a8a3-0242ac120002")
//private val SENSORS_CHARACTERISTIC_UUID = UUID.fromString("d2309612-80dd-11ec-a8a3-0242ac120002")

val LBS_SERVICE_UUID = UUID.fromString("00001523-1212-efde-1523-785feabcd123")
private val BUTTON_CHARACTERISTIC_UUID = UUID.fromString("00001524-1212-efde-1523-785feabcd123")
private val LED_CHARACTERISTIC_UUID = UUID.fromString("00001525-1212-efde-1523-785feabcd123")

private val SUSPENSION_SERVICE = UUID.fromString("d2309610-80dd-11ec-a8a3-0242ac120002")
private val OUTPUTS_CHARACTERISTIC_UUID = UUID.fromString("d2309611-80dd-11ec-a8a3-0242ac120002")
private val SENSORS_CHARACTERISTIC_UUID = UUID.fromString("d2309612-80dd-11ec-a8a3-0242ac120002")
private val CONFIG_CHARACTERISTIC_UUID = UUID.fromString("d2309613-80dd-11ec-a8a3-0242ac120002")
private val ALARM_CHARACTERISTIC_UUID = UUID.fromString("d2309614-80dd-11ec-a8a3-0242ac120002")

private const val TAG = "Ble Controller Manager"

sealed class ControllerResult()

/**
 * Sensors Data 16 bytes
 * struct SensorsData{
    uint16_t pressure1;
    uint16_t pressure2;
    uint16_t pressure3;
    uint16_t pressure4;
    uint16_t pressureTank;
    uint8_t pos1;
    uint8_t pos2;
    uint8_t pos3;
    uint8_t pos4;
    uint8_t flag;
    uint8_t crc;
    };
 */
data class SensorsDataResult(
    val pressureMV: ArrayList<Int>,
    val positionsPercents: ArrayList<Int>,
): ControllerResult()

/**
 * AlarmInfo 8 bytes
 */
data class AlarmDataResult(
    val lowPressure: Boolean,
    val overPressure: Boolean,
): ControllerResult()

/**
 * ConfigInfo 8 bytes
 */
data class ConfigDataResult(
    val version: String
): ControllerResult()

object IdleDataResult: ControllerResult()

class SimpleControllerManager(
    context: Context,
    private val scope: CoroutineScope,
    //private val logger: Logger
): BleManager(context) {

    //private val data = MutableStateFlow<ControllerResult>(IdleDataResult)
    //val dataHolder = ConnectionObserverAdapter<HRSData>()
    val controllerData = MutableStateFlow<ControllerResult>(IdleDataResult)
    val connectionStateHolder = ConnectionObserverAdapter<String>()
    val bondStateHolder = BondingObserverAdapter<String>()

    private var outputsCharacteristic: BluetoothGattCharacteristic? = null
    private var sensorsCharacteristic: BluetoothGattCharacteristic? = null
    private var configCharacteristic: BluetoothGattCharacteristic? = null
    private var alarmCharacteristic: BluetoothGattCharacteristic? = null
    private val settingsCharacteristic: BluetoothGattCharacteristic? = null
    private val regulationCharacteristic: BluetoothGattCharacteristic? = null

    private var useLongWrite = false

    init {
        connectionObserver = connectionStateHolder
        bondingObserver = bondStateHolder
        connectionStateHolder.setValue("Idle")

//        data.onEach{
//            connectionDataHolder.setValue(it)
//        }.launchIn(scope)

        bondStateHolder.bondingStatus.onEach { state->
            Log.i(TAG, "Got new Bond state: $state")
        }.launchIn(scope)
    }

    override fun log(priority: Int, message: String) {
        Log.w("Simple controller manager", message)
    }

    override fun getMinLogPriority(): Int {
        return Log.VERBOSE
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return SimpleControllerManagerCallback()
    }

    fun sendOutputs(bytes: ByteArray){
        if (outputsCharacteristic == null) return

        scope.launchWithCatch {
            val writeType = if (useLongWrite) {
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            } else {
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            }

            val b = printByteArray(bytes)
            log(Log.VERBOSE, "Wrote data bytes  $b")

            val request: WriteRequest =
                writeCharacteristic(outputsCharacteristic, bytes, writeType)
            if (!useLongWrite) {
                request.split()
            }
            request.suspend()
        }
    }

    fun readSensors(){
        if (sensorsCharacteristic == null) return

        scope.launchWithCatch {
            val readData = readCharacteristic(sensorsCharacteristic)
                .suspend()

            if (readData.value?.size == 16){
                val s = printByteArray(readData.value) ?: ""
                log(Log.ERROR, "Got data bytes from read sensor characteristics $s readData")

                val sensorsData = SensorsDataResult(
                    pressureMV = arrayListOf(
                        (readData.value!![1].toUByte().toInt() shl 8) + readData.value!![0].toUByte().toInt(),
                        (readData.value!![3].toUByte().toInt() shl 8) + readData.value!![2].toUByte().toInt(),
                        (readData.value!![5].toUByte().toInt() shl 8) + readData.value!![4].toUByte().toInt(),
                        (readData.value!![7].toUByte().toInt() shl 8) + readData.value!![6].toUByte().toInt(),
                        (readData.value!![9].toUByte().toInt() shl 8) + readData.value!![8].toUByte().toInt(),
                    ),
                    positionsPercents = arrayListOf(
                        readData.value!![10].toUByte().toInt(),
                        readData.value!![11].toUByte().toInt(),
                        readData.value!![12].toUByte().toInt(),
                        readData.value!![13].toUByte().toInt(),
                    )
                )
                controllerData.value = sensorsData
            }
            else {
                log(Log.ERROR, "Unexcepted data packet from sensors Char")
            }
        }
    }

    fun readConfig(){
        if (configCharacteristic == null){
            log(Log.ERROR, "Config Characteristic is null")
            return
        }
        log(Log.ERROR, "Launching reading config bytes")

        scope.launchWithCatch {
            val readData = readCharacteristic(configCharacteristic)
                .suspend()

            if (readData.value?.size == 8){
                val s = printByteArray(readData.value) ?: ""
                log(Log.VERBOSE, "Got data bytes from read config characteristics $s readData")

                val configData = ConfigDataResult(
                    version = "${readData.value!![0].toUByte()}.${readData.value!![1].toUByte()}.${readData.value!![2].toUByte()}"
                )
                controllerData.value = configData
            }
            else {
                log(Log.ERROR, "Unexcepted data packet from config Char")
            }
        }
    }

    fun enableNotification(){
        log(Log.ERROR, "Alarm char enable notification")
        enableNotifications(alarmCharacteristic).enqueue()
    }

    fun disableNotification(){
        log(Log.ERROR, "Alarm char disable notification")
        disableNotifications(alarmCharacteristic).enqueue()
    }

    private inner class SimpleControllerManagerCallback: BleManagerGattCallback(){
        @OptIn(ExperimentalCoroutinesApi::class)
        override fun initialize() {
            super.initialize()

            setNotificationCallback(alarmCharacteristic).asFlow()
                .onEach {readData ->
                    if (readData.value?.size == 8){
                        val s = printByteArray(readData.value) ?: ""
                        log(Log.VERBOSE, "Got data bytes from alarm notification $s")

                        val alarmData = AlarmDataResult(
                            lowPressure = readData.value!![0] > 0,
                            overPressure = readData.value!![1] > 0,
                        )
                        controllerData.value = alarmData
                    }
                    else {
                        log(Log.ERROR, "Unexcepted data packet from alarm Char")
                    }
                }.launchIn(scope)

        }
        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
//            gatt.getService(LBS_SERVICE_UUID)?.run {
//                outputsCharacteristic = getCharacteristic(LED_CHARACTERISTIC_UUID)
//                sensorsCharacteristic = getCharacteristic(BUTTON_CHARACTERISTIC_UUID)
//            }

            gatt.getService(SUSPENSION_SERVICE)?.run {
                outputsCharacteristic = getCharacteristic(OUTPUTS_CHARACTERISTIC_UUID)
                sensorsCharacteristic = getCharacteristic(SENSORS_CHARACTERISTIC_UUID)
                configCharacteristic = getCharacteristic(CONFIG_CHARACTERISTIC_UUID)
                alarmCharacteristic = getCharacteristic(ALARM_CHARACTERISTIC_UUID)
            }
            return outputsCharacteristic != null
        }

        override fun onServicesInvalidated() {
            outputsCharacteristic = null
            sensorsCharacteristic = null
            configCharacteristic = null
            alarmCharacteristic = null
        }

    }

    fun removeBondAndDisconnect(){
        removeBond().enqueue()
    }

    fun requestDeviceBond(){
        createBond().enqueue()
    }

    fun deviceIsBonded(): Boolean{
        return isBonded
    }

    private fun printByteArray(b: ByteArray?) = b?.joinToString(separator = " ") { eachByte -> "0x%02x".format(eachByte) }

}