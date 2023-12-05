package com.example.scodd.ui.chore

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scodd.R
import com.example.scodd.model.*
import com.example.scodd.ui.theme.Burgundy40
import com.example.scodd.ui.components.*
import com.example.scodd.ui.theme.RoosterRed40
import com.example.scodd.utils.*
import java.time.DayOfWeek

@Composable
fun CreateChoreScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateChoreViewModel = hiltViewModel(),
    choreId: String? ,
    ){
    StatusBar(RoosterRed40)
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog = remember { mutableStateOf(false) }

    val contentColor = MaterialTheme.colorScheme.onSecondary
    Scaffold(
        topBar = {
            TextFieldTopBar(
                onNavigateBack = onNavigateBack,
                focusManager = focusManager,
                title = uiState.title,
                type = TextFieldTopBarType.CHORE,
                contentColor = contentColor,
                onTitleChanged = viewModel :: updateTitle,
                actions = {
                    TextButton(onClick = viewModel :: saveChore){Text(stringResource(R.string.save_button), color = contentColor)}
                    if(choreId!=null){
                        ChoreContextMenu(onAddClicked = {
                            viewModel.addToRoundUp()
                        }, onDeleteClicked = {showDeleteDialog.value = true}, tint = contentColor)
                    }
                },
                hintColor = MaterialTheme.colorScheme.onSecondary
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)  }
    ) {
        Surface(Modifier.fillMaxHeight(1f).padding(it).testTag("ChoreScreen")
            .pointerInput(Unit) {
            detectTapGestures(onPress = { focusManager.clearFocus()})}
        ){
            CreateChoreContent(viewModel = viewModel, uiState = uiState)

            LaunchedEffect(uiState.isChoreSaved) {
                if (uiState.isChoreSaved) {
                    onNavigateBack()
                }
            }

            LaunchedEffect(uiState.isChoreDeleted) {
                if (uiState.isChoreDeleted) {
                    onNavigateBack()
                }
            }

            DeleteConfirmationDialog(onConfirm = {viewModel.deleteChore();showDeleteDialog.value = false},
                onDismiss = {showDeleteDialog.value = false}, showDeleteDialog.value)

//          Check for user messages to display on the screen
            uiState.userMessage?.let { userMessage ->
                val snackbarText = stringResource(userMessage)
                LaunchedEffect(scope, viewModel, userMessage, snackbarText) {
                    snackbarHostState.showSnackbar(message = snackbarText, duration = SnackbarDuration.Short)
                    viewModel.snackbarMessageShown()
                }
            }
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateChoreContent(viewModel: CreateChoreViewModel, uiState: CreateChoreUiState){
    val horizontalPadding = 12.dp
    Column(Modifier.padding(vertical = 4.dp).fillMaxHeight().verticalScroll(rememberScrollState())) {
        // Room Section
        /**
         * TODO: clicking on +2 More shows other rooms priority: 4
         */
        Column(Modifier.padding(horizontal = horizontalPadding)){
            LabelText(stringResource(R.string.room_title))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                uiState.rooms.forEach{ room -> //Use index to know what room was selected
                    SelectableRoomFilterChip(room.title,viewModel.isRoomSelected(room),
                        onSelectedChanged = {viewModel.selectRoom(room.id)},
                        modifier = Modifier)
                }
            }
        }
        ChoreDivider()

        //WorkflowSection
        val createWorkflowDialog = remember { mutableStateOf(false) }
        Column(Modifier.padding(horizontal = horizontalPadding)){
            LabelText(stringResource(R.string.workflow_title))
        }
        WorkflowSelectRow(
            uiState.workflows,
            viewModel :: isWorkflowSelected,
            viewModel :: selectWorkflow
        ) { createWorkflowDialog.value = true }
        ChoreDivider()

        CreateDialog(
            stringResource(R.string.create_workflow_label), onDismissRequest = {createWorkflowDialog.value = false},
            onCreateClick = { title ->
                viewModel.createWorkflow(title)
                createWorkflowDialog.value = false //Push to data model to create new object
            },
            createWorkflowDialog)



        RoutineSection(horizontalPadding,
            showSchedule = uiState.routineInfo.isOneTime,
            onDateChanged = viewModel :: dateChange,
            onTimeChanged = viewModel :: timeChange,
            onCheckChanged = {viewModel.switchOneTime(it)},
            frequencyErrorMessage = uiState.frequencyErrorMessage,
            onFrequencyValueChanged = {viewModel.changeValue(it,ChoreInputType.FREQUENCY)},
            onFrequencyOptionSelected = {viewModel.selectFrequencyOption(it)},
            scheduleTimeText = uiState.scheduleTimeText,
            onScheduleTypeSelected = {viewModel.selectScheduleType(it)},
            onWeekDaySelected = {viewModel.selectWeekday(it)},
            routineInfo = uiState.routineInfo
        )

        //Modes Section
        Column(Modifier.padding(horizontal = horizontalPadding)){
            LabelText(stringResource(R.string.mode_label))

            ChoreSwitch(uiState.isTimeModeActive, stringResource(R.string.time_mode_title), onCheckChanged = {viewModel.switchTimerMode(it)})
            if(uiState.isTimeModeActive){
                LabelText(stringResource(R.string.time_duration_label))
                ChoreDropdownNumberInput(uiState.timerModeValue,uiState.timerOption, onOptionSelect = {viewModel.selectTimerOption(it)}, scoddTimeUnits,
                    onValueChange = {viewModel.changeValue(it, ChoreInputType.TIMER)},
                    errorMessage = uiState.timerErrorMessage )
            }

            ChoreSwitch(uiState.isBankModeActive, stringResource(R.string.bank_mode_title), onCheckChanged = {viewModel.switchBankMode(it)})
            if(uiState.isBankModeActive){
                LabelText(stringResource(R.string.bank_amount))
                BankModeInput(uiState.bankModeValue, onBankValueChanged = {viewModel.changeValue(it, ChoreInputType.BANK)})
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineSection(horizontalPadding: Dp,
                   frequencyErrorMessage: Int?,
                   onFrequencyValueChanged : (String) -> Unit,
                   showSchedule : Boolean,
                   onDateChanged: (Long?) -> Unit,
                   onTimeChanged: (Int, Int) -> Unit,
                   onCheckChanged : (Boolean) -> Unit,
                   onFrequencyOptionSelected : (ScoddTime) -> Unit,
                   scheduleTimeText : String,
                   onScheduleTypeSelected: (ScoddTime) -> Unit,
                   onWeekDaySelected: (ScoddTime) -> Unit,
                   routineInfo: RoutineInfo
){

    val selectDateDialog = remember {mutableStateOf(false)}
    val selectTimeDialog = remember {mutableStateOf(false)}

    var datePickerState = rememberDatePickerState()

    if(routineInfo.date != null){
        datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = routineInfo.date
        )
    }



//    initialHour = Instant.now().atZone(ZoneOffset.systemDefault()).hour,
//    initialMinute = Instant.now().atZone(ZoneId.systemDefault()).minute



    Column(Modifier.padding(horizontal = horizontalPadding)){
        LabelText(stringResource(R.string.routine_label))
            OutlinedTextField(
                value = scheduleTimeText,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                onValueChange = {},
                trailingIcon = {
                    Row{
                        ScoddOpenDialogFilledButtons(selectDateDialog, R.drawable.ic_edit_cal, stringResource(R.string.date_button_contDesc) )
                        ScoddOpenDialogFilledButtons(selectTimeDialog, R.drawable.ic_time, stringResource(R.string.time_button_contDesc) )
                    }

                },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    focusedLabelColor = MaterialTheme.colorScheme.secondary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.secondary),
                label = {Text(if (showSchedule) stringResource(R.string.date_label) else stringResource(R.string.start_date_label))}
            )

        ChoreSwitch(showSchedule, stringResource(R.string.one_time_label), onCheckChanged = {onCheckChanged(it)})

        if(!showSchedule){
            LabelText(stringResource(R.string.schedule_label))
            TimingsChooser(
                frequencyValue = routineInfo.frequencyValue,
                frequencyErrorMessage = frequencyErrorMessage,
                selectedFrequencyOption = routineInfo.frequencyOption,
                onFrequencyOptionSelected = {onFrequencyOptionSelected(it)},
                onFrequencyValueChanged = {onFrequencyValueChanged(it)},
                scheduleType = routineInfo.scheduleType,
                onScheduleTypeSelected = {onScheduleTypeSelected(it)},
                selectedWeekDay = RoutineInfo.getScoddTime(routineInfo.weeklyDay),
                onWeekDaySelected = {onWeekDaySelected(it)}
            )
        }

    }

    ChoreDivider()

    if(selectDateDialog.value){
        ScoddDatePickerDialog(datePickerState,
            saveChanges = { onDateChanged(datePickerState.selectedDateMillis)
            selectDateDialog.value = false},
            onDismissRequest = {selectDateDialog.value = false})
    }

    var timePickerState: TimePickerState? = null

    if(selectTimeDialog.value){
        timePickerState = rememberTimePickerState(
            initialHour = routineInfo.hour,
            initialMinute = routineInfo.minute
        )
        ScoddTimePickerDialog(timePickerState,
            saveChanges = {onTimeChanged(timePickerState.hour, timePickerState.minute)
            selectTimeDialog.value = false },
            onDismissRequest = {selectTimeDialog.value = false})
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimingsChooser(frequencyValue: Int, frequencyErrorMessage: Int?,
                   onFrequencyValueChanged : (String) -> Unit, selectedFrequencyOption: ScoddTime, onFrequencyOptionSelected : (ScoddTime) -> Unit,
                   scheduleType : ScoddTime, onScheduleTypeSelected: (ScoddTime) -> Unit,
                   selectedWeekDay : ScoddTime, onWeekDaySelected: (ScoddTime) -> Unit){

    FlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        scoddTimings.forEach{ scoddTime ->
            SwitchableIconFilterChips(scoddTime,scheduleType,
                onSelectedChanged = {
                    onScheduleTypeSelected(scoddTime)
                })
        }
    }

    if(scheduleType == ScoddTime.CUSTOM){
        //Custom Schedule
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.freq_label))
            ChoreDropdownNumberInput(frequencyValue, selectedFrequencyOption, onOptionSelect = {onFrequencyOptionSelected(it)}, scoddFrequency,
                onValueChange = {onFrequencyValueChanged(it)}, errorMessage = frequencyErrorMessage)
        }

        if(selectedFrequencyOption == ScoddTime.WEEK){
            DaysOfTheWeekChooser(selectedWeekDay, onWeekDaySelected = {onWeekDaySelected(it)})
        }
    }

    if(scheduleType == ScoddTime.WEEKLY){
        DaysOfTheWeekChooser(selectedWeekDay, onWeekDaySelected = {onWeekDaySelected(it)})
    }

}



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DaysOfTheWeekChooser(selectedWeekDay : ScoddTime, onWeekDaySelected: (ScoddTime) -> Unit){

    FlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        scoddDaysOfWeek.forEach{ weekDay ->
            SwitchableFilterChip(weekDay,selectedWeekDay,
                onSelectedChanged = {
                    onWeekDaySelected(weekDay)
                })
        }
    }

}

@Composable
fun BankModeInput(amount : Int, onBankValueChanged: (String) -> Unit){
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = if (amount == -1 ) "" else amount.toString(),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        onValueChange = {onBankValueChanged(it)},
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
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        })
    )
}

@Composable
fun ScoddOpenDialogFilledButtons(open : MutableState<Boolean>, icon : Int, contentDescription: String){
    FilledIconButton(
        onClick = {open.value = true},
        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ){
        Icon(painterResource(icon), contentDescription, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoddDatePickerDialog(datePickerState: DatePickerState, saveChanges : () -> Unit, onDismissRequest : () -> Unit){

    Dialog(
        onDismissRequest = {onDismissRequest()},
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
                        yearContentColor = MaterialTheme.colorScheme.inversePrimary,
                        )
                )
                val buttonColor = MaterialTheme.colorScheme.onSurfaceVariant
                Row{
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = {onDismissRequest()}, //Discard Changes
                        colors = ButtonDefaults.textButtonColors(contentColor = buttonColor)
                    ){
                        Text(stringResource(R.string.dialog_option_cancel))
                    }
                    TextButton(
                        onClick = {saveChanges()}, //Implement Changes
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
fun ScoddTimePickerDialog(timePickerState: TimePickerState, saveChanges : () -> Unit, onDismissRequest : () -> Unit){
    Dialog(
        onDismissRequest = {onDismissRequest()},
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
                        onClick = {onDismissRequest()}, //Discard Changes
                        colors = ButtonDefaults.textButtonColors(contentColor = buttonColor)
                    ){
                        Text(stringResource(R.string.dialog_option_cancel))
                    }
                    TextButton(
                        onClick = {saveChanges()}, //Implement Changes
                        colors = ButtonDefaults.textButtonColors(contentColor = buttonColor)
                    ){
                        Text(stringResource(R.string.dialog_option_ok))
                    }
                }
            }

        }

    }
}
@Composable
fun ChoreContextMenu(
    onAddClicked : () -> Unit,
    onDeleteClicked: () -> Unit,
    tint: Color
){
    ContextMenu(
        tint = tint
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.add_roundup)) },
            onClick = { onAddClicked();it.value = false }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.delete)) },
            onClick = { onDeleteClicked();it.value = false }
        )

    }
}


