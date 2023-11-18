package com.example.scodd.ui.workflow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scodd.R
import com.example.scodd.model.ChoreItem
import com.example.scodd.model.Workflow
import com.example.scodd.ui.components.*
import com.example.scodd.ui.theme.Marigold40
import com.example.scodd.utils.LazyAnimations

/**
 * TODO: options: to clear completed priority: 5
 * TODO: add chores to workflow priority: 2
 * TODO: edit workflow title priority : 3
 * TODO: delete choreitem kinda
 */
@Composable
fun WorkflowScreen(
        selectedItems: State<List<String>>,
        onNavigateBack : () -> Unit,
        onAddChoreClick : (String, List<String>) -> Unit,
        viewModel: WorkflowViewModel = hiltViewModel()
){
    StatusBar(Marigold40)

    val uiState by viewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    var isLocked = remember { mutableStateOf(false) }
    var showDeleteDialog = remember { mutableStateOf(false) }

    val contentColor = MaterialTheme.colorScheme.onPrimary

    val workflow = uiState.workflow

    if(workflow != null){
        val title = remember { mutableStateOf(workflow.title) }
        Scaffold(
            topBar = {
                TextFieldTopBar(
                    title = title.value,
                    onNavigateBack = {
                        viewModel.updateTitle(title.value)
                        onNavigateBack()},
                    focusManager = LocalFocusManager.current,
                    type = TextFieldTopBarType.WORKFLOW,
                    contentColor = contentColor,
                    onTitleChanged = {title.value = it},
                    actions = {
                        val icon = if (workflow.isCheckList) Icons.Default.List else Icons.Default.Check
                        val contentDescription =
                            if (workflow.isCheckList) stringResource(R.string.workflow_view_button_contDesc) else stringResource(R.string.workflow_complete_button_contDesc)
                        if(!isLocked.value){
                            IconButton(onClick = viewModel::toggleListType){Icon(icon, contentDescription)}
                        }else{
                            /**
                             * TODO: icon with lock
                             */
                        }
                        WorkflowContextMenu(onAddClicked = {
                            /**
                             * TODO: add to round up
                             */
                        }, onDeleteClicked = {showDeleteDialog.value = true},
                            onLockClicked = {isLocked.value = !isLocked.value},
                            isLocked = isLocked.value,
                            tint = contentColor)
                    }

            ) },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState)  }
        ){
            Column(Modifier.padding(it)){
                WorkflowContent(
                    workflow = workflow,
                    onAddChoreClick = {onAddChoreClick(workflow.id, uiState.choreItems.map { choreItem -> choreItem.parentChoreId })},
                    choreItems = uiState.choreItems,
                    choreTitle = viewModel::getChoreTitle,
                    roomTitle = viewModel::getRoomTitle,
                    additionalAmount = viewModel::getAdditionalAmount,
                    completeItem = viewModel::setCompleted
                )

                LaunchedEffect(uiState.isWorkflowDeleted) {
                    if (uiState.isWorkflowDeleted) {
                        onNavigateBack()
                    }
                }
            }
            DeleteConfirmationDialog(onConfirm = {viewModel.deleteWorkflow();showDeleteDialog.value = false},
                onDismiss = {showDeleteDialog.value = false}, showDeleteDialog.value)
        }
    }



    uiState.userMessage?.let { userMessage ->
        val snackbarText = stringResource(userMessage)
        LaunchedEffect(scope, viewModel, userMessage, snackbarText) {
            snackbarHostState.showSnackbar(message = snackbarText, duration = SnackbarDuration.Short)
            viewModel.snackbarMessageShown()
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkflowContent(
    workflow : Workflow,
    onAddChoreClick: () -> Unit,
    choreItems: List<ChoreItem>,
    choreTitle: (String) -> String,
    roomTitle: (String, Int) -> String,
    additionalAmount: (String) -> Int,
    completeItem: (String, Boolean) -> Unit,){

    val focusManager =  LocalFocusManager.current
    val count = choreItems.count()
    Text(
        stringResource(R.string.chore_label) + ": $count",
        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.inverseSurface,
        modifier = Modifier.padding(start = 16.dp, top = 12.dp))
    LazyColumn{
        item(key = 0){Text("", Modifier.height(1.dp))}
        itemsIndexed(
            items = choreItems.sortedBy { it.isComplete },
            key = { _, item -> item.id }
        ){ index, choreItem ->
            val isSelected = remember { mutableStateOf(choreItem.isComplete) }
            ChoreListItem(
                firstRoom = roomTitle(choreItem.parentChoreId, 0),
                additionalAmount = additionalAmount(choreItem.parentChoreId),
                title = choreTitle(choreItem.parentChoreId),
                isComplete = choreItem.isComplete,
                onCheckChanged = {completeItem(choreItem.id, it)
                                 focusManager.clearFocus()},
                showCheckBox = workflow.isCheckList,
                animateModifier = Modifier.animateItemPlacement(LazyAnimations.WORKFLOW.animation)
            )
            if (index < choreItems.lastIndex)
                Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
        }
        item{
            AddChoreButton(onClick = {onAddChoreClick() }, choreItems.count())
        }
    }

}

@Composable
fun WorkflowContextMenu(
    onAddClicked : () -> Unit,
    onDeleteClicked: () -> Unit,
    onLockClicked: () -> Unit,
    isLocked: Boolean = false,
    tint: Color
){
    ContextMenu(
        tint = tint
    ) {
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.lock))
                    Spacer(Modifier.weight(1f))
                    if (isLocked) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.padding(end = 8.dp))
                    }
                }
            },
            onClick = { onLockClicked(); }
        )
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

