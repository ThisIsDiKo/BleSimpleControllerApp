package ru.dikoresearch.blesimplecontrollerapp.domain.entities

import ru.dikoresearch.blesimplecontrollerapp.R

enum class PressureUnits(val aliasStringId: Int) {
    BAR(R.string.bar_units),
    PSI(R.string.psi_units),
    MV(R.string.mv_units)
}