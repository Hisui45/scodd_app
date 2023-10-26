package com.example.scodd.chore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.scodd.*
import com.example.scodd.R
import com.example.scodd.components.AddWorkflowCard
import com.example.scodd.components.CustomFloatingActionButton
import com.example.scodd.components.LabelText
import com.example.scodd.components.FilterChip
import com.example.scodd.objects.ScoddChore
import com.example.scodd.objects.scoddChores
import com.example.scodd.objects.scoddFlows
import com.example.scodd.objects.scoddRooms


@Composable
fun ChoreScreen(
    onCreateWorkflowClick : () -> Unit,
    onCreateChoreClick : () -> Unit,

) {
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
                        onWorkflowFabClick ={onCreateWorkflowClick()},
                        onChoreFabClick = {onCreateChoreClick()},
                        Icons.Default.Add)
                }
            }
        ) {
            Column(Modifier.padding(it)){
                Search()
                Workflow(onCreateWorkflowClick)
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

    SearchBar(
        modifier = Modifier.fillMaxWidth().padding(10.dp, 0.dp),
        colors = SearchBarDefaults.colors(MaterialTheme.colorScheme.secondaryContainer),
        query = text,
        onQueryChange = { text = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = {
//            active = it  Change this to show results on the screen
        },
        placeholder = { Text("Find Chore") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer) },
        trailingIcon = {  },
        shape = RoundedCornerShape(size = 12.dp)

        ){
    }
}

@Composable
fun Workflow(onCreateWorkflowClick : () -> Unit){
    Column(
        Modifier.padding(15.dp,8.dp, 0.dp, 0.dp)
    ) {
        LabelText("Workflows")
        Row(Modifier.padding(0.dp, 6.dp)){
            WorkflowRow(onCreateWorkflowClick)
        }
    }
}

@Composable
fun WorkflowRow(onCreateWorkflowClick : () -> Unit){
    val colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(end = 15.dp)
    ) {
        item {
            WorkflowTitleCard()
        }
        items(scoddFlows){ workflow ->
            WorkflowCard(workflow.title)
        }

        item{
            AddWorkflowCard(onCreateWorkflowClick, colors)
        }
    }
}

@Composable
fun WorkflowCard(workflow : String){
    Card(
        modifier = Modifier.width(120.dp).height(110.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
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
            Text("Workflow", style = MaterialTheme.typography.headlineMedium)
            Text("Collection of chores routinely done together.", style = MaterialTheme.typography.labelSmall)
        }

    }
}

@Composable
fun Chore(nestedScrollConnection : NestedScrollConnection){
    Column(
        Modifier.padding(15.dp,8.dp, 15.dp, 8.dp)
    ) {
        LabelText("Chores")
        ChoreFilters()
        ChoreGrid(nestedScrollConnection)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChoreFilters(){
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        scoddRooms.forEach { room ->
            var selected = remember { mutableStateOf(room.selected) }
            FilterChip(room.title,
                selected,
                onClick = {}
            )

        }
    }
}

@Composable
fun ChoreCard(chore : ScoddChore){
    Card(
        Modifier.size(175.dp).padding(0.dp, 8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer,MaterialTheme.colorScheme.onSecondaryContainer)
    ){
        Column(
            Modifier.padding(12.dp, 0.dp, 0.dp, 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                val resource = if (chore.favorites) R.drawable.filled_star_24 else R.drawable.star_24
                Text(chore.room.title,
                    modifier = Modifier.width(120.dp).height(30.dp),
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Light)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {}){
                    Icon(painterResource(id = resource)
                        , "favorites", tint = MaterialTheme.colorScheme.outline)
                }
            }
            Spacer(Modifier.weight(1f))
            Text(chore.title,
                textAlign = TextAlign.End,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp), fontWeight = FontWeight.Light, style = MaterialTheme.typography.titleLarge)
        }

    }
}

@Composable
fun ChoreGrid(nestedScrollConnection : NestedScrollConnection){
    LazyVerticalGrid(columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier.nestedScroll(nestedScrollConnection)){
        items(scoddChores){ chore ->
            ChoreCard(chore)
        }
    }
}

