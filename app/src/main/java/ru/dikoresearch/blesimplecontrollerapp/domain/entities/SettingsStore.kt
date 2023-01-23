package ru.dikoresearch.blesimplecontrollerapp.domain.entities

data class SettingsStore(
    val applicationSettings: ApplicationSettings,
    val controllerSettings: ControllerSettings,
    val remoteConfig: RemoteSettings,
)
