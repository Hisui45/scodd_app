package com.example.scodd.ui.mode.bank

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scodd.R
import com.example.scodd.navigation.ModeBottomBar
import com.example.scodd.ui.theme.Marigold40
import com.example.scodd.ui.components.*
import com.example.scodd.utils.ADHD
import com.example.scodd.utils.Game
import com.example.scodd.utils.Motivation

@Composable
fun BankModeScreen(
    selectedItems: State<List<String>?>,
    onNavigateBack: () -> Unit,
    onAddChoreClick : (List<String>) -> Unit,
    onEditChore: (String) -> Unit,
    viewModel: BankModeViewModel = hiltViewModel()
){

    val suggestions = listOf(ADHD, Game, Motivation)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    StatusBar(Marigold40)
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)  },
        bottomBar = {ModeBottomBar(onStartClick = {})}
    ){
        Column(Modifier.padding(it)) {
            ScoddModeHeader(onNavigateBack,
                stringResource(R.string.bank_mode_title),
                stringResource(R.string.bank_mode_desc) ,
                suggestions)

            val uiState by viewModel.uiState.collectAsState()

            WorkflowSelectModeRow(
                workflows = uiState.parentWorkflows,
                isSelected = viewModel :: isWorkflowSelected,
                onWorkflowSelect = viewModel :: selectWorkflow
            )
            val potentialPayout = viewModel.calculatePotentialPayout()
            ChoreSelectModeHeaderRow("Potential Payout:", "$$potentialPayout")
            LazyColumn {
                itemsIndexed(uiState.selectedChores){ index, choreId ->
                    val bankValue = viewModel.getChoreBankModeValue(choreId)
                    val isDistinct = viewModel.checkIsDistinct(choreId)
                    BankModeChoreListItem(
                        title = viewModel.getChoreTitle(choreId),
                        bankValue = bankValue,
                        isDistinct = isDistinct,
                        existsInWorkflow = true,
                        onErrorClick = { errorMessage ->
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
                    val bankValue = viewModel.getChoreBankModeValue(choreId)
                    val isDistinct = viewModel.checkIsDistinct(choreId)
                    val exists = viewModel.checkExistInWorkflow(choreId)
                    BankModeChoreListItem(
                        title = viewModel.getChoreTitle(choreId),
                        bankValue = bankValue,
                        isDistinct = isDistinct,
                        existsInWorkflow = exists,
                        onErrorClick = { errorMessage ->
                            viewModel.showItemErrorMessage(errorMessage, choreId)
                        }
                    )
                    if (index < uiState.choresFromWorkflow.lastIndex)
                        Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                }
            }

            uiState.userMessage?.let { userMessage ->
                val snackbarText = stringResource(userMessage)
                LaunchedEffect(scope, viewModel, userMessage, snackbarText) {
                    if(userMessage == R.string.chore_bank_mode_not_active){
                        snackbarHostState.showSnackbar(
                            message = snackbarText,
                            duration = SnackbarDuration.Short,
                            actionLabel = "Edit"
                        ).let { result ->
                            if(result == SnackbarResult.ActionPerformed){
                                uiState.choreItemError?.let { choreItem ->
                                    onEditChore(choreItem)
                                }
                            }
                        }
                    }else if(userMessage == R.string.chore_repeat || userMessage == R.string.chore_not_found_workflow){
                        snackbarHostState.showSnackbar(
                            message = snackbarText,
                            duration = SnackbarDuration.Short,
                            actionLabel = "Remove"
                        ).let { result ->
                            if(result == SnackbarResult.ActionPerformed){
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
