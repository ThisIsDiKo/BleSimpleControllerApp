package ru.dikoresearch.blesimplecontrollerapp.ui.modifiers

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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