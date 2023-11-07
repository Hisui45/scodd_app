package com.example.scodd.mode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.scodd.components.*
import com.example.scodd.objects.*
import com.example.scodd.ui.theme.Marigold40

@Composable
fun TimeModeScreen(
    onNavigateBack: () -> Unit,
    onAddChoreToModeClick : (ScoddWorkflow) -> Unit
    ){
    val suggestions = listOf(ADHD, Game, Motivation)
    StatusBar(Marigold40)
    Column {
        ScoddModeHeader(onNavigateBack,"Time Crunch",
            "The ultimate solution for those seeking" +
                " a fun and motivating way to complete household tasks efficiently. This mode transforms the mundane " +
                "into the thrilling by using a specific time limit to each chore in your workflow. It's like turning " +
                "your cleaning routine into a high-stakes game where you race against the clock to conquer clutter " +
                "and chaos.",
            suggestions
        )
        WorkflowSelectModeRow()
        var value = 0 //Calculate from data logic most likely move to domain layer
        for(chore in scoddChores){
            value += chore.timerValue
        }
        ChoreSelectModeHeaderRow("Estimated Time:", "$value minutes")
        LazyColumn {
            itemsIndexed(scoddChores){ index, chore ->
                val timerValue = chore.timerValue
                ModeChoreListItem(chore.title, trailingContent = {
                        LabelText("$timerValue mins")
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
