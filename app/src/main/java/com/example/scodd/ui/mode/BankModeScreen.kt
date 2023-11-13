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
fun BankModeScreen(
    onNavigateBack: () -> Unit,
    onAddChoreToModeClick : (Workflow) -> Unit
){
    StatusBar(Marigold40)
    val suggestions = listOf(ADHD, Game)
    Column {
        ScoddModeHeader(onNavigateBack,"Piggy Bank", "Adds an exciting twist to your daily routine." +
                " It associates a virtual cash value with each chore on your to-do list. As you successfully" +
                " complete your chores, you'll see your \"Piggy Bank\" grow. It's like granting yourself a" +
                " personal budget for guilt-free indulgence in treats and small luxuries as a well-deserved" +
                " reward, all while maintaining a sense of accomplishment.",suggestions)
        WorkflowSelectModeRow()
        var value = 0 //Calculate from data logic most likely move to domain layer
        for(chore in scoddChores){
            value += chore.bankModeValue
        }
        ChoreSelectModeHeaderRow("Potential Payout:", "$$value")
        LazyColumn {
            itemsIndexed(scoddChores){ index, chore ->
                val bankValue = chore.bankModeValue
                ModeChoreListItem(chore.title, trailingContent = {
                    LabelText("$$bankValue")
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