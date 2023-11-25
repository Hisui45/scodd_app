package com.example.scodd.ui.mode.sand

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scodd.R
import com.example.scodd.navigation.ModeBottomBar
import com.example.scodd.ui.theme.Marigold40
import com.example.scodd.ui.components.*
import com.example.scodd.utils.ADHD
import com.example.scodd.utils.Game


@Composable
fun SandModeScreen(
    selectedItems: State<List<String>?>,
    onNavigateBack: () -> Unit,
    onAddChoreClick : (List<String>) -> Unit,
    viewModel: SandModeViewModel = hiltViewModel()
){
    StatusBar(Marigold40)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val suggestions = listOf(ADHD, Game)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)  },
        bottomBar = {ModeBottomBar(onStartClick = {})}
    )
    {
        Column(Modifier.padding(it)) {
            val uiState by viewModel.uiState.collectAsState()
            val uiTimerState by viewModel.uiTimerState.collectAsState()

            val currentOption = if (uiTimerState.showGuided) stringResource(R.string.sand_option_two) else stringResource(R.string.sand_option_one)


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
                TextButton(onClick = {viewModel.switchShowGuided()},
                ){ Text(stringResource(R.string.button_switch))}
            }

            TimerTextField(
                hrValue = uiTimerState.hourValue,
                onHrValueChange = {newValue ->
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

            val timerText = getTimerText(uiTimerState.hourValue, uiTimerState.minuteValue, uiTimerState.secondValue)
            Text(
                stringResource(R.string.sand_time_label).plus(" $timerText"), style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 12.dp, top = 0.dp),
                fontSize = 17.sp)

            if(uiTimerState.showGuided){
                WorkflowSelectModeRow(
                    workflows = uiState.parentWorkflows,
                    isSelected = viewModel :: isWorkflowSelected,
                    onWorkflowSelect = viewModel :: selectWorkflow
                )
                ChoreSelectModeHeaderRow("", "")
                LazyColumn {
                    itemsIndexed(uiState.selectedChores){ index, choreId ->
                        val isDistinct = viewModel.checkIsDistinct(choreId)
                        ModeChoreListItem(
                            title = viewModel.getChoreTitle(choreId),
                            isDistinct = isDistinct,
                            existsInWorkflow = true,
                            onErrorClick = {errorMessage ->
                                viewModel.showItemErrorMessage(errorMessage, choreId)
                            }
                        )
                        if (index < uiState.selectedChores.lastIndex)
                            Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                    }
                    item{
                        AddChoreButton(onClick = {onAddChoreClick(uiState.selectedChores)}, uiState.selectedChores.count())
                    }
                    if(uiState.choresFromWorkflow.isNotEmpty()){
                        item{
                            Column(
                                Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ){
                                LabelText(stringResource(R.string.chore_source))
                            }

                        }
                    }
                    itemsIndexed(uiState.choresFromWorkflow){ index, choreId ->
                        val isDistinct = viewModel.checkIsDistinct(choreId)
                        val exists = viewModel.checkExistInWorkflow(choreId)
                        ModeChoreListItem(
                            title = viewModel.getChoreTitle(choreId),
                            isDistinct = isDistinct,
                            existsInWorkflow = exists,
                            onErrorClick = {errorMessage ->
                                viewModel.showItemErrorMessage(errorMessage, choreId)
                            }
                        )
                        if (index < uiState.choresFromWorkflow.lastIndex)
                            Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                    }
                }
            }

            uiState.userMessage?.let { userMessage ->
                val snackbarText = stringResource(userMessage)
                LaunchedEffect(scope, viewModel, userMessage, snackbarText) {
                    snackbarHostState.showSnackbar(
                        message = snackbarText,
                        duration = SnackbarDuration.Short,
                        actionLabel = "Remove"
                    ).let { result ->
                        if(result == SnackbarResult.ActionPerformed){
                            uiState.choreItemError?.let { choreItem ->
                                viewModel.removeDuplicateItem(choreItem)
//                                viewModel.itemErrorMessageShown()
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
        TextField(
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
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions { focusManager.clearFocus() },
            colors = colors,
            textStyle = numberStyle
        )
        TextField(
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
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions { focusManager.clearFocus() },
            colors = colors,
            textStyle = numberStyle
        )
        TextField(
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
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions { focusManager.clearFocus() },
            colors = colors,
            textStyle = numberStyle
        )
    }

}

@Composable
private fun createTimeString(value: String, pluralResId: Int, singularResId: Int): String {
    return if (value.isNotEmpty()) {
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


