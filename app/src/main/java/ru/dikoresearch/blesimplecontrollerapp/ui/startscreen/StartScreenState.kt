package ru.dikoresearch.blesimplecontrollerapp.ui.startscreen

data class StartScreenState(
    val deviceName: String = "",
    val deviceAddress: String = "",
    val connectionErrorMessage: String = "",
    val isConnecting: Boolean = false,
    val isLoading: Boolean = true,
    val hasSavedDevice: Boolean = false
)