package ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.ControllerSettingsScreenState

@Composable
fun ControllerSettingsScreen(
    state: ControllerSettingsScreenState,
    onDeleteControllerClicked: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = state.controllerName)
        Text(text = state.controllerAddress)
        Text(text = state.controllerVersion)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {onDeleteControllerClicked()}) {
            Text("Delete controller")
        }
    }
}