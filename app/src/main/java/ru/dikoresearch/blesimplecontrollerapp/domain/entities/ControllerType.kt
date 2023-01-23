package ru.dikoresearch.blesimplecontrollerapp.domain.entities

import ru.dikoresearch.blesimplecontrollerapp.R

enum class ControllerType(val aliasStringId: Int) {
    SimplePressureController(R.string.simple_controller),
    ExtendedPressureController(R.string.extended_controller),
}