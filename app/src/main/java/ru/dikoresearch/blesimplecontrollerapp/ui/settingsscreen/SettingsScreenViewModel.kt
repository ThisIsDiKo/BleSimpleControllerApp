package ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.navOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.dikoresearch.blesimplecontrollerapp.data.LocalSettingsRepository
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.*
import ru.dikoresearch.blesimplecontrollerapp.repository.SimpleControllerRepository
import ru.dikoresearch.blesimplecontrollerapp.ui.startscreen.StartScreenState
import ru.dikoresearch.blesimplecontrollerapp.ui.startscreen.StartScreenUIEvent
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.NavigationOptions
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.ScanScreenDestination
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.SettingsScreenDestination
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.StartScreenDestination
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val localSettingsRepository: LocalSettingsRepository,
    private val controllerRepository: SimpleControllerRepository
): ViewModel() {
    private var appSettings = LocalSettingsRepository.defaultApplicationSettings
        set(value){
            field = value
            _appSettingsState.value = applicationSettingsToState(value)
        }

    private var controllerSettings = LocalSettingsRepository.defaultControllerSettings
        set(value){
            field = value
            _controllerSettingsState.value = controllerSettingsToState(value)
        }

    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state.asStateFlow()

    private val _appSettingsState = MutableStateFlow(applicationSettingsToState(appSettings))
    val appSettingsState = _appSettingsState.asStateFlow()

    private val _controllerSettingsState = MutableStateFlow(controllerSettingsToState(controllerSettings))
    val controllerSettingsState = _controllerSettingsState.asStateFlow()

    private val _uiEvent = Channel<StartScreenUIEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadSettings()
    }

    fun controlSchemeChanged(controlScheme: ControlScheme){
        appSettings = appSettings.copy(controlScheme = controlScheme)
        localSettingsRepository.setApplicationSettings(appSettings)
    }

    fun pressureSensorChanged(pressureSensor: PressureSensor){
        appSettings = appSettings.copy(pressureSensor = pressureSensor)
        localSettingsRepository.setApplicationSettings(appSettings)
    }

    fun pressureUnitsChanges(pressureUnits: PressureUnits){
        appSettings = appSettings.copy(pressureUnits = pressureUnits)
        localSettingsRepository.setApplicationSettings(appSettings)
    }

    fun showTankPressure(show: Boolean){
        appSettings = appSettings.copy(showTankPressure = show)
        localSettingsRepository.setApplicationSettings(appSettings)
    }

    fun showControls(show: Boolean){
        appSettings = appSettings.copy(showControls = show)
        localSettingsRepository.setApplicationSettings(appSettings)
    }

    fun showPressureRegulation(show: Boolean){
        appSettings = appSettings.copy(showPressureRegulation = show)
        localSettingsRepository.setApplicationSettings(appSettings)
    }

    fun deleteAllSettings(){
        val navigationOptions = navOptions {
            popUpTo(SettingsScreenDestination.url){
                inclusive = true
            }
        }
        viewModelScope.launch {
            localSettingsRepository.clearAllSettings()
            controllerRepository.removeBondAndDisconnect()
            _uiEvent.send(StartScreenUIEvent.Navigate(ScanScreenDestination, navigationOptions))
        }
    }

    fun showDeleteConfirmationDialog(){
        _state.value = state.value.copy(showDeleteConfirmationDialog = true)
    }

    fun hideDeleteConfirmationDialog(){
        _state.value = state.value.copy(showDeleteConfirmationDialog = false)
    }

    private fun loadSettings(){
        appSettings = localSettingsRepository.loadApplicationSettings()
        controllerSettings = localSettingsRepository.loadControllerSettings()
    }

    private fun applicationSettingsToState(settings: ApplicationSettings): ApplicationSettingsScreenState {
        val listOfSchemas = mutableListOf(ControlScheme.OneWayController, ControlScheme.TwoWayController)

        if (settings.controllerType == ControllerType.ExtendedPressureController){
            listOfSchemas.add(ControlScheme.ThreeWayController)
            listOfSchemas.add(ControlScheme.FourWayController)
        }

        return ApplicationSettingsScreenState(
            listOfControlSchemes = listOfSchemas,
            selectedControlScheme = settings.controlScheme,

            listOfPressureSensors = settings.listOfPressureSensors,
            selectedPressureSensor = settings.pressureSensor,

            listOfPressureUnits = PressureUnits.values().toList(),
            selectedPressureUnits = settings.pressureUnits,

            isShowTankPressureChecked = settings.showTankPressure,
            isShowControlsChecked = settings.showControls,
            isShowPressureRegulationChecked = settings.showPressureRegulation
        )
    }

    private fun controllerSettingsToState(settings: ControllerSettings): ControllerSettingsScreenState {
        return ControllerSettingsScreenState(
            controllerName = settings.controllerName,
            controllerAddress = settings.controllerAddress,
            controllerVersion = settings.controllerVersion
        )
    }

    override fun onCleared() {
        Log.e("Settings view model", "On cleared")
        super.onCleared()
    }
}