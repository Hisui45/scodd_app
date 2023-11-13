package com.example.scodd.ui.mode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.scodd.data.Workflow1
import com.example.scodd.data.scoddChores
import com.example.scodd.model.*
import com.example.scodd.ui.theme.Marigold40
import com.example.scodd.ui.components.*
import com.example.scodd.utils.ADHD
import com.example.scodd.utils.Game


@Composable
fun SpinModeScreen(
    onNavigateBack: () -> Unit,
    onAddChoreToModeClick : (Workflow) -> Unit
){
    StatusBar(Marigold40)
    val suggestions = listOf(ADHD, Game)
    Column {
        ScoddModeHeader(onNavigateBack,"Chore Spin", "Injects an element of surprise into your " +
                "daily routine. Instead of manually selecting your next chore, you can leave it up to chance with a fun spin of " +
                "the wheel. Just like spinning a roulette wheel, Chore Spin randomly selects a task from your list of chores, " +
                "adding an element of excitement and unpredictability to your daily chores.",suggestions)
        WorkflowSelectModeRow()

        ChoreSelectModeHeaderRow("", "")
        LazyColumn {
            itemsIndexed(scoddChores){ index, chore ->

                ModeChoreListItem(chore.title, trailingContent = {

                })
                if (index < scoddChores.lastIndex)
                    Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)

            }
            item{
                AddChoreButton(onClick = {
                    onAddChoreToModeClick(Workflow1) //So take list of chores for the mode and pass it through, the id

                })
            }
        }
    }
}