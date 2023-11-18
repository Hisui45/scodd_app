package com.example.scodd.ui.mode.time

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scodd.R
import com.example.scodd.ui.theme.Marigold40
import com.example.scodd.ui.components.*
import com.example.scodd.utils.ADHD
import com.example.scodd.utils.Game
import com.example.scodd.utils.Motivation

@Composable
fun TimeModeScreen(
    selectedItems: State<List<String>>,
    onNavigateBack: () -> Unit,
//    onAddChoreToModeClick : (Workflow) -> Unit,
    onAddChoreClick : (List<String>) -> Unit,
    viewModel: TimeModeViewModel = hiltViewModel()
    ){
    val suggestions = listOf(ADHD, Game, Motivation)
    StatusBar(Marigold40)
    Column {
        ScoddModeHeader(onNavigateBack,"Time Crunch",
            "The ultimate solution for those seeking" +
                " a fun and motivating way to complete household tasks efficiently. This mode transforms the mundane " +
                "into the thrilling by using a specific time limit to each chore in your workflow. It's like turning " +
                "your cleaning routine into a high-stakes game where you race against the clock to conquer clutter " +
                "and chaos.",
            suggestions
        )

        val uiState by viewModel.uiState.collectAsState()

        WorkflowSelectModeRow(
            workflows = uiState.parentWorkflows,
            isSelected = viewModel :: isWorkflowSelected,
            onWorkflowSelect = viewModel :: selectWorkflow
        )
        var estimatedTime = viewModel.calculateEstimatedTime()
        ChoreSelectModeHeaderRow("Estimated Time:", "$estimatedTime minutes")
        LazyColumn {
            item(key = 0){
                Text("")
            }
            itemsIndexed(uiState.selectedChores){ index, choreId ->
                val timerValue = viewModel.getChoreTimeModeValue(choreId)
                val timeUnit = viewModel.getChoreTimeUnit(choreId)
                ModeChoreListItem(viewModel.getChoreTitle(choreId), trailingContent = {
                    if(timerValue > 0){
                        LabelText("$timerValue $timeUnit")
                    }else{
                        Icon(Icons.Default.Warning, stringResource(R.string.chore_no_value), tint = MaterialTheme.colorScheme.error)
                    }

                })
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
                val timerValue = viewModel.getChoreTimeModeValue(choreId)
                val timeUnit = viewModel.getChoreTimeUnit(choreId)
                ModeChoreListItem(viewModel.getChoreTitle(choreId), trailingContent = {
                    if(timerValue > 0){
                        LabelText("$timerValue $timeUnit")
                    }else{
                        Icon(Icons.Default.Warning, stringResource(R.string.chore_no_value), tint = MaterialTheme.colorScheme.error)
                    }

                })
                if (index < uiState.choresFromWorkflow.lastIndex)
                    Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
            }


        }

    }

    var addedItems by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        if (!addedItems) {
            viewModel.selectedItems(selectedItems.value)
            addedItems = true
        }
    }
}


