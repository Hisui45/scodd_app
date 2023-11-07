package com.example.scodd.chore

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.scodd.R
import com.example.scodd.components.*
import com.example.scodd.objects.*
import com.example.scodd.ui.theme.Burgundy40
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun CreateChoreScreen(focusManager: FocusManager){
    val horizontalPadding = 12.dp
    StatusBar(Burgundy40)
    Surface(Modifier.fillMaxHeight(1f).pointerInput(Unit) {
        detectTapGestures(onPress = {
            focusManager.clearFocus()
        })
    })
    {
        Column(Modifier.padding(vertical = 4.dp).fillMaxHeight().verticalScroll(rememberScrollState())){

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
        LabelText(stringResource(R.string.room_title))
        //Input copy of Room list with rooms that the chore is a part of when editing chore, default(all false) when new chore
        SelectableRoomFilterChips(scoddRooms)
    }
    ChoreDivider()
}

@Composable
fun WorkflowSection(horizontalPadding : Dp){
    val createWorkflowDialog = remember { mutableStateOf(false) }
    Column(Modifier.padding(horizontal = horizontalPadding)){
        LabelText(stringResource(R.string.workflow_title))
    }
    WorkflowSelectAddRow(onCreateWorkflowClick = { createWorkflowDialog.value = true } )
    ChoreDivider()

    CreateDialog(
        stringResource(R.string.create_workflow_label),
        onDismissRequest = {
            createWorkflowDialog.value = false
        },
        onCreateClick = {
            createWorkflowDialog.value = false
            //Push to data model to create new object
        },
        createWorkflowDialog)

}

@OptIn(ExperimentalMaterial3Api::class)
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
        LabelText(stringResource(R.string.routine_label))
        if (selectedDate != null) {
            OutlinedTextField(
                value = selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                        + "" + stringResource(R.string.date_label_conj) + "" + selectedTime,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                onValueChange = {},
                trailingIcon = {
                    Row{
                        ScoddOpenDialogFilledButtons(selectDateDialog, Icons.Default.DateRange, stringResource(R.string.date_button_contDesc) )
                        ScoddOpenDialogFilledButtons(selectTimeDialog, Icons.Default.CheckCircle, stringResource(R.string.time_button_contDesc) )
                    }

                },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    focusedLabelColor = MaterialTheme.colorScheme.secondary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.secondary),
                label = {Text(if (showSchedule.value) stringResource(R.string.date_label) else stringResource(R.string.start_date_label))}
            )
        }


        ChoreSwitch(showSchedule, stringResource(R.string.one_time_label))

        if(!showSchedule.value){
            LabelText(stringResource(R.string.schedule_label))
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
    val selected = remember { mutableStateOf(0) }  //Use to pre-fill value and use to know what it is

    FlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        scoddTimings.forEachIndexed { index, scoddTime ->
            SwitchableIconFilterChips(scoddTime.title,index,selected.value,
                onSelectedChanged = {
                    selected.value = index
                })
        }
    }

    if(scoddTimings[selected.value] == Custom){
        CustomFrequency()
    }

    if(scoddTimings[selected.value] == Weekly){
        DaysOfTheWeekChooser()
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DaysOfTheWeekChooser(){
    val selected = remember { mutableStateOf(0) }
    FlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        scoddDaysOfWeek.forEachIndexed { index, chip ->
            SwitchableFilterChip(chip.title,index,selected.value,
                onSelectedChanged = {
                    selected.value = index
                })
        }
    }

}

@Composable
fun CustomFrequency(){
    val number = remember { mutableStateOf(1) }
    val selectedOption = remember { mutableStateOf(scoddFrequency[0]) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.freq_label))
        ChoreDropdownNumberInput(number,selectedOption, scoddFrequency)
    }

    if(selectedOption.value == Week){
        DaysOfTheWeekChooser()
    }
}

@Composable
fun ModesSection(horizontalPadding: Dp){
    val crunchActive = remember { mutableStateOf(false) }
    val time = remember { mutableStateOf(1) }
    val timeUnit = remember { mutableStateOf(scoddTimeUnits[1]) }

    val bankActive = remember { mutableStateOf(false) }
    val amount = remember { mutableStateOf(1) }

    Column(Modifier.padding(horizontal = horizontalPadding)){
        LabelText(stringResource(R.string.mode_label))
        ChoreSwitch(crunchActive, stringResource(R.string.time_mode_title))
        if(crunchActive.value){
            LabelText(stringResource(R.string.time_duration_label))
            ChoreDropdownNumberInput(time,timeUnit, scoddTimeUnits)
        }

        ChoreSwitch(bankActive, stringResource(R.string.bank_mode_title))
        if(bankActive.value){
            LabelText(stringResource(R.string.bank_amount))
            BankModeInput(amount)
        }

    }
}

@Composable
fun BankModeInput(amount : MutableState<Int>){
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = if (amount.value == -1 ) "" else amount.value.toString(),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        onValueChange = {amount.value = if (it.isEmpty()) -1 else it.toInt()},
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
            focusedLabelColor = MaterialTheme.colorScheme.secondary,
            unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
            unfocusedPrefixColor = MaterialTheme.colorScheme.outline,
            focusedPrefixColor = MaterialTheme.colorScheme.outline),
        prefix = {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.attach_money_24), null)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        })
    )
}

@Composable
fun ScoddOpenDialogFilledButtons(open : MutableState<Boolean>, icon : ImageVector, contentDescription: String){
    FilledIconButton(
        onClick = {open.value = true},
        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ){
        Icon(icon, contentDescription, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoddDatePickerDialog(datePickerState: DatePickerState, selectDateDialog : MutableState<Boolean>){

    Dialog(
        onDismissRequest = {selectDateDialog.value = false},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ){
        Card(
            modifier = Modifier.padding(horizontal = 27.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(28.dp)
        ){
            Column(
                Modifier.padding(top= 16.dp, bottom = 12.dp)
            ){
                DatePicker(
                    state = datePickerState,
                    showModeToggle = true,
                    colors = DatePickerDefaults.colors(titleContentColor = MaterialTheme.colorScheme.inversePrimary,
                        headlineContentColor = MaterialTheme.colorScheme.outline,
                        weekdayContentColor = MaterialTheme.colorScheme.secondary,
                        subheadContentColor = MaterialTheme.colorScheme.inversePrimary,
//                        todayContentColor = MaterialTheme.colorScheme.inversePrimary,
                        yearContentColor = MaterialTheme.colorScheme.inversePrimary,
//                        selectedDayContentColor = MaterialTheme.colorScheme.inversePrimary,
//                        currentYearContentColor = MaterialTheme.colorScheme.inversePrimary,
//                        dayInSelectionRangeContentColor = MaterialTheme.colorScheme.inversePrimary,
//                        dayContentColor = MaterialTheme.colorScheme.inversePrimary,
//                        selectedYearContentColor = MaterialTheme.colorScheme.inversePrimary,
////                        disabledDayContentColor = MaterialTheme.colorScheme.inversePrimary,
//                        todayDateBorderColor = MaterialTheme.colorScheme.secondary,
//                        disabledSelectedDayContentColor = MaterialTheme.colorScheme.inversePrimary,
////                        selectedDayContainerColor = MaterialTheme.colorScheme.inversePrimary,
//                        selectedYearContainerColor = MaterialTheme.colorScheme.inversePrimary
                        )

                )
                val buttonColor = MaterialTheme.colorScheme.onSurfaceVariant
                Row{
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = {selectDateDialog.value = false}, //Discard Changes
                        colors = ButtonDefaults.textButtonColors(contentColor = buttonColor)
                    ){
                        Text(stringResource(R.string.dialog_option_cancel))
                    }
                    TextButton(
                        onClick = {selectDateDialog.value = false}, //Implement Changes
                        colors = ButtonDefaults.textButtonColors(contentColor = buttonColor)
                    ){
                        Text(stringResource(R.string.dialog_option_ok))
                    }
                }

            }

        }

    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoddTimePickerDialog(timePickerState: TimePickerState, selectTimeDialog : MutableState<Boolean>){
    Dialog(
        onDismissRequest = {selectTimeDialog.value = false},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ){
        Card(
            modifier = Modifier.padding(horizontal = 43.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(28.dp)
        ){
            Column(Modifier.padding(start = 24.dp, end = 24.dp, top= 24.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),){
                Text(stringResource(R.string.dialog_select_time_label))
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.outline,
                        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary)
                )
                val buttonColor = MaterialTheme.colorScheme.onSurfaceVariant
                Row{
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = {selectTimeDialog.value = false}, //Discard Changes
                        colors = ButtonDefaults.textButtonColors(contentColor = buttonColor)
                    ){
                        Text(stringResource(R.string.dialog_option_cancel))
                    }
                    TextButton(
                        onClick = {selectTimeDialog.value = false}, //Implement Changes
                        colors = ButtonDefaults.textButtonColors(contentColor = buttonColor)
                    ){
                        Text(stringResource(R.string.dialog_option_ok))
                    }
                }
            }

        }

    }


}

