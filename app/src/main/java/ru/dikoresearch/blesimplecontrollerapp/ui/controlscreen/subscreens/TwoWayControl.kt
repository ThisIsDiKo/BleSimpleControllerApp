package ru.dikoresearch.blesimplecontrollerapp.ui.controlscreen.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.dikoresearch.blesimplecontrollerapp.ui.controlscreen.components.AirBag
import ru.dikoresearch.blesimplecontrollerapp.ui.controlscreen.components.ControlGroup

@Composable
fun TwoWayControl(
    paddingValues: PaddingValues,
    pressure1: String,
    pressure2: String,
    pressureInTank: String,
    showPressureInTank: Boolean,
    showControlGroup: Boolean,
    showRegulationGroup: Boolean,
    onOutputClicked: (String) -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                AirBag(
                    text = pressure1,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    showPreset = false,
                    presetText = "",
                    backgroundColor = Color.LightGray,
                    borderColor = Color.DarkGray
                )
                AirBag(
                    text = pressure2,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    showPreset = false,
                    presetText = "",
                    backgroundColor = Color.LightGray,
                    borderColor = Color.DarkGray
                )
            }

            if(showPressureInTank){
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Pressure in tank: $pressureInTank")
            }
        }

        if (showControlGroup){
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp, 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlGroup(
                    buttonSize = 100.dp,
                    spacerHeight = 15.dp,
                    contentColor = Color.Black,
                    backgroundColor = Color.Transparent,
                    borderColor = Color.Black,
                    onUpPressed = {
                        onOutputClicked("0001")
                    },
                    onDownPressed = {
                        onOutputClicked("0010")
                    },
                    onUpDownReleased =  {
                        onOutputClicked("0000")
                    },
                )
                ControlGroup(
                    buttonSize = 115.dp,
                    spacerHeight = 15.dp,
                    onUpPressed = {
                        onOutputClicked("0101")
                    },
                    onDownPressed = {
                        onOutputClicked("1010")
                    },
                    onUpDownReleased =  {
                        onOutputClicked("0000")
                    },
                )
                ControlGroup(
                    buttonSize = 100.dp,
                    spacerHeight = 15.dp,
                    onUpPressed = {
                        onOutputClicked("0100")
                    },
                    onDownPressed = {
                        onOutputClicked("1000")
                    },
                    onUpDownReleased =  {
                        onOutputClicked("0000")
                    },
                )
            }
        }
    }
}