package ru.dikoresearch.blesimplecontrollerapp.ui.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import ru.dikoresearch.blesimplecontrollerapp.R
import ru.dikoresearch.blesimplecontrollerapp.ui.startscreen.StartScreenUIEvent

@SuppressLint("MissingPermission")
@Composable
fun ScannerScreen(
    navController: NavController
){
    val viewModel: ScannerViewModel = hiltViewModel()
    val result = viewModel.devices.collectAsState().value
    val isConnecting = viewModel.showConnectionDialog.collectAsState().value
    val dialogMessage = viewModel.connectionDialogMessage.collectAsState().value

    val lifeCycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = Unit){
        lifeCycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.event.collectLatest { event ->
                when(event){
                    is StartScreenUIEvent.Navigate -> {
                        navController.navigate(event.destination.url, navOptions = event.options)
                    }
                    is StartScreenUIEvent.ShowToast -> {

                    }
                }
            }
        }
    }

    if (isConnecting) {
        Dialog(
            onDismissRequest = {},
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment= Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = dialogMessage)
                    Spacer(modifier = Modifier.height(4.dp))
                    CircularProgressIndicator()
                }
                
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Select device to connect to")
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(contentPadding = PaddingValues(horizontal = 8.dp)){
            items(items = result, itemContent = { device ->
                ScanItem(
                    deviceName = device.device.name ?: stringResource(id = R.string.unknown_device),
                    deviceAddress = device.device.address,
                    rssi = device.rssi,
                    onClick = {
                        viewModel.connectToDevice(it)
                    }
                )
            })
        }
    }
}