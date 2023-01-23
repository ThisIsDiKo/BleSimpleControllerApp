package ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.ControlScheme
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.PressureSensor
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.PressureUnits
import ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.ApplicationSettingsScreenState
import ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.components.DropDownSetting
import ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.components.SwitchSetting

@Composable
fun ApplicationSettingsScreen(
    state: ApplicationSettingsScreenState,
    paddingValues: PaddingValues,
    onControlSchemeChanged: (ControlScheme) -> Unit,
    onPressureSensorChanged: (PressureSensor) -> Unit,
    onPressureUnitsChanged: (PressureUnits) -> Unit,
    onShowPressureTankChanged: (Boolean) -> Unit,
    onShowControlsChanged: (Boolean) -> Unit,
    onShowPressureRegulationChanged: (Boolean) -> Unit
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        DropDownSetting(
            optionName = "Control Scheme",
            listOfOptions = state.listOfControlSchemes
                .map {
                    stringResource(id = it.aliasStringId) to it
                },
            selectedOption = stringResource(id = state.selectedControlScheme.aliasStringId),
            paddingValues = paddingValues,
            onOptionClicked = { onControlSchemeChanged(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        DropDownSetting(
            optionName = "Pressure Sensor",
            listOfOptions = state.listOfPressureSensors
                .map {
                    it.name to it
                },
            selectedOption = state.selectedPressureSensor.name,
            paddingValues = paddingValues,
            onOptionClicked = { onPressureSensorChanged(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        DropDownSetting(
            optionName = "Pressure Units",
            listOfOptions = state.listOfPressureUnits
                .map {
                    stringResource(id = it.aliasStringId) to it
                },
            selectedOption =  stringResource(id = state.selectedPressureUnits.aliasStringId),
            paddingValues = paddingValues,
            onOptionClicked = { onPressureUnitsChanged(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        SwitchSetting(
            optionName = "Show Tank Pressure",
            checked = state.isShowTankPressureChecked,
            paddingValues = paddingValues,
            onCheckedChange = { onShowPressureTankChanged(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        SwitchSetting(
            optionName = "Show controls",
            checked = state.isShowControlsChecked,
            paddingValues = paddingValues,
            onCheckedChange = { onShowControlsChanged(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        SwitchSetting(
            optionName = "Show pressure regulation",
            checked = state.isShowPressureRegulationChecked,
            paddingValues = paddingValues,
            onCheckedChange = { onShowPressureRegulationChanged(it) }
        )
    }
}