package ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen

import android.content.res.Resources
import androidx.compose.runtime.Composable

typealias ComposableFun = @Composable () -> Unit
sealed class SettingsTabItem(val title: String){
    object ApplicationSettings: SettingsTabItem("AppSettings")
    object ControllerSettings: SettingsTabItem("ControllerSettings")
}