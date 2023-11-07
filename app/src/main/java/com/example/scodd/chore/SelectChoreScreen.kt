package com.example.scodd.chore

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.scodd.R
import com.example.scodd.components.*
import com.example.scodd.objects.ScoddChore
import com.example.scodd.objects.scoddChores
import com.example.scodd.objects.scoddRooms
import com.example.scodd.ui.theme.Marigold40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectChoreScreen(onSelectFinish : (List<ScoddChore>) -> Unit,
                      onNavigateBack: () -> Unit,
                      navigateToChoreCreate : () -> Unit){
    StatusBar(Marigold40)
    val createChoreDialog = remember { mutableStateOf(false) }
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
                                Icons.Default.List
                            )
                        },
                        bottomFloatingActionButton = {
                            ExpandableFABButtonItem(
                                onButtonClick = {
                                    createChoreDialog.value = true
                                },
                                title = stringResource(R.string.fab_quick_create),
                                Icons.Default.List
                            )

                        },
                        Icons.Default.Add)
                }
            }
        ) {
            Column(Modifier.padding(it)){
                    val contentColor =  MaterialTheme.colorScheme.onPrimary
                    val selectedCount = 0
                    val selected = remember { mutableStateOf(false) }
                    val query = ""
                    TopAppBar(
                        title = { Text(stringResource(R.string.select_chore_title)) },
                        navigationIcon = { NavigationButton(onNavigateBack) },
                        actions = {
                            IconButton(
                                onClick = {onSelectFinish(emptyList())}
                            ){
                                Icon(Icons.Default.Check, stringResource(R.string.check_button_contDesc))
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary,
                            navigationIconContentColor = contentColor,
                            titleContentColor = contentColor,
                            actionIconContentColor = contentColor
                        )
                    )
                    Row(
                        modifier = Modifier.padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        LabelText("$selectedCount " + stringResource(R.string.selected_label))
                        Spacer(Modifier.weight(1f))
                        LabelText(stringResource(R.string.select_all_label))
                        RadioButton(
                            selected = selected.value,
                            onClick = {selected.value = !selected.value},
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.secondary)
                        )
                    }
                    var text by rememberSaveable { mutableStateOf("") }
                    var active by rememberSaveable { mutableStateOf(false) }
                    val textColor = MaterialTheme.colorScheme.onSecondary
                    SearchBar(
                        modifier = Modifier.fillMaxWidth().padding(10.dp, 8.dp),
//                    colors = SearchBarDefaults.colors(MaterialTheme.colorScheme.secondaryContainer,
//                        inputFieldColors = TextFieldDefaults.colors(focusedPlaceholderColor = textColor,
//                            unfocusedPlaceholderColor = textColor, focusedTextColor = textColor,
//                            unfocusedTextColor = textColor)),
                        query = text,
                        onQueryChange = { text = it },
                        onSearch = { active = false },
                        active = active,
                        onActiveChange = {
//            active = it  Change this to show results on the screen
                        },
                        placeholder = { Text(stringResource(R.string.search_bar_placeholder)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null,
//                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )},
                        trailingIcon = {  },
                        shape = RoundedCornerShape(size = 12.dp)

                    ){

                    }
                ChoreView(scoddRooms, onChipSelected = {}, chores = scoddChores, onChoreSelected = {}, nestedScrollConnection = nestedScrollConnection  )

                CreateDialog(
                    stringResource(R.string.dialog_quick_title),
                    onDismissRequest = {
                        createChoreDialog.value = false
                    },
                    onCreateClick = {
                        createChoreDialog.value = false
                        //Push to data model to create new object
                    },
                    createChoreDialog)
            }
        }

    }

}
