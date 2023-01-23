package ru.dikoresearch.blesimplecontrollerapp.repository

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.ktx.suspend
import ru.dikoresearch.blesimplecontrollerapp.data.ControllerResult
import ru.dikoresearch.blesimplecontrollerapp.data.IdleDataResult
import ru.dikoresearch.blesimplecontrollerapp.data.SimpleControllerManager
import ru.dikoresearch.blesimplecontrollerapp.service.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.experimental.inv

@Singleton
class SimpleControllerRepository @Inject internal constructor(
    @ApplicationContext
    private val context: Context,
    private val serviceManager: ServiceManager
    //TODO private val loggerFactory: LoggerFactory
){
    private var manager: SimpleControllerManager? = null

    private val _connectionState = MutableStateFlow<BleManagerResult<String>>(IdleResult())
    val connectionState = _connectionState.asStateFlow()

    private val _bondingState = MutableStateFlow<BleBondingManagerResult<String>>(BondingIdleResult())
    val bondingState = _bondingState.asStateFlow()

    private val _controllerData = MutableStateFlow<ControllerResult>(IdleDataResult)
    val controllerData = _controllerData.asStateFlow()

    val isRunning = connectionState.map { it.isRunning() }
    val hasBeenDisconnected = connectionState.map { it.hasBeenDisconnected() }
    private var isClosingResources = false

    private var isRemovingBond = false

    fun launch(device: BluetoothDevice){
        serviceManager.startService(SimpleControllerService::class.java, device)
    }

    fun launch(address: String){
        val device = getRemoteDevice(address)
        launch(device!!)
    }

    fun start(device: BluetoothDevice, scope: CoroutineScope){
        //val createdLogger = loggerFactory.create(stringConst.APP_NAME, "HRS", device.address()).also {
        //            logger = it
        //        }
        Log.e("", "Starting manager")
        val manager = SimpleControllerManager(context, scope)
        this.manager = manager
        isRemovingBond = false

        manager.connectionStateHolder.status.onEach{
            Log.e("Simple Controller Repository", "Got new Connection state: $it")
            if (isRemovingBond && (it is DisconnectedResult)){
                Log.e("", "Removing bond and disconnect result")
                stopService()
                isRemovingBond = false
            }

            if (isClosingResources && (it is DisconnectedResult)){
                Log.e("Simple Controller Repository", "Disconnected and is closing")
                manager.close()
            }
            _connectionState.value = it
        }.launchIn(scope)

        manager.bondStateHolder.bondingStatus.onEach {
            Log.e("Simple Controller Repository", "Got new Bond state: $it")
            _bondingState.value = it
        }.launchIn(scope)

        manager.controllerData.onEach {
            Log.e("Simple Controller Repository", "Got new controller data state: $it")
            _controllerData.value = it
        }.launchIn(scope)

        scope.launch {
            manager.start(device)
        }
    }

    /**
     * string MSB first "00000001"
     * will auto reversed to LSB first
     */
    fun sendData(text: String){
        var intValues = 0

        text.reversed().forEachIndexed { index, c ->
            if (c == '1'){
                intValues = intValues or (1 shl index)
            }
        }

        val bytes = byteArrayOf(
            intValues.toByte(),
            (intValues shr 8).toByte(),
            intValues.toByte().inv(),
            (intValues shr 8).toByte().inv()
        )

        Log.e("Simple Controller Repository", "Converted text to bytes: $text -> ${bytes.joinToString(separator = " ") { eachByte -> "0x%02x".format(eachByte) }}")

        manager?.sendOutputs(bytes)
    }

    fun readData(){
        manager?.readSensors()
    }

    fun readConfig(){
        Log.e("Simple Controller Repository", "Reading config")
        manager?.readConfig()
    }

    fun openLogger(){
        //TODO logger?.openLogger()
    }

    fun getRemoteDevice(address: String): BluetoothDevice?{
        return try{
            val device = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter.getRemoteDevice(address)
            device
        } catch (e: Exception){
            null
        }
    }

    private suspend fun SimpleControllerManager.start(device: BluetoothDevice){
        try {
            connect(device)
                .useAutoConnect(true)
                //.useAutoConnect(false)
                .retry(3, 100)
                .suspend()
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun release(){
        Log.e("Simple Controller Repository", "Realising manager")
        manager?.disconnect()?.enqueue()
        isClosingResources = true

        //TODO logger = null
        //manager = null
    }

    fun stopService(){
        serviceManager.stopService(SimpleControllerService::class.java)
    }

    fun getConnectedDeviceAddress(): String{
        return manager?.bluetoothDevice?.address ?: ""
    }

    fun enableNotification(state: Boolean){
        if (state){
            manager?.enableNotification()
        }
        else {
            manager?.disableNotification()
        }
    }

    fun removeBondAndDisconnect(){
        isRemovingBond = true
        manager?.removeBondAndDisconnect()
    }

    fun makeBond(){
        manager?.requestDeviceBond()
    }

    fun deviceIsBonded(): Boolean{
        return manager?.deviceIsBonded() ?: false
    }
}