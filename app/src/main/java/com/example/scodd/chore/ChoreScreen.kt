package com.example.scodd.chore

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.scodd.*
import com.example.scodd.R


@Composable
fun ChoreScreen() {
    Surface{
        Column{
            Search()
            Workflow()
            Divider()
            Chore()
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
        colors = SearchBarDefaults.colors(MaterialTheme.colorScheme.primaryContainer),
        query = text,
        onQueryChange = { text = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = {
//            active = it  Change this to show results on the screen
        },
        placeholder = { Text("Find Chore") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {  },

        ){
    }
}

@Composable
fun Workflow(){
    Column(
        Modifier.padding(15.dp,8.dp, 0.dp, 8.dp)
    ) {
        Text("Workflows",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground)
        Row(Modifier.padding(0.dp, 5.dp)){
//            WorkflowTitleCard()
            WorkflowRow()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkflowRow(){
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(end = 15.dp)
    ) {
        item {
            WorkflowTitleCard()
        }
        items(
            scoddFlows
        ){workflow ->
            Card(
                modifier = Modifier.width(120.dp).height(110.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onTertiary)
            ){
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(workflow.title,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(8.dp))

                }

            }

        }

        item{
            Card(
                modifier = Modifier.width(60.dp).height(110.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onTertiary)

            ){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {},
//                modifier = Modifier
                    ){
                        Icon(Icons.Default.Add, "add workflow")
                    }
                }

            }

        }
    }
//    HorizontalPager(
//        pageCount = scoddFlows.size+1,
//        contentPadding = PaddingValues(end = 75.dp)
//    ){
//        Card(
//            modifier = Modifier.width(120.dp).height(110.dp),
//            shape = RoundedCornerShape(28.dp),
//            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onTertiary)
//        ){
//            Column(
//                Modifier.fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                when (it) {
//                    scoddFlows.size -> {
//                        IconButton(
//                            onClick = {}
//                        ){
//                            Icon(Icons.Default.Add, "add")
//                        }
//                    }
//                    else -> {
//                        Text(scoddFlows[it].title,
//                            overflow = TextOverflow.Ellipsis,
//                            modifier = Modifier.padding(8.dp))
//                    }
//                }
//
//            }
//
//        }
//
//    }
}

@Composable
fun WorkflowTitleCard(){
    Card(
        modifier = Modifier.requiredWidth(189.dp).requiredHeight(110.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onTertiary)
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
fun Chore(){
    Column(
        Modifier.padding(15.dp,8.dp, 15.dp, 8.dp)
    ) {
        Text(
            "Chores",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        ChoreFilters()
        ChoreGrid()
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChoreFilters(){
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        scoddRooms.forEach { room ->
            FilterChip(
                selected = false,
                label = { Text(room.title) },
                onClick = {},
                colors = FilterChipDefaults.filterChipColors(labelColor = MaterialTheme.colorScheme.onBackground))

        }
    }
}

@Composable
fun ChoreCard(chore : ScoddChore){
    Card(
        Modifier.size(175.dp).padding(0.dp, 8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer,MaterialTheme.colorScheme.onPrimaryContainer)
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
                        , "favorites", tint = MaterialTheme.colorScheme.onBackground)
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
fun ChoreGrid(){
    LazyVerticalGrid(columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(15.dp)){
        items(scoddChores){ chore ->
            ChoreCard(chore)
        }
    }
}

