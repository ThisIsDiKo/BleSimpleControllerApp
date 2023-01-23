package ru.dikoresearch.blesimplecontrollerapp.ui.scanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.dikoresearch.blesimplecontrollerapp.R

@Composable
fun ScanItem(
    deviceName: String,
    deviceAddress: String,
    rssi: Int,
    onClick: (String) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                onClick(deviceAddress)
            },
        elevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = deviceName)
                    Text(text = deviceAddress)
                }
            }
            Text(text = "rssi $rssi")
        }
    }
}

@Preview
@Composable
fun ScanItemPreview(){
    ScanItem(
        deviceName = "My Device",
        deviceAddress = "00:99:00:88:99:11",
        rssi = -60,
        onClick = {})
}