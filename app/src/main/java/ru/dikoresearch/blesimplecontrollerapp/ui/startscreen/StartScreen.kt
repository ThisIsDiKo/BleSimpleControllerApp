package ru.dikoresearch.blesimplecontrollerapp.ui.startscreen

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.collectLatest
import ru.dikoresearch.blesimplecontrollerapp.R


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartScreen(
    navController: NavController,
) {
    val viewModel: StartScreenViewModel = hiltViewModel()
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.state.collectAsState().value
    val scaffoldState = rememberScaffoldState()

    val showPermissionInfoDialog = remember { mutableStateOf(false) }

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
                    Log.e("", "Start screen Resume")
                    if (permissionState.allPermissionsGranted){
                        viewModel.startConnecting()
                    }
                    else{
                        showPermissionInfoDialog.value = true
                    }
                }
                else {
                    Log.e("", "Start screen ${event.name}")
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
                            Log.e("", "Settings view clicked")
                            viewModel.navigateToSettingsScreen()
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Filled.Settings,
                            contentDescription = ""
                        )
                    }
                },
                backgroundColor = Color.White,
                elevation = 0.dp
            )
        }
    ) { padding ->

        if(showPermissionInfoDialog.value){
            AlertDialog(
                title = { Text(text = "") },
                text = { Text(text = "Начиная с версии Android 6.0, для работы с Bluetooth приложению необходимо разреншение на доступ к геопозиции") },
                onDismissRequest = { /*TODO*/ },
                confirmButton = {
                    Button(
                        onClick = {
                            showPermissionInfoDialog.value = false
                            permissionState.launchMultiplePermissionRequest()
                        }) {
                        Text("Ok")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showPermissionInfoDialog.value = false
                        }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //TODO need to set photo here
            Image(
                modifier = Modifier
                    .size(300.dp),
                painter = painterResource(id = R.drawable.ic_start_screen_logo),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (!state.isLoading){
                if (state.hasSavedDevice){
                    if (state.isConnecting){
                        CircularProgressIndicator()
                        Text(text = stringResource(R.string.is_connecting))
                    }
                    else {
                        Text(text = state.connectionErrorMessage)
                    }
                }
                else {
                    TextButton(
                        onClick = {
                            viewModel.navigateToScanScreen()
                        }
                    ) {
                        Text(text = "Start scan")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun StartScreenPreview(){
    StartScreen(rememberNavController())
}