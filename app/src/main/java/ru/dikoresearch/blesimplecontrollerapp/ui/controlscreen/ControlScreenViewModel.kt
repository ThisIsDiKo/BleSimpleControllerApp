package ru.dikoresearch.blesimplecontrollerapp.ui.controlscreen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dikoresearch.blesimplecontrollerapp.data.*
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.ControlScheme
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.PressureSensor
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.PressureUnits
import ru.dikoresearch.blesimplecontrollerapp.repository.SimpleControllerRepository
import ru.dikoresearch.blesimplecontrollerapp.repository.SuccessResult
import ru.dikoresearch.blesimplecontrollerapp.service.*
import ru.dikoresearch.blesimplecontrollerapp.ui.startscreen.StartScreenUIEvent
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.SettingsScreenDestination
import javax.inject.Inject

data class ControlScreenState(
    val isConnecting: Boolean = false,
    val isLoading: Boolean = true,
    val controlScheme: ControlScheme = ControlScheme.OneWayController,
    val sensorsState: SensorsState = SensorsState(),
)

data class SensorsState(
    val pressures: List<String> = listOf("----", "----", "----", "----", "----")
)

@HiltViewModel
class ControlScreenViewModel @Inject constructor(
    private val settingsRepository: LocalSettingsRepository,
    private val controllerRepository: SimpleControllerRepository
): ViewModel() {

    private val TAG = "ControlViewModel"

    private var readingJob: Job? = null

    private val _screenState = MutableStateFlow(ControlScreenState())
    val screenState = _screenState.asStateFlow()

    private val _uiEvent = Channel<StartScreenUIEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private var appSettings = LocalSettingsRepository.defaultApplicationSettings
    private var controllerSettings = LocalSettingsRepository.defaultControllerSettings

    init {

        viewModelScope.launch {
            delay(1000)
            enableNotification()
        }

        controllerRepository.connectionState.onEach {result ->
            Log.e(TAG, "Got new connection result $result")
            when(result){
                is DisconnectedResult, is LinkLossResult, is UnknownErrorResult, is MissingServiceResult -> {
                    Log.e(TAG, "Disconnect result $result")
                    _screenState.value = screenState.value.copy(isConnecting = true)
                    stopReadingSensors()
                }
                is DeviceIsReadyResult -> {
                    _screenState.value = screenState.value.copy(isConnecting = false)
                    startReadingSensors()
                }
                else -> {

                }
            }
        }.launchIn(viewModelScope)

        controllerRepository.controllerData.onEach { result ->
            when(result){
                is AlarmDataResult -> {
                    Log.e(TAG, "New Alarm Result $result")
                }
                is SensorsDataResult -> {
                    Log.e(TAG, "New Sensors Result $result")
                    processSensorsData(result)
                }
                is ConfigDataResult -> {
                    Log.e(TAG, "New Config Result $result")

                }
            }
        }.launchIn(viewModelScope)


    }

    private fun processAlarmData(alarm: AlarmDataResult){
        //TODO send alarm info to server
        Log.e(TAG, "Processing alarm data $alarm")
    }

    private fun processConfigData(config: ConfigDataResult){

    }

    private fun processSensorsData(sensors: SensorsDataResult){
        val sensorsPressure = sensors.pressureMV
            .map { calculatePressure(it, appSettings.pressureSensor, appSettings.pressureUnits) }
            .map { convertPressureToString(it, appSettings.pressureUnits) }

        val sensorsState = SensorsState(sensorsPressure)
        _screenState.value = screenState.value.copy(sensorsState = sensorsState)
    }

    fun loadSettings(){
        _screenState.value = screenState.value.copy(isLoading = true)

        appSettings = settingsRepository.loadApplicationSettings()

        _screenState.value = screenState.value.copy(controlScheme = appSettings.controlScheme)

        Log.e(TAG, "Got app settings: $appSettings")
        controllerSettings = settingsRepository.loadControllerSettings()

        _screenState.value = screenState.value.copy(isLoading = false)
    }

    fun sendOutputs(text: String){
        controllerRepository.sendData(text)
    }

    private fun readSensors(){
        controllerRepository.readData()
    }

    fun readConfig(){
        controllerRepository.readConfig()
    }

    /**
     * Returning pressure in bar
     */
    private fun calculatePressure(value: Int, pressureSensor: PressureSensor, pressureUnits: PressureUnits): Float{
        val k = pressureSensor.k
        val b = pressureSensor.b

        val result = when(pressureUnits){
            PressureUnits.BAR -> {
                value.toFloat() / 1000.0f * k + b
            }
            PressureUnits.PSI -> {
                (value.toFloat() / 1000.0f * k + b) * 14.5038f
            }
            PressureUnits.MV -> {
                value.toFloat() / 1000.0f
            }
        }

        if (result < 0.0f) return -1.0f
        return result
    }

    private fun convertPressureToString(pressure: Float, pressureUnits: PressureUnits): String{
        if (pressure < 0.0f) return "----"
        return when(pressureUnits){
            PressureUnits.BAR -> {
                String.format("%.1f", pressure)
            }
            PressureUnits.PSI -> {
                String.format("%.0f", pressure)
            }
            PressureUnits.MV -> {
                String.format("%.2f", pressure)
            }
        }
    }

    fun startReadingSensors(){
        if (readingJob != null) return

        Log.e(TAG, "Start reading sensors")

        readingJob = viewModelScope.launch(Dispatchers.IO) {
            while(true){
                readSensors()
                delay(1000)
            }
        }
    }

    fun stopReadingSensors(){
        if (readingJob != null){
            readingJob?.cancel()
            readingJob = null
        }
    }

    fun navigateToSettingsScreen(){
        viewModelScope.launch {
            _uiEvent.send(StartScreenUIEvent.Navigate(SettingsScreenDestination))
        }
    }

    private fun enableNotification(){
        controllerRepository.enableNotification(true)
    }

    private fun disableNotification(){
        controllerRepository.enableNotification(false)
    }

    override fun onCleared() {
        disableNotification()
        super.onCleared()
    }
}