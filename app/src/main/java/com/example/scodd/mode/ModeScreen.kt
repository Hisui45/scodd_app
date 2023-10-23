package com.example.scodd.mode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ModeScreen() {
    Surface{
        Column(
            Modifier.padding(12.dp, 0.dp)
        ){
            ModeTitleCard()
            ModeList()
        }

    }
}

@Composable
fun ModeTitleCard(){
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
        modifier = Modifier.fillMaxWidth(1f).padding(0.dp, 12.dp)
    ){
        Column(
            Modifier.padding(24.dp)
        ){
            Text("Modes",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.height(90.dp))
            Text("Utilize different techniques to work through chores and workflows.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Light)
        }

    }
}
@Composable
fun ModeCard(mode : String, onClick: () -> Unit){
    ElevatedCard(
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp , 12.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer,MaterialTheme.colorScheme.onTertiaryContainer)
    ){

        Column(
            Modifier.fillMaxWidth().fillMaxHeight().height(150.dp),
            horizontalAlignment = Alignment.End
        ){
            IconButton(
                onClick ={onClick}
            ){
                Icon(Icons.Default.MoreVert,"More")

            }
            Spacer(Modifier.weight(1f))
            Text(mode,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(16.dp))
        }

    }
}
@Composable
fun ModeList(){
    LazyColumn(
        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp)
    ){
        item {
            ModeCard("Time Crunch", onClick = {})
        }

        item{
            ModeCard("Chore Quest", onClick = {})
        }

        item{
            ModeCard("Chore Spin", onClick = {})
        }

        item{
            ModeCard("Sand Glass", onClick = {})
        }

        item{
            ModeCard("Piggy Bank", onClick = {})
        }
    }
}
