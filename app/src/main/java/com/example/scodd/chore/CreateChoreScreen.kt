package com.example.scodd.chore

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.scodd.components.*
import com.example.scodd.objects.*
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun CreateChoreScreen(focusManager: FocusManager){
    val horizontalPadding = 12.dp

    Surface(Modifier.fillMaxHeight(1f).pointerInput(Unit) {
        detectTapGestures(onPress = {
            focusManager.clearFocus()
        })
    })
    {
        Column(Modifier.padding(vertical = 8.dp)){

            RoomSection(horizontalPadding)

            WorkflowSection(horizontalPadding)

            RoutineSection(horizontalPadding)

            ModesSection(horizontalPadding)

        }

    }
}

@Composable
fun RoomSection(horizontalPadding : Dp){
    Column(Modifier.padding(horizontal = horizontalPadding)){
        LabelText("Room")
        RoomFiltersIconFlowRow()
    }
    ChoreDivider()
}

@Composable
fun WorkflowSection(horizontalPadding : Dp){
    val createWorkflowDialog = remember { mutableStateOf(false) }
    Column(Modifier.padding(horizontal = horizontalPadding)){
        LabelText("Workflow")
    }
    WorkflowSelectRow(onCreateWorkflowClick = { createWorkflowDialog.value = true } )
    ChoreDivider()

    if(createWorkflowDialog.value){
        CreateWorkflowDialog(createWorkflowDialog)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RoutineSection(horizontalPadding: Dp){
    val selectDateDialog = remember { mutableStateOf(false) }
    val selectTimeDialog = remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    val timePickerState = rememberTimePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC)
    }
    val selectedTime = LocalTime.of(timePickerState.hour,timePickerState.minute).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

    val showSchedule = remember { mutableStateOf(true) }

    Column(Modifier.padding(horizontal = horizontalPadding)){
        LabelText("Routine")
        if (selectedDate != null) {
            OutlinedTextField(
                value = selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) + " at " + selectedTime,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                onValueChange = {},
                trailingIcon = {
                    Row{
                        ScoddOpenDialogFilledButtons(selectDateDialog, Icons.Default.DateRange, "date" )
                        ScoddOpenDialogFilledButtons(selectTimeDialog, Icons.Default.CheckCircle, "time" )
                    }

                },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.secondary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.secondary),
                label = {Text(if (showSchedule.value) "Date" else "Start Date")}
            )
        }


        ChoreSwitch(showSchedule.value,"One-Time", onCheckChanged = {showSchedule.value = !showSchedule.value} )

        if(!showSchedule.value){
            LabelText("Schedule")
            TimingsChooser()
        }

    }

    ChoreDivider()

    if(selectDateDialog.value){
        ScoddDatePickerDialog(datePickerState, selectDateDialog)
    }

    if(selectTimeDialog.value){
        ScoddTimePickerDialog(timePickerState,selectTimeDialog)
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimingsChooser(){

    var selectedChip = remember {mutableStateOf(scoddTimings.find { it.selected }) }

    FlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        scoddTimings.forEach { interval ->
            var selected = remember { mutableStateOf(interval.selected) }
            FilterIconChip(interval.title,selected,
                onClick = {
//                    var currentSelected = scoddTimings.find { it.selected }
//                    if (currentSelected != null) {
//                        currentSelected.selected = false
//                    }
                    interval.selected = !interval.selected
                    selected.value = !selected.value
                    selectedChip.value = scoddTimings.find { it.selected }
                }
            )
        }
    }

    if(selectedChip.value == Custom){
        CustomFrequency()
        DaysOfTheWeekChooser()
    }

    if(selectedChip.value == Weekly){
        DaysOfTheWeekChooser()
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DaysOfTheWeekChooser(){
    var selectedChip = remember {mutableStateOf(scoddDaysOfWeek.find { it.selected }) }

    FlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        scoddDaysOfWeek.forEach { interval ->
            var selected = remember { mutableStateOf(interval.selected) }
            FilterChip(interval.title,selected,
                onClick = {
                    interval.selected = !interval.selected
                    selected.value = !selected.value
                    selectedChip.value = scoddDaysOfWeek.find { it.selected }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFrequency(){
    val number = remember { mutableStateOf(1) }
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(scoddFrequency[0]) }
    val pattern = remember { Regex("^\\d+\$") }
    val focusManager = LocalFocusManager.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ){

        Text("Every")
        OutlinedTextField(
            value = if (number.value == 0) "" else number.value.toString(),
            onValueChange = {
                if (it.isEmpty() || it.matches(pattern)) {
                    number.value = if (it == "") 0 else it.toInt()
                }
               },
            modifier = Modifier.width(150.dp),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline)
            )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ){
            OutlinedTextField(
                readOnly = true,
                value = if (number.value <= 1) selectedOptionText.title else selectedOptionText.title + "s",
                onValueChange = { },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(unfocusedTrailingIconColor = MaterialTheme.colorScheme.outline,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline),
                modifier = Modifier.menuAnchor(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                scoddFrequency.forEach { frequency ->
                    DropdownMenuItem(
                        text = {
                            Text(if (number.value <= 1) frequency.title else frequency.title + "s" )
                               },
                        onClick = {
                            selectedOptionText = frequency
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ModesSection(horizontalPadding: Dp){
    Column(Modifier.padding(horizontal = horizontalPadding)){
        LabelText("Modes")
        ChoreSwitch(false,"Time Crunch", onCheckChanged = {})
        ChoreSwitch(false,"Piggy Bank", onCheckChanged = {})
    }
}

@Composable
fun ScoddOpenDialogFilledButtons(open : MutableState<Boolean>, icon : ImageVector, contentDescription: String){
    FilledIconButton(
        onClick = {open.value = true},
        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ){
        Icon(icon, contentDescription, tint = MaterialTheme.colorScheme.outline)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoddDatePickerDialog(datePickerState: DatePickerState, selectDateDialog : MutableState<Boolean>){
    DatePickerDialog(
        onDismissRequest = {selectDateDialog.value = false},
        confirmButton = {
            TextButton(
                onClick = {selectDateDialog.value = false}
            ){
                Text("Okay")
            }
        }
    ){
        DatePicker(
            state = datePickerState,
            showModeToggle = true
            )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoddTimePickerDialog(timePickerState: TimePickerState, selectTimeDialog : MutableState<Boolean>){
    Dialog(
        onDismissRequest = {selectTimeDialog.value = false}
    ){
        Card(
            Modifier.fillMaxWidth()
        ){
            TimePicker(
                state = timePickerState,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
            )
        }

    }


}

@Composable
fun CreateWorkflowDialog(openAlertDialog: MutableState<Boolean>){
    var title = ""
    Dialog(
        onDismissRequest = {openAlertDialog.value = false}
    ){
        ElevatedCard{
            Column(Modifier.padding(16.dp)){
                Text("New Workflow", modifier = Modifier.padding(vertical = 12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = {title = it},
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Row{
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = {},
                    ){
                        Text("Create")
                    }
                }
            }

        }
    }
}