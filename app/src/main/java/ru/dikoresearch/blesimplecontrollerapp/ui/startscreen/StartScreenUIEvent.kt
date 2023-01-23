package ru.dikoresearch.blesimplecontrollerapp.ui.startscreen

import android.os.Bundle
import androidx.navigation.NavOptions
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.NavigationDestination
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.NavigationOptions

sealed class StartScreenUIEvent{
    data class Navigate(
        val destination: NavigationDestination,
        val options: NavOptions? = null,
        val bundle: Bundle? = null
    ): StartScreenUIEvent()
    data class ShowToast(val message: String): StartScreenUIEvent()
}