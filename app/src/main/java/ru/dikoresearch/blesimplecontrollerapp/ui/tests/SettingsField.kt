package ru.dikoresearch.blesimplecontrollerapp.ui.tests

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp



enum class ControlTypes(val alias: String){
    OneWay("One Way"),
    TwoWay("Two Way"),
    TrioWay("Trio Way"),
    QuadWay("Quad way")
}

enum class ControllerModel(val alias: String){
    OnlyPressure("Only pressure"),
    SimplePressureController("Simple controller"),
    ExtendedPressureController("Quad pressure Controller"),
}


@Composable
fun SettingsField(
     controllerModel: ControllerModel
){
    //val controller = ControllerModel.SimplePressureController
    val expanded = remember { mutableStateOf(false)}
    val selectedType = remember { mutableStateOf("Unknown") }
    val listOfOptions = mutableListOf<ControlTypes>()
    listOfOptions.add(ControlTypes.OneWay)
    listOfOptions.add(ControlTypes.TwoWay)

    if (controllerModel == ControllerModel.ExtendedPressureController || controllerModel == ControllerModel.OnlyPressure){
        listOfOptions.add(ControlTypes.TrioWay)
        listOfOptions.add(ControlTypes.QuadWay)
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Controller type")
        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)){
            Text(
                modifier = Modifier.clickable {
                    expanded.value = true
                },
                text = selectedType.value
            )
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = {expanded.value = false}
            ) {
                listOfOptions.forEachIndexed{index, type ->
                    DropdownMenuItem(
                        onClick = {
                            selectedType.value = type.alias
                            expanded.value = false
                        }
                    ) {
                        Text(text = type.alias)
                    }
                }
            }
        }

    }

}

@Preview
@Composable
fun SettingsFieldPreview(){
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsField(ControllerModel.SimplePressureController)
        Spacer(modifier = Modifier.height(16.dp))
        SettingsField(ControllerModel.ExtendedPressureController)
    }

}