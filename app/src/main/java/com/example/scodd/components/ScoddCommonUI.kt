package com.example.scodd.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginButton(onClick: () -> Unit, text : String){
    Button(onClick = { onClick() },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
    ){
        Text(text)
    }
}

@Composable
fun LabelText(text : String){
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
fun ChoreSwitch(checked : Boolean, label : String, onCheckChanged : () -> Unit){
    val checked = remember { mutableStateOf(checked) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(label)
        Spacer(Modifier.weight(1f))
        Switch(
            checked = checked.value,
            onCheckedChange = {checked.value = it
                onCheckChanged()},
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onSecondary,
                checkedTrackColor = MaterialTheme.colorScheme.secondary)
        )
    }

}

@Composable
fun ChoreDivider(){
    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

}