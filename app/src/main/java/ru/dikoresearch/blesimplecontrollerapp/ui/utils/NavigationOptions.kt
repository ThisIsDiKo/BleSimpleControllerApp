package ru.dikoresearch.blesimplecontrollerapp.ui.utils

data class NavigationOptions(
    val addToBackStack: Boolean? = null,
    val popUpTo: NavigationDestination? = null,
    val popUpInclusive: Boolean? = null
)
