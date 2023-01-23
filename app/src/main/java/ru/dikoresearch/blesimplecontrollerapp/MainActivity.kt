package ru.dikoresearch.blesimplecontrollerapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.dikoresearch.blesimplecontrollerapp.repository.SimpleControllerService
import ru.dikoresearch.blesimplecontrollerapp.ui.startscreen.StartScreen
import ru.dikoresearch.blesimplecontrollerapp.ui.controlscreen.ControlScreen
import ru.dikoresearch.blesimplecontrollerapp.ui.scanner.ScannerScreen
import ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.SettingsScreen
import ru.dikoresearch.blesimplecontrollerapp.ui.theme.BleSimpleControllerAppTheme
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.ControlScreenDestination
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.ScanScreenDestination
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.SettingsScreenDestination
import ru.dikoresearch.blesimplecontrollerapp.ui.utils.StartScreenDestination

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BleSimpleControllerAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = StartScreenDestination.url
                    ){
                        composable(ScanScreenDestination.url) { ScannerScreen(navController) }
                        composable(SettingsScreenDestination.url) { SettingsScreen(navController) }
                        composable(StartScreenDestination.url) { StartScreen(navController) }
                        composable(ControlScreenDestination.url){ ControlScreen(navController) }
                    }
//                    Greeting("Android")

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, SimpleControllerService::class.java))
    }
}