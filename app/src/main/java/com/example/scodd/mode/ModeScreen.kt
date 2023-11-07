package com.example.scodd.mode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scodd.components.StatusBar
import com.example.scodd.objects.ScoddDestination
import com.example.scodd.ui.theme.White40

@Composable
fun ModeScreen(
    modeScreens: List<ScoddDestination>,
    onModeClick: (ScoddDestination) -> Unit) {
    Surface{
        StatusBar(White40)
        Column(
            Modifier.padding(12.dp, 0.dp)
        ){
            ModeTitleCard()
            ModeList(modeScreens,onModeClick)
        }

    }
}

@Composable
fun ModeTitleCard(){
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary),
        modifier = Modifier.fillMaxWidth(1f).padding(bottom = 8.dp)
    ){
        Column(
            Modifier.padding(24.dp)
        ){
            Text("Modes",
                style = MaterialTheme.typography.displaySmall)
            Text("Utilize different techniques to work through chores and workflows.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Light)
        }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeCard(mode : ScoddDestination, onModeClick: (ScoddDestination) -> Unit ){

    ElevatedCard(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary,MaterialTheme.colorScheme.onPrimary),
        onClick = {onModeClick(mode)}
    ){

        Column(
            Modifier.fillMaxWidth().fillMaxHeight().height(150.dp),
            horizontalAlignment = Alignment.End
        ){
            IconButton(
                onClick ={}
            ){
                Icon(Icons.Default.MoreVert,"More")

            }
            Spacer(Modifier.weight(1f))
            Text(mode.route,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp))
        }

    }
}
@Composable
fun ModeList(modeScreens: List<ScoddDestination>,
             onModeClick: (ScoddDestination) -> Unit,
){
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        items(modeScreens){ mode ->
            ModeCard(
                mode, onModeClick)
        }
    }
}
