package com.example.scodd.ui.mode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scodd.R
import com.example.scodd.model.ScoddMode
import com.example.scodd.model.ScoddMode.*
import com.example.scodd.navigation.ModeBottomBar
import com.example.scodd.ui.components.*
import com.example.scodd.ui.theme.Marigold40
import com.example.scodd.utils.ADHD
import com.example.scodd.utils.Game
import com.example.scodd.utils.Motivation

@Composable
fun StartModeScreen(
    selectedItems: State<List<String>?>,
    onNavigateBack: () -> Unit,
    onAddChoreClick : (List<String>) -> Unit,
    onEditChore: (String) -> Unit,
    onStartClick: (List<String>, String, Long) -> Unit,
    viewModel: ModesViewModel = hiltViewModel()
) {
    val suggestions = listOf("ADHD", "Gamify", "Motivation")
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()
    val mode = uiState.mode

    val uiTimerState by viewModel.uiTimerState.collectAsState()

    StatusBar(Marigold40)
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = { ModeBottomBar(
            enabled =
            when(mode){
                is QuestMode ->{
                    uiState.selectedRooms.isNotEmpty()
                }
                is SandMode -> {
                    if(uiTimerState.showGuided){
                        checkNoErrors(mode, uiState.selectedChores, uiState.choresFromWorkflow, viewModel)
                                && checkSandTimerValue(uiTimerState.hourValue, uiTimerState.minuteValue, uiTimerState.secondValue)
                    }else{
                        checkSandTimerValue(uiTimerState.hourValue, uiTimerState.minuteValue, uiTimerState.secondValue)
                    }
                }
                else -> {
                    checkNoErrors(
                        mode = mode,
                        selectedChores = uiState.selectedChores,
                        choresFromWorkflow = uiState.choresFromWorkflow,
                        viewModel = viewModel
                    )
                }
           },
            onStartClick = {onStartClick(
                when(mode){
                    is SandMode ->{
                        if(uiTimerState.showGuided){
                            viewModel.allSelectedChores()
                        }else{
                            emptyList()
                        }
                    }
                    is QuestMode ->{
                        viewModel.getRoomTitles()
                    }
                    else ->{
                        viewModel.allSelectedChores()
                    }
                },
                uiState.mode.modeId,
                when(mode){
                    is SandMode -> {
                        TimeUtils.getTimeListInSeconds(listOf(uiTimerState.hourValue, uiTimerState.minuteValue, uiTimerState.secondValue))
                    }
                    is TimeMode -> {
                        viewModel.getFirstChoreTimeDuration()
                    }
                    else -> {
                        20
                    }
                }

                )}) }
    ) {
        Column(Modifier.padding(it)) {
            val currentOption =
                if (uiTimerState.showGuided) stringResource(R.string.sand_option_two) else stringResource(R.string.sand_option_one)

            ScoddModeHeader(onNavigateBack, stringResource(mode.title), stringResource(mode.description), suggestions)

            when (mode) {
                is SandMode -> {
                    Row(
                        modifier = Modifier.padding(start = 12.dp, top = 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.mode_option_label).plus(" $currentOption"))
                        Spacer(Modifier.weight(1f))
                        TextButton(
                            onClick = { viewModel.switchShowGuided() },
                        ) { Text(stringResource(R.string.button_switch)) }
                    }

                    TimerTextField(
                        hrValue = uiTimerState.hourValue,
                        onHrValueChange = { newValue ->
                            viewModel.changeHrValue(newValue)
                        },
                        minValue = uiTimerState.minuteValue,
                        onMinValueChange = { newValue ->
                            viewModel.changeMinValue(newValue)

                        },
                        secValue = uiTimerState.secondValue,
                        onSecValueChange = { newValue ->
                            viewModel.changeSecValue(newValue)

                        },
                    )

                    val timerText =
                        getTimerText(uiTimerState.hourValue, uiTimerState.minuteValue, uiTimerState.secondValue)
                    Text(
                        stringResource(R.string.sand_time_label).plus(" $timerText"),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 12.dp, top = 0.dp),
                        fontSize = 17.sp
                    )

                    if (uiTimerState.showGuided) {
                        WorkflowSelectModeRow(
                            workflows = uiState.parentWorkflows,
                            isSelected = viewModel::isWorkflowSelected,
                            onWorkflowSelect = viewModel::selectWorkflow
                        )
                    }
                }

                is QuestMode -> {

                }

                else -> {
                    WorkflowSelectModeRow(
                        workflows = uiState.parentWorkflows,
                        isSelected = viewModel::isWorkflowSelected,
                        onWorkflowSelect = viewModel::selectWorkflow
                    )
                }
            }

            val noErrors = checkNoErrors(mode, uiState.selectedChores, uiState.choresFromWorkflow, viewModel)

            when (mode) {

                is BankMode -> {
                    val potentialPayout = if(noErrors){
                            viewModel.calculatePotentialPayout()
                        }else{
                            "?"
                        }
                    ChoreSelectModeHeaderRow(stringResource(R.string.bank_potential_payout), "$$potentialPayout")
                }

                is TimeMode -> {
                    val estimatedTime = if(noErrors){
                        viewModel.calculateEstimatedTime()
                    }else{
                        "?"
                    }
                    ChoreSelectModeHeaderRow(stringResource(R.string.time_esitmation), estimatedTime)
                }

                is QuestMode -> {
                    val isSelectAll = uiState.selectedRooms.containsAll(uiState.rooms.map { room -> room.id })
                    Text(
                        stringResource(R.string.quest_directions), style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp).align(Alignment.CenterHorizontally)
                    )
                    Row(
                        modifier = Modifier.padding(start = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.quest_focus))
                        Spacer(Modifier.weight(1f))
                        LabelText(stringResource(R.string.select_all_label))
                        RadioButton(
                            selected = isSelectAll,
                            onClick = { viewModel.selectAll(isSelectAll) },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.secondary)
                        )
                    }
                }

                is SandMode -> {
                    if (uiTimerState.showGuided) {
                        ChoreSelectModeHeaderRow("", "")
                    }
                }

                is SpinMode -> {
                    ChoreSelectModeHeaderRow("", "")
                }
            }

            LazyColumn {
                when (mode) {
                    is QuestMode -> {
                        itemsIndexed(uiState.rooms) { index, room ->
                            val isSelected = viewModel.isRoomSelected(room.id)
                            ListItem(
                                headlineContent = {
                                    Text(
                                        room.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                trailingContent = {
                                    Checkbox(checked = isSelected, onCheckedChange = { viewModel.selectRoom(room) })
                                },
                            )
                            if (index < uiState.rooms.lastIndex)
                                Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                        }
                    }
                    else -> {
                        if (mode != SandMode || (mode == SandMode && uiTimerState.showGuided)){
                            itemsIndexed(uiState.selectedChores) { index, choreId ->
                                val title = viewModel.getChoreTitle(choreId)
                                val isDistinct = viewModel.checkIsDistinct(choreId)
                                val existsInWorkflow = true // Default value for common parameter
                                val onErrorClick: (Int) -> Unit =
                                    { errorMessage -> viewModel.showItemErrorMessage(errorMessage, choreId) }

                                when (mode) {
                                    is BankMode -> ModeChoreListItem(
                                        title = title,
                                        isDistinct = isDistinct,
                                        existsInWorkflow = existsInWorkflow,
                                        onErrorClick = onErrorClick,
                                        bankValue = viewModel.getChoreBankModeValue(choreId)
                                    )

                                    is TimeMode -> ModeChoreListItem(
                                        title = title,
                                        isDistinct = isDistinct,
                                        existsInWorkflow = existsInWorkflow,
                                        onErrorClick = onErrorClick,
                                        timerValue = viewModel.getChoreTimeModeValue(choreId),
                                        timeUnit = viewModel.getChoreTimeUnit(choreId)
                                    )

                                    is SpinMode, is SandMode -> ModeChoreListItem(
                                        title = title,
                                        isDistinct = isDistinct,
                                        existsInWorkflow = existsInWorkflow,
                                        onErrorClick = onErrorClick
                                    )

                                    is QuestMode -> {

                                    }
                                }

                                if (index < uiState.selectedChores.lastIndex)
                                    Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                            }

                            item {
                                AddChoreButton(
                                    onClick = { onAddChoreClick(uiState.selectedChores) },
                                    uiState.selectedChores.count()
                                )
                            }

                            if(uiState.choresFromWorkflow.isNotEmpty()){
                                item {
                                    Column(
                                        Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        LabelText(stringResource(R.string.chore_source))
                                    }
                                }
                            }

                            itemsIndexed(uiState.choresFromWorkflow) { index, choreId ->
                                val title = viewModel.getChoreTitle(choreId)
                                val isDistinct = true
                                val existsInWorkflow = viewModel.checkExistInWorkflow(choreId)
                                val onErrorClick: (Int) -> Unit =
                                    { errorMessage -> viewModel.showItemErrorMessage(errorMessage, choreId) }

                                when (mode) {
                                    is BankMode -> ModeChoreListItem(
                                        title = title,
                                        isDistinct = isDistinct,
                                        existsInWorkflow = existsInWorkflow,
                                        onErrorClick = onErrorClick,
                                        bankValue = viewModel.getChoreBankModeValue(choreId)
                                    )

                                    is TimeMode -> ModeChoreListItem(
                                        title = title,
                                        isDistinct = isDistinct,
                                        existsInWorkflow = existsInWorkflow,
                                        onErrorClick = onErrorClick,
                                        timerValue = viewModel.getChoreTimeModeValue(choreId),
                                        timeUnit = viewModel.getChoreTimeUnit(choreId)
                                    )

                                    is SpinMode, is SandMode -> ModeChoreListItem(
                                        title = title,
                                        isDistinct = isDistinct,
                                        existsInWorkflow = existsInWorkflow,
                                        onErrorClick = onErrorClick
                                    )
                                    is QuestMode -> {

                                    }
                                }
                                if (index < uiState.choresFromWorkflow.lastIndex)
                                    Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                            }
                        }
                    }
                }
            }

            uiState.userMessage?.let { userMessage ->
                val snackbarText = stringResource(userMessage)
                LaunchedEffect(scope, viewModel, userMessage, snackbarText) {
                    when (mode) {
                        is BankMode, is TimeMode -> {
                            if (userMessage == R.string.chore_bank_mode_not_active || userMessage == R.string.chore_timer_mode_not_active) {
                                snackbarHostState.showSnackbar(
                                    message = snackbarText,
                                    duration = SnackbarDuration.Short,
                                    actionLabel = "Change"
                                ).let { result ->
                                    if (result == SnackbarResult.ActionPerformed) {
                                        uiState.choreItemError?.let { choreItem ->
                                            onEditChore(choreItem)
                                        }
                                    }
                                }
                            }
                        }
                        else -> {

                        }
                    }
                    if (userMessage == R.string.chore_repeat || userMessage == R.string.chore_not_found_workflow) {
                        snackbarHostState.showSnackbar(
                            message = snackbarText,
                            duration = SnackbarDuration.Short,
                            actionLabel = "Remove"
                        ).let { result ->
                            if (result == SnackbarResult.ActionPerformed) {
                                uiState.choreItemError?.let { choreItem ->
                                    viewModel.removeDuplicateItem(choreItem)
                                }
                            }
                        }
                    }
                    viewModel.itemErrorMessageShown()
                }
            }
        }
    }

    selectedItems.value?.let {
        LaunchedEffect(it) {
            viewModel.selectedItems(it)
        }
    }
}

fun checkSandTimerValue(hourValue: String, minuteValue: String, secondValue: String): Boolean{
    return (hourValue.isNotEmpty() && hourValue != "0" && hourValue != "00" ||
            minuteValue.isNotEmpty() && minuteValue != "0" && minuteValue != "00" ||
            secondValue.isNotEmpty() && secondValue != "0" && secondValue != "00")
}

@Composable
fun checkNoErrors(mode: ScoddMode, selectedChores: List<String>, choresFromWorkflow: List<String>, viewModel: ModesViewModel): Boolean{
    if(selectedChores.isEmpty() && choresFromWorkflow.isEmpty() ){
        return false
    }
    selectedChores.forEach { choreId ->
        val isDistinct = viewModel.checkIsDistinct(choreId)
        if(!isDistinct){
            return false
        }
        when(mode){
            BankMode -> {
                if(viewModel.getChoreBankModeValue(choreId) < 0){
                    return false
                }
            }
            TimeMode -> {
                if(viewModel.getChoreTimeModeValue(choreId) < 0 ){
                    return false
                }
            }
            else -> return true
        }
    }

    choresFromWorkflow.forEach { choreId ->
        val existsInWorkflow = viewModel.checkExistInWorkflow(choreId)
        if(!existsInWorkflow){
            return false
        }
        when(mode){
            BankMode -> {
                if(viewModel.getChoreBankModeValue(choreId) < 0){
                    return false
                }
            }
            TimeMode -> {
                if(viewModel.getChoreTimeModeValue(choreId) < 0 ){
                    return false
                }
            }
            else -> return true
        }
    }
    return true
}

@Composable
fun TimerTextField(
    hrValue : String,
    onHrValueChange:(String) -> Unit,
    minValue : String,
    onMinValueChange:(String) -> Unit,
    secValue : String,
    onSecValueChange:(String) -> Unit,
){
    val focusManager = LocalFocusManager.current
    val width = 101.dp
    val colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.Transparent,
        focusedBorderColor = Color.Transparent)
    val numberStyle = MaterialTheme.typography.displaySmall
    val labelStyle = MaterialTheme.typography.displaySmall

    Row(
        modifier = Modifier.padding(top = 0.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = hrValue,
            onValueChange = {
                onHrValueChange(it)
            },
            placeholder = {
                Text(stringResource(R.string.sand_time_placeholder), style = numberStyle)
            },
            suffix = {
                Text(stringResource(R.string.sand_picker_hr_label), style = labelStyle)
            },
            modifier = Modifier.width(width),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            colors = colors,
            textStyle = numberStyle
        )
        OutlinedTextField(
            value = minValue,
            onValueChange = {
                onMinValueChange(it)
            },
            placeholder = {
                Text(stringResource(R.string.sand_time_placeholder), style = numberStyle)
            },
            suffix = {
                Text(stringResource(R.string.sand_picker_min_label), style = labelStyle)
            },
            modifier = Modifier.width(width),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            colors = colors,
            textStyle = numberStyle
        )
        OutlinedTextField(
            value = secValue,
            onValueChange = {
                onSecValueChange(it)
            },
            placeholder = {
                Text(stringResource(R.string.sand_time_placeholder), style = numberStyle)
            },
            suffix = {
                Text(stringResource(R.string.sand_picker_sec_label), style = labelStyle)
            },
            modifier = Modifier.width(width),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            colors = colors,
            textStyle = numberStyle
        )
    }

}

@Composable
private fun createTimeString(value: String, pluralResId: Int, singularResId: Int): String {
    return if (value.isNotBlank()) {
        val valueInt = value.toInt()
        val unitString = stringResource(if (valueInt > 1) pluralResId else singularResId)
        "$value $unitString"
    } else {
        ""
    }
}

@Composable
fun getTimerText(hourValue: String, minuteValue: String, secondValue: String): String{
    val hrTimeString =
        createTimeString(hourValue, R.string.sand_time_hr_plural, R.string.sand_time_hr)
    val minTimeString =
        createTimeString(minuteValue,R.string.sand_time_min_plural,R.string.sand_time_min)
    val secTimeString =
        createTimeString(secondValue, R.string.sand_time_sec_plural, R.string.sand_time_sec)

    return buildString {
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
}