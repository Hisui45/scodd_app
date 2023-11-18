package com.example.scodd.ui.workflow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scodd.R
import com.example.scodd.model.Chore
import com.example.scodd.ui.components.AddChoreButton
import com.example.scodd.ui.components.TextFieldTopBar
import com.example.scodd.ui.components.StatusBar
import com.example.scodd.ui.components.TextFieldTopBarType
import com.example.scodd.ui.theme.Marigold40
import com.example.scodd.utils.LazyAnimations

@Composable
fun CreateWorkflowScreen(
    selectedItems: State<List<String>>,
    onAddChoreButtonClick: (List<String>) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CreateWorkflowViewModel = hiltViewModel()
){
    StatusBar(Marigold40) // Temporary
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val createTypeWorkflow = 1

    /**
     * TODO: use interface for colors
     */
    val contentColor = MaterialTheme.colorScheme.onPrimary
    Scaffold(
        topBar = {
            TextFieldTopBar(
                onNavigateBack = onNavigateBack,
                focusManager = focusManager,
                title = uiState.title,
                type = TextFieldTopBarType.WORKFLOW,
                contentColor = contentColor,
                onTitleChanged = viewModel :: updateTitle,
                actions = { TextButton(onClick = viewModel :: saveWorkflow){Text(stringResource(R.string.save_buttton), color = contentColor)}
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)  }
    ) {
        Column(Modifier.padding(it)) {
            WorkflowContent(
                onAddChoreButtonClick = {onAddChoreButtonClick(uiState.parentChores.map { parentChoreId -> parentChoreId.id})},
                onDeleteButtonClick = viewModel :: deleteChore,
                chores = uiState.parentChores,
            )
        }
        LaunchedEffect(uiState.isWorkflowSaved) {
            if (uiState.isWorkflowSaved) {
                onNavigateBack()
            }
        }

        uiState.userMessage?.let { userMessage ->
            val snackbarText = stringResource(userMessage)
            LaunchedEffect(scope, viewModel, userMessage, snackbarText) {
                snackbarHostState.showSnackbar(message = snackbarText, duration = SnackbarDuration.Short)
                viewModel.snackbarMessageShown()
            }
        }
    }

    if(selectedItems.value.isNotEmpty()) run {
        viewModel.selectedItems(selectedItems.value)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkflowContent(
    onAddChoreButtonClick: () -> Unit,
    onDeleteButtonClick: (Chore) -> Unit,
    chores: List<Chore>,
){
    if(chores.isNotEmpty()){
            val count = chores.count()
            Text(
                stringResource(R.string.chore_label) + ": $count",
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.inverseSurface,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
            )
            LazyColumn {
                itemsIndexed(
                    items = chores,
                    key = { _, item -> item.id }
                    ) {index, chore ->
                    Row(Modifier.padding(vertical = 2.dp, horizontal = 16.dp)
                        .animateItemPlacement(LazyAnimations.CREATE_WORKFLOW.animation),
                        verticalAlignment = Alignment.CenterVertically){
                       Text(chore.title, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = {onDeleteButtonClick(chore)},
                        ){
                            Icon(Icons.Default.Delete, "remove")
                        }
                    }
                    if (index < chores.lastIndex)
                        Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                }
                item {
                    AddChoreButton(onClick = {onAddChoreButtonClick()}, chores.count()) //Should create a temp object and pass it through
                }
            }

    }else{
        Column(
            modifier = Modifier.fillMaxHeight().padding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.instructions_text))
            AddChoreButton(onClick = { onAddChoreButtonClick() }, 0) //Should create a temp object and pass it through
        }
    }
}
        //          Check for user messages to display on the screen