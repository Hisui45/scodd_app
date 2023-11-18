package com.example.scodd.ui.mode.bank

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
import com.example.scodd.data.scoddChores
import com.example.scodd.model.*
import com.example.scodd.ui.theme.Marigold40
import com.example.scodd.ui.components.*
import com.example.scodd.utils.ADHD
import com.example.scodd.utils.Game


@Composable
fun BankModeScreen(
    selectedItems: State<List<String>>,
    onNavigateBack: () -> Unit,
//    onAddChoreToModeClick : (Workflow) -> Unit,
    onAddChoreClick : (List<String>) -> Unit,
    viewModel: BankModeViewModel = hiltViewModel()
){
    StatusBar(Marigold40)
    val suggestions = listOf(ADHD, Game)
    Column {
        ScoddModeHeader(onNavigateBack,"Piggy Bank", "Adds an exciting twist to your daily routine." +
                " It associates a virtual cash value with each chore on your to-do list. As you successfully" +
                " complete your chores, you'll see your \"Piggy Bank\" grow. It's like granting yourself a" +
                " personal budget for guilt-free indulgence in treats and small luxuries as a well-deserved" +
                " reward, all while maintaining a sense of accomplishment.",suggestions)

        val uiState by viewModel.uiState.collectAsState()


        WorkflowSelectModeRow(
            workflows = uiState.parentWorkflows,
            isSelected = viewModel :: isWorkflowSelected,
            onWorkflowSelect = viewModel :: selectWorkflow
        )

        var potentialPayout = viewModel.calculatePotentialPayout()

        ChoreSelectModeHeaderRow("Potential Payout:", "$$potentialPayout")
        LazyColumn {
            item(key = 0){
                Text("")
            }
            itemsIndexed(uiState.selectedChores){ index, choreId ->
                val bankValue = viewModel.getChoreBankModeValue(choreId)
                ModeChoreListItem(viewModel.getChoreTitle(choreId), trailingContent = {
                    if(bankValue > 0){
                        LabelText("$$bankValue")
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
                val bankValue = viewModel.getChoreBankModeValue(choreId)
                ModeChoreListItem(viewModel.getChoreTitle(choreId), trailingContent = {
                    if(bankValue > 0){
                        LabelText("$$bankValue")
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