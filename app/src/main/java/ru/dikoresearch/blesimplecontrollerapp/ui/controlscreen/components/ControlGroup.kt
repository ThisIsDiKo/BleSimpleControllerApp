package ru.dikoresearch.blesimplecontrollerapp.ui.controlscreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ControlGroup(
    modifier: Modifier = Modifier,
    buttonSize: Dp,
    spacerHeight: Dp,
    contentColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.surface,
    borderColor: Color = MaterialTheme.colors.primary,
    onUpPressed: () -> Unit,
    onDownPressed: () -> Unit,
    onUpDownReleased: () -> Unit
){
    Column(
        modifier = modifier
    ) {
        ControlButton(
            modifier = Modifier
                .size(buttonSize),
            icon = Icons.Default.KeyboardArrowUp,
            contentColor = contentColor,
            backgroundColor = backgroundColor,
            borderColor = borderColor,
            onPress = {
                onUpPressed()
            },
            onRelease = {
                onUpDownReleased()
            }
        )

        Spacer(modifier = Modifier.height(spacerHeight))

        ControlButton(
            modifier = Modifier
                .size(buttonSize),
            icon = Icons.Default.KeyboardArrowDown,
            contentColor = contentColor,
            backgroundColor = backgroundColor,
            borderColor = borderColor,
            onPress = {
                onDownPressed()
            },
            onRelease = {
                onUpDownReleased()
            }
        )
    }
}

@Preview
@Composable
fun ControlGroupPreview() {
    ControlGroup(
        modifier = Modifier,
        buttonSize = 64.dp,
        spacerHeight = 6.dp,
        contentColor = Color.Red,
        backgroundColor = Color.Gray,
        borderColor = Color.Blue,
        onUpPressed = { },
        onDownPressed = { },
        onUpDownReleased = {}
    )
}
