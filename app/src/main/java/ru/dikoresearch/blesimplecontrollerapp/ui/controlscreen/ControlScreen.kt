package ru.dikoresearch.blesimplecontrollerapp.ui.controlscreen

import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.collectLatest
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.ControlScheme
import ru.dikoresearch.blesimplecontrollerapp.ui.controlscreen.subscreens.FourWayControl
import ru.dikoresearch.blesimplecontrollerapp.ui.controlscreen.subscreens.OneWayControl
import ru.dikoresearch.blesimplecontrollerapp.ui.controlscreen.subscreens.TwoWayControl
import ru.dikoresearch.blesimplecontrollerapp.ui.startscreen.StartScreenUIEvent

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ControlScreen(
    navController: NavController
){
    val viewModel: ControlScreenViewModel = hiltViewModel()
    val lifecycleOwner = LocalLifecycleOwner.current
    val scaffoldState = rememberScaffoldState()
    val state = viewModel.screenState.collectAsState().value

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(key1 = Unit){
        lifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.uiEvent.collectLatest { event->
                when(event){
                    is StartScreenUIEvent.Navigate -> {
                        navController.navigate(event.destination.url, navOptions = event.options)
                    }
                    is StartScreenUIEvent.ShowToast -> {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = event.message
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver{ _, event ->
                if (event == Lifecycle.Event.ON_RESUME){
                    if (permissionState.allPermissionsGranted){
                        Log.e("", "Going to load settings and start reading")
                        viewModel.loadSettings()
                        viewModel.startReadingSensors()
                    }
                    else{
                        viewModel.sendOutputs("00000000")
                        viewModel.stopReadingSensors()
                        //TODO Cancel connection and go to startScreen
                        //navController.navigate("startscreen")
                    }

                }
                else if (event == Lifecycle.Event.ON_PAUSE){
                    viewModel.sendOutputs("00000000")
                    viewModel.stopReadingSensors()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.navigateToSettingsScreen()
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Filled.Settings, contentDescription = ""
                        )
                    }
                },
                backgroundColor = Color.White,
                elevation = 0.dp
            )
        }
    ){ paddingValues ->
        if (state.isConnecting){
            Dialog(
                onDismissRequest = { /*TODO*/ },
                properties =  DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment= Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Connecting")
                        Spacer(modifier = Modifier.height(4.dp))
                        CircularProgressIndicator()
                    }

                }
            }
        }
        else if (state.isLoading){

        }
        else {
            when(state.controlScheme){
                ControlScheme.OneWayController -> {
                    OneWayControl(
                        paddingValues = paddingValues,
                        pressure1 = state.sensorsState.pressures[0],
                        pressureInTank = state.sensorsState.pressures[4],
                        showPressureInTank = false,
                        showControlGroup = true,
                        showRegulationGroup = false,
                        onOutputClicked = {
                            viewModel.sendOutputs(it)
                        }
                    )
                }
                ControlScheme.TwoWayController -> {
                    TwoWayControl(
                        paddingValues = paddingValues,
                        pressure1 =state.sensorsState.pressures[0],
                        pressure2 = state.sensorsState.pressures[1],
                        pressureInTank = state.sensorsState.pressures[4],
                        showPressureInTank = false,
                        showControlGroup = true,
                        showRegulationGroup = false,
                        onOutputClicked = {
                            viewModel.sendOutputs(it)
                        }
                    )
                }
                ControlScheme.ThreeWayController -> {

                }
                ControlScheme.FourWayController -> {
                    FourWayControl(
                        paddingValues = paddingValues,
                        pressure1 = state.sensorsState.pressures[0],
                        pressure2 = state.sensorsState.pressures[1],
                        pressure3 = state.sensorsState.pressures[2],
                        pressure4 = state.sensorsState.pressures[3],
                        pressureInTank = state.sensorsState.pressures[4],
                        showPressureInTank = false,
                        showControlGroup = true,
                        showRegulationGroup = false,
                        onOutputClicked = {
                            viewModel.sendOutputs(it)
                        }
                    )
                }
            }
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(paddingValues),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Text(
//                    text = String.format(
//                        "%.2f %.2f %.2f %.2f %.2f",
//                        state.sensorsState.pressures[0],
//                        state.sensorsState.pressures[1],
//                        state.sensorsState.pressures[2],
//                        state.sensorsState.pressures[3],
//                        state.sensorsState.pressures[4]
//                    )
//                )
//                Button(onClick = {viewModel.sendOutputs("00")}) {
//                    Text(text = "off")
//                }
//                Button(onClick = {viewModel.sendOutputs("11")}) {
//                    Text(text = "on")
//                }
//            }

        }
    }
}