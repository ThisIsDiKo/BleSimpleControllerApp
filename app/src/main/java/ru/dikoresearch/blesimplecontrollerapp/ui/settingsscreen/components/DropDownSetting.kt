package ru.dikoresearch.blesimplecontrollerapp.ui.settingsscreen.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.dikoresearch.blesimplecontrollerapp.R

@Composable
fun <T> DropDownSetting(
    optionName: String,
    listOfOptions: List<Pair<String, T>>,
    selectedOption: String,
    paddingValues: PaddingValues,
    onOptionClicked: (T) -> Unit
){
    var expanded by remember {
        mutableStateOf(false)
    }
    val rotateAnimation = animateFloatAsState(
        targetValue = if(expanded) 180f else 0f,
        animationSpec= tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(paddingValues),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            //modifier = Modifier.padding(10.dp, 4.dp),
            text = optionName
        )
        Box(
            modifier = Modifier
                //.padding(10.dp, 4.dp)
                //.background(color = Color.Gray, shape = RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    expanded = !expanded
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(8.dp, 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = selectedOption)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    modifier = Modifier
                        .rotate(
                            degrees = rotateAnimation.value
                        ),
                    painter = painterResource(id = R.drawable.ic_settings_24),
                    contentDescription = ""
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                listOfOptions.forEach { pair ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onOptionClicked(pair.second)
                        }
                    ) {
                        Text(text = pair.first)
                    }
                }
            }
        }
    }
}