package ru.dikoresearch.blesimplecontrollerapp.ui.startscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.navOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dikoresearch.blesimplecontrollerapp.data.ConfigDataResult
import ru.dikoresearch.blesimplecontrollerapp.data.LocalSettingsRepository
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.ControllerSettings
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.ControllerType
import ru.dikoresearch.blesimplecontrollerapp.repository.SimpleControllerRepository
import ru.dikoresearch.blesimplecontrollerapp.service.ConnectedResult
import ru.dikoresearch.blesimplecontrollerapp.service.DisconnectedResult
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.ControlScreenDestination
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.ScanScreenDestination
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.SettingsScreenDestination
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.StartScreenDestination
import javax.inject.Inject

@HiltViewModel
class StartScreenViewModel @Inject constructor(
    private val repository: SimpleControllerRepository,
    private val localSettingsRepository: LocalSettingsRepository
): ViewModel() {

    private val _state = MutableStateFlow(StartScreenState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<StartScreenUIEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()
    private var initRun = true

    init {
        repository.controllerData.onEach {
            when(it){
                is ConfigDataResult -> {
//                    val localControllerVersion= localSettingsRepository.loadControllerSettings().controllerVersion
//                    val remoteControllerVersion = it.version
//
//                    if (remoteControllerVersion != localControllerVersion){
//                        val newControllerSettings = localSettingsRepository.loadControllerSettings().copy(controllerVersion = remoteControllerVersion)
//                        localSettingsRepository.setControllerSettings(newControllerSettings)
//                        Log.e("", "Updating controller settings to $newControllerSettings")
//                    }


                }
                else -> {

                }
            }
        }.launchIn(viewModelScope)

        repository.connectionState.onEach {
            when(it){
                is ConnectedResult -> {
                    _state.value = state.value.copy(isConnecting = false)
//                    Log.e("", "Reading config")
//                    repository.readConfig()

                    val options = navOptions {
                        popUpTo(StartScreenDestination.url){
                            inclusive = true
                        }
                    }
                    _uiEvent.send(StartScreenUIEvent.Navigate(ControlScreenDestination, options = options))

//                    val options = navOptions {
//                        popUpTo(StartScreenDestination.url){
//                            inclusive = true
//                        }
//                    }
//                    _uiEvent.send(StartScreenUIEvent.Navigate(ControlScreenDestination, options = options))
                }
                else -> {

                }
            }
        }.launchIn(viewModelScope)
    }

    fun startConnecting(){
        if (initRun){
            viewModelScope.launch{
                initRun = false
                val s = localSettingsRepository.loadControllerSettings()
                Log.e("", "Got settings $s")
                val deviceName = s.controllerName
                val deviceAddress = s.controllerAddress

                if (deviceAddress.isBlank()){
                    _state.value = state.value.copy(
                        isLoading = false,
                        hasSavedDevice = false,
                        isConnecting = false,
                    )
                }
                else {
                    _state.value = state.value.copy(
                        deviceName = deviceName,
                        deviceAddress = deviceAddress,
                        isLoading = false,
                        hasSavedDevice = true,
                        isConnecting = true,
                    )
                    repository.launch(deviceAddress)
                }
            }
        }

    }

    fun navigateToSettingsScreen(){
        //TODO Рассмотреть вариант ситуации, при которой мы перешли на экран настроект,
        // а в этот момент подключилось устройство,
        // а на экране настроект выбрали "Удалить устройство"
        viewModelScope.launch {
            Log.e("", "Navigation to settings screen")
            _uiEvent.send(StartScreenUIEvent.Navigate(SettingsScreenDestination))
        }
    }

    fun navigateToScanScreen(){
        viewModelScope.launch {
            Log.e("", "Navigation to scan screen")
            val options = navOptions {
                popUpTo(StartScreenDestination.url){
                    inclusive = true
                }
            }
            _uiEvent.send(StartScreenUIEvent.Navigate(ScanScreenDestination, options = options))
        }
    }

    override fun onCleared() {
        Log.e("", "Start Screen View Model Is cleared")
        super.onCleared()
    }
}