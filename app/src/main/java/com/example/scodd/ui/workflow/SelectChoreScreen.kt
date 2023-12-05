package com.example.scodd.ui.workflow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scodd.R
import com.example.scodd.ui.theme.Marigold40
import com.example.scodd.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectChoreScreen(
        incomingSelectedChores: List<String>,
        workflowId: String?,
        onSelectFinish : (List<String>) -> Unit,
        onNavigateBack: () -> Unit,
        navigateToChoreCreate : () -> Unit,
        viewModel: SelectChoreViewModel = hiltViewModel()
                      ){
    StatusBar(Marigold40)
    val isVisible = rememberSaveable { mutableStateOf(true) }
    val createChoreDialog = remember { mutableStateOf(false) }
    val contentColor =  MaterialTheme.colorScheme.onPrimary

    val uiState by viewModel.uiState.collectAsState()
    var selectionActive = true
    var isInSelectionMode = remember { mutableStateOf(true) }
    val selectedItems = remember { mutableStateListOf<String>() }

    val isSelectAll = selectedItems.containsAll(uiState.items.map { it.id })

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
    Surface{
        Scaffold(
            floatingActionButton = {
                AnimatedVisibility(
                    visible = isVisible.value,
                    enter = slideInVertically(initialOffsetY = { it * 2 }),
                    exit = slideOutVertically(targetOffsetY = { it * 2 }),
                ) {
                    CustomFloatingActionButton(true,
                        topFloatingActionButton ={
                            ExpandableFABButtonItem(
                                onButtonClick = {
                                    navigateToChoreCreate()
                                },
                                title = stringResource(R.string.fab_create_chore),
                                painterResource(R.drawable.ic_eggs)
                            )
                        },
                        bottomFloatingActionButton = {
                            ExpandableFABButtonItem(
                                onButtonClick = {
                                    createChoreDialog.value = true
                                },
                                title = stringResource(R.string.fab_quick_create),
                                painterResource(R.drawable.ic_egg)
                            )

                        },
                        Icons.Default.Add)
                }
            },
            topBar = {TopAppBar(
                title = { Text(stringResource(R.string.select_chore_title)) },
                navigationIcon = { NavigationButton(onNavigateBack) },
                actions = {
                    if(selectedItems.isNotEmpty() || incomingSelectedChores.isNotEmpty()) {
                        IconButton(
                            onClick = { onSelectFinish(selectedItems.toMutableList()) }
                        ) {
                            Icon(Icons.Default.Check, stringResource(R.string.check_button_contDesc))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = contentColor,
                    titleContentColor = contentColor,
                    actionIconContentColor = contentColor
                )
            )}
        ) {
            Column(Modifier.padding(it)){

                SelectContent(selectedItems.count(),
                    onSelectAllClick = {
                        selectedItems.clear()
                        if (!isSelectAll) {
                            selectedItems.addAll(uiState.items.map { it.id })
                        }
                    },
                    selected = isSelectAll)

                ChoreContent(
                    roomChips = uiState.rooms,
                    nestedScrollConnection = nestedScrollConnection,
                    chores = uiState.items,
                    toggleChip = viewModel :: toggleRoom,
                    onFavoriteChipSelected = viewModel :: setFavoriteFilterType,
                    favoriteSelected = viewModel.getFavorite(),
                    onFavoriteChanged = viewModel::favoriteChore,
                    onChoreSelected = { choreId -> selectedItems.remove(choreId)},
                    getRoomTitle = viewModel :: getRoomTitle,
                    isSelectionActive = selectionActive,
                    isInSelectionMode = isInSelectionMode,
                    selectedItems = selectedItems
                )

                CreateDialog(stringResource(R.string.dialog_quick_title), onDismissRequest = {createChoreDialog.value = false},
                    onCreateClick = { title ->
                        viewModel.createChore(title)
                        createChoreDialog.value = false //Push to data model to create new object
                    },
                    createChoreDialog)

            }
        }

    }

    var addedItems by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        if (!addedItems) {
            selectedItems.addAll(incomingSelectedChores)
            addedItems = true
        }
    }
}

@Composable
/**
 * TODO: SEARCH BAR
 */
fun SelectContent(count: Int, onSelectAllClick: (Boolean) -> Unit, selected: Boolean){
    Row(
        modifier = Modifier.padding(start = 16.dp, top = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        LabelText("$count " + stringResource(R.string.selected_label))
        Spacer(Modifier.weight(1f))
        LabelText(stringResource(R.string.select_all_label))
        RadioButton(
            selected = selected,
            onClick = {onSelectAllClick(selected)},
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.secondary)
        )
    }
}
