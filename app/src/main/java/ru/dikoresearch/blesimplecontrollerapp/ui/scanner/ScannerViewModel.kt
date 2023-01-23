package ru.dikoresearch.blesimplecontrollerapp.ui.scanner

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import ru.dikoresearch.blesimplecontrollerapp.data.ConfigDataResult
import ru.dikoresearch.blesimplecontrollerapp.data.LocalSettingsRepository
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.ControllerSettings
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.ControllerType
import ru.dikoresearch.blesimplecontrollerapp.repository.DevicesRepository
import ru.dikoresearch.blesimplecontrollerapp.repository.SimpleControllerRepository
import ru.dikoresearch.blesimplecontrollerapp.service.*
import ru.dikoresearch.blesimplecontrollerapp.ui.startscreen.StartScreenUIEvent
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.ControlScreenDestination
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.NavigationOptions
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.ScanScreenDestination
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val devicesRepository: DevicesRepository,
    private val repository: SimpleControllerRepository,
    private val localSettingsRepository: LocalSettingsRepository
): ViewModel() {

    private val _event = Channel<StartScreenUIEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private val _showConnectionDialog = MutableStateFlow(false)
    val showConnectionDialog = _showConnectionDialog.asStateFlow()

    private val _connectionDialogMessage = MutableStateFlow("Connecting")
    val connectionDialogMessage = _connectionDialogMessage.asStateFlow()

    private var deviceName: String = ""
    private var deviceAddress: String = ""

    init {
        repository.controllerData.onEach {
            Log.e("Scan view model", "New Controller Data")
            when(it){
                is ConfigDataResult -> {
                    val controllerSettings = ControllerSettings(
                        controllerName = deviceName,
                        controllerAddress = deviceAddress,
                        controllerVersion = it.version
                    )


                    val controllerType = when(deviceName){
                        "AC PR 4" -> {
                            ControllerType.ExtendedPressureController
                        }
                        else -> {
                            ControllerType.SimplePressureController
                        }
                    }

                    val appSettings = LocalSettingsRepository.defaultApplicationSettings.copy(controllerType = controllerType)

                    Log.e("Scan view model", "Saving controller data $controllerSettings")
                    Log.e("Scan view model", "Saving application data $appSettings")


                    localSettingsRepository.setControllerSettings(controllerSettings)
                    localSettingsRepository.setApplicationSettings(appSettings)

                    _showConnectionDialog.value = false

                    val navOptions = androidx.navigation.navOptions {
                        popUpTo(ScanScreenDestination.url){
                            inclusive = true
                        }
                    }
                    _event.send(StartScreenUIEvent.Navigate(destination = ControlScreenDestination, options = navOptions))
                }
                else -> {

                }
            }
        }.launchIn(viewModelScope)

        repository.connectionState.onEach {
            when(it){
                is DeviceIsReadyResult -> {
                    _connectionDialogMessage.value = "Bonding"
                    if (repository.deviceIsBonded()){
                        Log.e("", "Device is already bonded")
                        deviceName = it.device.name ?: ""
                        deviceAddress = it.device.address

                        delay(1000)
                        repository.readConfig()
                        _connectionDialogMessage.value = "Reading config data"
                    }
                    else {
                        Log.e("", "Start Bonding")
                        repository.makeBond()
                    }
                }
                else -> {

                }
            }
        }.launchIn(viewModelScope)

        repository.bondingState.onEach {
            Log.e("Scan view model", "Got new bond state: $it")
            when(it){
                is BondingSuccessResult -> {
                    Log.e("Scan view model", "Bonding Success")
                    deviceName = it.device.name ?: ""
                    deviceAddress = it.device.address
                    repository.readConfig()
                    _connectionDialogMessage.value = "Reading config data"
                }
                is BondingFailedResult -> {
                    Log.e("Scan view model", "Bonding Failed")
                    _showConnectionDialog.value = false
                    repository.stopService()
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    val devices = devicesRepository.getDevices()
//        .onEach {
//            Log.e("Scanner view model", "Got ${it.size} devices")
//        }
        .map {
            it.toMutableList()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun connectToDevice(address: String){
        _showConnectionDialog.value = true
        _connectionDialogMessage.value = "Connecting"
        repository.launch(address)
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("", "Scanner view model cleared")
        devicesRepository.clear()
    }

}