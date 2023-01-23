package ru.dikoresearch.blesimplecontrollerapp.domain.entities

data class PressureSensor(
    val name: String,
    val alias: String,
    val info: String,
    val maxPressureBar: Float,
    val k: Float,
    val b: Float
)
