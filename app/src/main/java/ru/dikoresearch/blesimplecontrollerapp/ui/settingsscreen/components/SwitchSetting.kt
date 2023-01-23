package ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SwitchSetting(
    optionName: String,
    checked: Boolean,
    paddingValues: PaddingValues,
    onCheckedChange: (Boolean) -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth().padding(paddingValues),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            //modifier = Modifier.padding(10.dp, 0.dp),
            text = optionName
        )
        Switch(
            //modifier = Modifier.padding(10.dp, 0.dp),
            checked = checked,
            onCheckedChange = { checked ->
                onCheckedChange(checked)
            }
        )
    }
}