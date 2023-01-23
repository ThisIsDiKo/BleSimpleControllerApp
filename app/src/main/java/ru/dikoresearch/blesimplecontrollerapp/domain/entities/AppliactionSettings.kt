package ru.dikoresearch.blesimplecontrollerapp.domain.entities

data class ApplicationSettings(
    val controllerType: ControllerType,
    val controlScheme: ControlScheme,
    val pressureSensor: PressureSensor,
    val listOfPressureSensors: List<PressureSensor>,
    val pressureUnits: PressureUnits,

    val showTankPressure: Boolean,
    val showControls: Boolean,
    val showPressureRegulation: Boolean
)