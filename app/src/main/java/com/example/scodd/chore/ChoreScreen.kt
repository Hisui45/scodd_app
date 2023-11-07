package com.example.scodd.chore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.scodd.*
import com.example.scodd.R
import com.example.scodd.components.*
import com.example.scodd.objects.ScoddWorkflow
import com.example.scodd.objects.scoddChores
import com.example.scodd.objects.scoddFlows
import com.example.scodd.objects.scoddRooms
import com.example.scodd.ui.theme.White40


@Composable
fun ChoreScreen(
    onCreateWorkflowClick : () -> Unit,
    onCreateChoreClick : () -> Unit,
    onWorkflowClick: (ScoddWorkflow) -> Unit

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
            }
        ) {
            Column(Modifier.padding(it)){
                Search()
                Workflow(onCreateWorkflowClick, onWorkflowClick)
                Divider()
                Chore(nestedScrollConnection)
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
fun Workflow(onCreateWorkflowClick : () -> Unit, onWorkflowClick: (ScoddWorkflow) -> Unit){
    Column(
        Modifier.padding(15.dp,8.dp, 0.dp, 0.dp)
    ) {
        LabelText(stringResource(R.string.workflow_label))
    }
    Row(Modifier.padding(0.dp, 6.dp)){
        val colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            item {
                WorkflowTitleCard()
            }
            items(scoddFlows){ workflow ->
                WorkflowCard(workflow.title,colors, onWorkflowClick = {
                    onWorkflowClick(workflow)
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
        modifier = Modifier.width(170.dp).height(110.dp),
        shape = RoundedCornerShape(28.dp),
        colors = colors,
        onClick = {onWorkflowClick()}
    ){
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(workflow,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp))
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

@Composable
fun Chore(nestedScrollConnection : NestedScrollConnection){
    Column(
        Modifier.padding(15.dp,8.dp, 15.dp, 8.dp)
    ) {
        LabelText(stringResource(R.string.chore_label))
    }
    ChoreView(scoddRooms, onChipSelected = {}, chores = scoddChores, onChoreSelected = {}, nestedScrollConnection = nestedScrollConnection  )

}
