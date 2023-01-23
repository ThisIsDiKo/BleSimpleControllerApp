package ru.dikoresearch.blesimplecontrollerapp.ui.utils

sealed class NavigationDestination(val url: String)

object StartScreenDestination: NavigationDestination("startscreen")
object ScanScreenDestination: NavigationDestination("scanscreen")
object ControlScreenDestination: NavigationDestination("controlscreen")
object SettingsScreenDestination: NavigationDestination("settingsscreen")
