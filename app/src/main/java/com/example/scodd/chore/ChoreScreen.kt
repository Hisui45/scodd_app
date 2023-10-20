package com.example.scodd.chore

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.unit.dp
import com.example.scodd.R
import com.example.scodd.scoddChores
import com.example.scodd.scoddFlows
import com.example.scodd.scoddRooms


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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Workflow(){
    Column(
        Modifier.padding(15.dp,8.dp, 0.dp, 8.dp)
    ) {
        Text("Workflows",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground)
        Row(Modifier.padding(0.dp, 5.dp)){
            Card(
                modifier = Modifier.width(179.dp).height(106.dp).padding(0.dp, 0.dp, 4.dp, 0.dp ),
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

            HorizontalPager(
                pageCount = 5,
                contentPadding = PaddingValues(end = 90.dp)
                ){
                Card(
                    modifier = Modifier.width(120.dp).height(106.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onTertiary)
                ){
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if(it == 4){
                            IconButton(
                                onClick = {}
                            ){
                                Icon(Icons.Default.Add, "add")
                            }
                        }else{
                            Text(scoddFlows[it].title)
                        }

                    }

                }

            }
        }

    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Chore(){
    Column(
        Modifier.padding(15.dp,8.dp, 0.dp, 8.dp)
    ) {
        Text(
            "Chores",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
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
        FlowRow(
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            scoddChores.forEach {chore ->
                Card(
                    Modifier.fillMaxWidth(0.46f).fillMaxHeight(0.50f).padding(0.dp, 8.dp),
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
                            modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp), fontWeight = FontWeight.Light, style = MaterialTheme.typography.titleLarge)
                    }

                }

            }
        }
    }
}



