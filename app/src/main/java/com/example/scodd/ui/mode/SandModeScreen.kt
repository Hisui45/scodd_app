package com.example.scodd.ui.mode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scodd.R
import com.example.scodd.data.Workflow1
import com.example.scodd.data.scoddChores
import com.example.scodd.model.*
import com.example.scodd.ui.theme.Marigold40
import com.example.scodd.ui.components.*
import com.example.scodd.utils.ADHD
import com.example.scodd.utils.Game


@Composable
fun SandModeScreen(
    onNavigateBack: () -> Unit,
    onAddChoreToModeClick : (Workflow) -> Unit
){
    StatusBar(Marigold40)
    val suggestions = listOf(ADHD, Game)
    val focusManager = LocalFocusManager.current
    val width = 101.dp
    val hrString = remember { mutableStateOf("") } //Take from data class
    val minString = remember { mutableStateOf("15") } //Take from data class
    val secString = remember { mutableStateOf("") } //Take from data class
    val colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.Transparent,
        focusedBorderColor = Color.Transparent)
    val numberStyle = MaterialTheme.typography.displaySmall
    val labelStyle = MaterialTheme.typography.displaySmall

    val showGuided = remember { mutableStateOf(false) }
    val currentOption = if (showGuided.value) stringResource(R.string.sand_option_two) else stringResource(R.string.sand_option_one)



    Column {
        ScoddModeHeader(
            onNavigateBack,
            stringResource(R.string.sand_mode_title),
            stringResource(R.string.sand_mode_desc),
            suggestions
        )

        Row(
            modifier = Modifier.padding(start = 12.dp, top = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.mode_option_label).plus(" $currentOption"))
            Spacer(Modifier.weight(1f))
            TextButton(
                onClick = {
                    showGuided.value = !showGuided.value
                },
            ) {
                Text(stringResource(R.string.button_switch))
            }
        }

        Row(
            modifier = Modifier.padding(top = 0.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                value = hrString.value,
                onValueChange = {
                    if (it.length >= 2) hrString.value = it.substring(0, 2) else hrString.value = it
                },
                placeholder = {
                    Text(stringResource(R.string.sand_time_placeholder), style = numberStyle)
                },
                suffix = {
                    Text(stringResource(R.string.sand_picker_hr_label), style = labelStyle)
                },
                modifier = Modifier.width(width),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions { focusManager.clearFocus() },
                colors = colors,
                textStyle = numberStyle
            )
            TextField(
                value = minString.value,
                onValueChange = {
                    if (it.length >= 2) minString.value = it.substring(0, 2) else minString.value = it
                },
                placeholder = {
                    Text(stringResource(R.string.sand_time_placeholder), style = numberStyle)
                },
                suffix = {
                    Text(stringResource(R.string.sand_picker_min_label), style = labelStyle)
                },
                modifier = Modifier.width(width),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions { focusManager.clearFocus() },
                colors = colors,
                textStyle = numberStyle
            )
            TextField(
                value = secString.value,
                onValueChange = {
                    if (it.length >= 2) secString.value = it.substring(0, 2) else secString.value = it
                },
                placeholder = {
                    Text(stringResource(R.string.sand_time_placeholder), style = numberStyle)
                },
                suffix = {
                    Text(stringResource(R.string.sand_picker_sec_label), style = labelStyle)
                },
                modifier = Modifier.width(width),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions { focusManager.clearFocus() },
                colors = colors,
                textStyle = numberStyle
            )
        }
        /**
         * TODO: Move to data class 129-153
         */
        val hrTimeString =
            createTimeString(hrString.value, R.string.sand_time_hr_plural, R.string.sand_time_hr)
        val minTimeString =
            createTimeString(minString.value,R.string.sand_time_min_plural,R.string.sand_time_min)
        val secTimeString =
            createTimeString(secString.value, R.string.sand_time_sec_plural, R.string.sand_time_sec)

        val timeString = buildString {
            if (hrTimeString.isNotEmpty()) append(hrTimeString)
            if (minTimeString.isNotEmpty()) {
                if(secTimeString.isNotEmpty() && minTimeString.isNotEmpty() && hrTimeString.isNotEmpty()){
                    append(", ")
                }else
                if (isNotEmpty()) {
                    append(" " + stringResource(R.string.sand_time_label_conj) + " ")
                }
                append(minTimeString)
            }
            if (secTimeString.isNotEmpty()) {
                if (isNotEmpty()) {
                    append(" " + stringResource(R.string.sand_time_label_conj) + " ")
                }
                append(secTimeString)
            }
        }

        Text(
            stringResource(R.string.sand_time_label).plus(" $timeString"), style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 12.dp, top = 0.dp),
            fontSize = 17.sp)

        if(showGuided.value){
//            WorkflowSelectModeRow()
            ChoreSelectModeHeaderRow("", "")
            LazyColumn {
                itemsIndexed(scoddChores){ index, chore ->
                    ModeChoreListItem(chore.title, trailingContent = {})
                    if (index < scoddChores.lastIndex)
                        Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                }
                item{
//                    AddChoreButton(onClick = {
//                        onAddChoreToModeClick(Workflow1) //So take list of chores for the mode and pass it through, the id
//
//                    })
                }
            }
        }

    }
}

@Composable
fun createTimeString(value: String,pluralResId: Int, singularResId: Int): String {
    return if (value.isNotEmpty()) {
        val valueInt = value.toInt()
        val unitString = stringResource(if (valueInt > 1) pluralResId else singularResId)
        "$value $unitString"
    } else {
        ""
    }
}


