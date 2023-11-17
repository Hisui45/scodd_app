package com.example.scodd.ui.chore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.scodd.*
import com.example.scodd.R
import com.example.scodd.model.Workflow
import com.example.scodd.ui.theme.White40
import com.example.scodd.ui.components.*
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.*


@Composable
fun ChoreScreen(
    onCreateWorkflowClick : () -> Unit,
    onCreateChoreClick : () -> Unit,
    onEditChore : (String) -> Unit,
    onViewWorkflow : (String) -> Unit,
    viewModel: ChoreViewModel = hiltViewModel()
) {
    StatusBar(White40)
    val isVisible = rememberSaveable { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Hide FAB
                if (available.y < -1) {
                    isVisible.value = false
                }

                // Show FAB
                if (available.y > 1) {
                    isVisible.value = true
                }

                return Offset.Zero
            }
        }
    }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
            floatingActionButton = {
                AnimatedVisibility(
                    visible = isVisible.value,
                    enter = slideInVertically(initialOffsetY = { it * 2 }),
                    exit = slideOutVertically(targetOffsetY = { it * 2 }),
                ) {
                    CustomFloatingActionButton(true,
                        topFloatingActionButton ={
                            ExpandableFABButtonItem(
                                onButtonClick = { onCreateWorkflowClick()},
                                title = stringResource(R.string.workflow_title),
                                Icons.Default.List
                            )
                           },
                        bottomFloatingActionButton = {
                            ExpandableFABButtonItem(
                                onButtonClick = {onCreateChoreClick()},
                                title = stringResource(R.string.chore_title),
                                Icons.Default.Email
                            )
                            },
                        Icons.Default.Add)
                }
            },

        ) {
            val uiState by viewModel.uiState.collectAsState()
            var selectionActive = false
            var isInSelectionMode = remember { mutableStateOf(false) }
            val selectedItems = remember { mutableStateListOf<String>()}
            if(selectedItems.size == 0){ isInSelectionMode.value = false }

            Surface{
                Column(Modifier.padding(it)){
                    Search()
                    WorkflowContent(
                        workflows = uiState.workflows,
                        onCreateWorkflowClick = {onCreateWorkflowClick()},
                        onWorkflowClick = {onViewWorkflow(it)}
                    )
                    Divider()
                    ChoreContent(
                        roomChips = uiState.rooms,
                        nestedScrollConnection = nestedScrollConnection,
                        chores = uiState.items,
                        toggleChip = viewModel :: toggleRoom,
                        onFavoriteChipSelected = viewModel :: setFavoriteFilterType,
                        favoriteSelected = viewModel.getFavorite(),
                        onFavoriteChanged = viewModel::favoriteChore,
                        onChoreSelected = {onEditChore(it)},
                        getRoomTitle = viewModel :: getRoomTitle,
                        isSelectionActive = selectionActive,
                        isInSelectionMode = isInSelectionMode,
                        selectedItems = selectedItems
                    )
                }
            }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(){
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    val textColor = MaterialTheme.colorScheme.onSecondary
    SearchBar(
        modifier = Modifier.fillMaxWidth().padding(10.dp, 0.dp),
        colors = SearchBarDefaults.colors(MaterialTheme.colorScheme.secondaryContainer,
            inputFieldColors = TextFieldDefaults.colors(focusedPlaceholderColor = textColor,
                unfocusedPlaceholderColor = textColor, focusedTextColor = textColor,
                unfocusedTextColor = textColor)),
        query = text,
        onQueryChange = { text = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = {
//            active = it  Change this to show results on the screen
        },
        placeholder = { Text(stringResource(R.string.search_bar_placeholder)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer) },
        trailingIcon = {  },
        shape = RoundedCornerShape(size = 12.dp)

        ){
    }
}

@Composable
fun WorkflowContent(workflows : List<Workflow>, onCreateWorkflowClick : () -> Unit, onWorkflowClick: (String) -> Unit){
    Column(
        Modifier.padding(15.dp,8.dp, 0.dp, 6.dp)
    ) {
        LabelText(stringResource(R.string.workflow_label))
    }
    Row(Modifier.padding(bottom = 8.dp)){
        val colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            item {
                WorkflowTitleCard()
            }
            items(workflows){ workflow ->
                WorkflowCard(workflow.title,colors, onWorkflowClick = {
                    onWorkflowClick(workflow.id)
                } )
            }

            item{
                AddWorkflowCard(onCreateWorkflowClick, colors)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkflowCard(workflow : String, colors: CardColors, onWorkflowClick : () -> Unit){
    Card(
        modifier = Modifier.height(110.dp),
        shape = RoundedCornerShape(28.dp),
        colors = colors,
        onClick = {onWorkflowClick()}
    ){
        Column(
            Modifier.fillMaxHeight().padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(workflow,
                modifier =Modifier.defaultMinSize(130.dp),
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WorkflowTitleCard(){
    Card(
        modifier = Modifier.requiredWidth(189.dp).requiredHeight(110.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
    ){
        Column(
            Modifier.padding(16.dp)
        ){
            Text(stringResource(R.string.workflow_title), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.workflow_desc), style = MaterialTheme.typography.labelSmall)
        }

    }
}


