package ru.dikoresearch.blesimplecontrollerapp.ui.controlscreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun ControlButton(
    modifier: Modifier,
    icon: ImageVector,
    contentColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.surface,
    borderColor: Color = MaterialTheme.colors.primary,
    onPress: () -> Unit,
    onRelease: () -> Unit
){
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedButton(
        modifier = modifier
            .actionWithRippleEffect(
                interactionSource = interactionSource,
                onPress = onPress,
                onRelease = onRelease
            ),
        shape = CircleShape,
        border = BorderStroke(
            ButtonDefaults.OutlinedBorderSize,
            borderColor.copy(alpha = ButtonDefaults.OutlinedBorderOpacity)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        ),
        onClick = { /*TODO*/ },
    ) {
        Icon(
            imageVector = icon,
            contentDescription ="",
            modifier = Modifier.size(64.dp),
            //tint = MaterialTheme.colors.secondary
        )
    }
}

fun Modifier.actionWithRippleEffect(
    interactionSource: MutableInteractionSource,
    onPress: () -> Unit,
    onRelease: () -> Unit
): Modifier = composed {
    pointerInput(interactionSource){ //add enabled
        forEachGesture {
            coroutineScope {
                awaitPointerEventScope {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val pressed = PressInteraction.Press(down.position)
                    val job = launch {
                        interactionSource.emit(pressed)
                        onPress()
                    }

                    val up = waitForUpOrCancellation()
                    job.cancel()

                    val releaseOrCancel = when(up){
                        null -> PressInteraction.Cancel(pressed)
                        else -> PressInteraction.Release(pressed)
                    }

                    launch {
                        interactionSource.emit(releaseOrCancel)
                        onRelease()
                    }
                }
            }
        }
    }
    //.indication(interactionSource, rememberRipple(bounded = true))
}

@Preview
@Composable
fun ControlButtonPreview(){
    ControlButton(
        modifier = Modifier
            .size(64.dp),
        icon = Icons.Default.KeyboardArrowUp,
        contentColor = Color.Red,
        backgroundColor = Color.Transparent,
        onPress = { /*TODO*/ },
        onRelease = {})
}