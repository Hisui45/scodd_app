package com.example.scodd.mode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scodd.components.*
import com.example.scodd.objects.*
import com.example.scodd.ui.theme.Marigold40


@Composable
fun QuestModeScreen(
    onNavigateBack: () -> Unit
){
    StatusBar(Marigold40)
    val suggestions = listOf(ADHD, Game)
    val selected = remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScoddModeHeader(onNavigateBack,"Chore Quest", "Your trusted method for maintaining a clean " +
                "and organized home without the overwhelm. Inspired by the 5 Things method, it's time to bid farewell" +
                " to the stress of chores and say hello to a more organized, goal-oriented approach to your cleaning" +
                " routine. With clear guidance and achievable steps, you'll work through each room, making chore" +
                " management a breeze.", suggestions)
        Text("This mode focuses on rooms rather than chores.", style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
        Row(
            modifier = Modifier.padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Focus Areas")
            Spacer(Modifier.weight(1f))
            LabelText("Select All")
            RadioButton(
                selected = selected.value,
                onClick = {selected.value = !selected.value},
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.secondary)
            )
        }
        LazyColumn {

            itemsIndexed(scoddRooms){index, room ->
                val checked = remember { mutableStateOf(false) }
                ChoreListItem("", room.title, checked.value,
                    onCheckChanged = {
                    checked.value = !checked.value
                }, true)

                if (index < scoddRooms.lastIndex)
                    Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
            }


        }

    }
}