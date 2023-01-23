package ru.dikoresearch.blesimplecontrollerapp.domain.entities

import ru.dikoresearch.blesimplecontrollerapp.R

enum class ControlScheme(val aliasStringId: Int){
    OneWayController(R.string.one_way_control),
    TwoWayController(R.string.two_way_control),
    ThreeWayController(R.string.three_way_control),
    FourWayController(R.string.four_way_control)
}