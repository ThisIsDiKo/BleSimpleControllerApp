package ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen

import ru.dikoresearch.blesimplecontrollerapp.domain.entities.ControlScheme
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.PressureSensor
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.PressureUnits

data class SettingsScreenState(
    val isLoading: Boolean = true,
    val showDeleteConfirmationDialog: Boolean = false
)

data class ApplicationSettingsScreenState(
    val listOfControlSchemes: List<ControlScheme>,
    val selectedControlScheme: ControlScheme,

    val listOfPressureSensors: List<PressureSensor>,
    val selectedPressureSensor: PressureSensor,

    val listOfPressureUnits: List<PressureUnits>,
    val selectedPressureUnits: PressureUnits,

    val isShowTankPressureChecked: Boolean,
    val isShowControlsChecked: Boolean,
    val isShowPressureRegulationChecked: Boolean
)

data class ControllerSettingsScreenState(
    val controllerName: String,
    val controllerAddress: String,
    val controllerVersion: String,
)