package ru.dikoresearch.blesimplecontrollerapp.ui.utils

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/*
fun NavOptionsBuilder.popUpToTop(navController: NavController) {
    popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
        inclusive =  true
    }
}

navController.navigate(ScreenRoutes.Login.route) { popUpToTop(navController) }

OR

navController.navigate(ScreenRoutes.Login.route){
                    popUpTo(navController.graph.findStartDestination().id){
                        inclusive = true  }}
 */
fun NavOptionsBuilder.popUpToTop(navController: NavController) {
    popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
        inclusive =  true
    }
}