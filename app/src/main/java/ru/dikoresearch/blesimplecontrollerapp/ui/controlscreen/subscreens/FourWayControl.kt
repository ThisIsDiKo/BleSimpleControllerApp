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
fun FourWayControl(
    paddingValues: PaddingValues,
    pressure1: String,
    pressure2: String,
    pressure3: String,
    pressure4: String,
    pressureInTank: String,
    showPressureInTank: Boolean,
    showControlGroup: Boolean,
    showRegulationGroup: Boolean,
    onOutputClicked: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                AirBag(
                    text = pressure3,
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
                    text = pressure4,
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
                        onOutputClicked("01010101")
                    },
                    onDownPressed = {
                        onOutputClicked("10101010")
                    },
                    onUpDownReleased =  {
                        onOutputClicked("00000000")
                    },
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        ControlGroup(
                            buttonSize = 80.dp,
                            spacerHeight = 10.dp,
                            onUpPressed = {
                                onOutputClicked("00000001")
                            },
                            onDownPressed = {
                                onOutputClicked("00000010")
                            },
                            onUpDownReleased =  {
                                onOutputClicked("00000000")
                            },
                        )
                        ControlGroup(
                            buttonSize = 80.dp,
                            spacerHeight = 10.dp,
                            onUpPressed = {
                                onOutputClicked("00000100")
                            },
                            onDownPressed = {
                                onOutputClicked("00001000")
                            },
                            onUpDownReleased =  {
                                onOutputClicked("00000000")
                            },
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        ControlGroup(
                            buttonSize = 80.dp,
                            spacerHeight = 10.dp,
                            onUpPressed = {
                                onOutputClicked("00010000")
                            },
                            onDownPressed = {
                                onOutputClicked("00100000")
                            },
                            onUpDownReleased =  {
                                onOutputClicked("00000000")
                            },
                        )
                        ControlGroup(
                            buttonSize = 80.dp,
                            spacerHeight = 10.dp,
                            onUpPressed = {
                                onOutputClicked("01000000")
                            },
                            onDownPressed = {
                                onOutputClicked("10000000")
                            },
                            onUpDownReleased =  {
                                onOutputClicked("00000000")
                            },
                        )
                    }
                }

            }
        }
    }
}