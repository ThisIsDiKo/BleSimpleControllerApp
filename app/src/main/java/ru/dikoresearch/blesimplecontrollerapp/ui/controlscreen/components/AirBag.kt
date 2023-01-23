package ru.dikoresearch.blesimplecontrollerapp.ui.controlscreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AirBag(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle,
    showPreset: Boolean,
    presetText: String,
    backgroundColor: Color,
    borderColor: Color
){
    Column(
        //modifier = Modifier.fillMaxHeight(),
        modifier = modifier
            .height(100.dp)
            .graphicsLayer {
                //shadowElevation = 8.dp.toPx()
                shape = AirBagShape()
                clip = true
            }
            .background(color = backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = AirBagShape()
            )
            .padding(start = 40.dp, top = 8.dp, end = 40.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = textStyle,
            textAlign = TextAlign.Center,
        )
        if (showPreset){
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = presetText,
                style = textStyle.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                ),
                textAlign = TextAlign.Center,
            )
        }

    }

}

class AirBagShape: Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = drawAirbagPath(size = size)
        )
    }
}

fun drawAirbagPath(size: Size): Path {
    val radius = size.height / 4.0f

    return Path().apply {
        reset()
        moveTo(radius, 0f)
        lineTo(x = size.width-radius, y = 0f)

        relativeCubicTo(
            dx1 = radius,
            dy1 = 0f,
            dx2 = radius,
            dy2 = 2f*radius,
            dx3 = 0f,
            dy3 = 2f*radius-1f,
        )
        relativeCubicTo(
            dx1 = -1f,
            dy1 = 0f,
            dx2 = -1f,
            dy2 = 2f,
            dx3 = 0f,
            dy3 = 2f,
        )
        relativeCubicTo(
            dx1 = radius,
            dy1 = 0f,
            dx2 = radius,
            dy2 = 2f*radius,
            dx3 = 0f,
            dy3 = 2f*radius-1f,
        )

        lineTo(x = radius, y = size.height)

        relativeCubicTo(
            dx1 = -radius,
            dy1 = 0f,
            dx2 = -radius,
            dy2 = -2f*radius,
            dx3 = 0f,
            dy3 = -2f*radius+1f,
        )
        relativeCubicTo(
            dx1 = 1f,
            dy1 = 0f,
            dx2 = 1f,
            dy2 = -2f,
            dx3 = 0f,
            dy3 = -2f,
        )
        relativeCubicTo(
            dx1 = -radius,
            dy1 = 0f,
            dx2 = -radius,
            dy2 = -2f*radius,
            dx3 = 0f,
            dy3 = -2f*radius+1f,
        )

        close()
    }
}

@Preview
@Composable
fun AirBagPreview(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AirBag(
            text = "10.0",
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            ),
            showPreset = true,
            presetText = "10.0",
            backgroundColor = Color.LightGray,
            borderColor = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        AirBag(
            text = "10.0",
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            ),
            showPreset = false,
            presetText = "10.0",
            backgroundColor = Color.LightGray,
            borderColor = Color.DarkGray
        )
    }

}