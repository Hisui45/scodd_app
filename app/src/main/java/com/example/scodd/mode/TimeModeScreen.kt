package com.example.scodd.mode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.scodd.objects.ADHD
import com.example.scodd.objects.Game
import com.example.scodd.objects.Motivation
import com.example.scodd.components.ScoddModeHeader
import com.example.scodd.components.WorkflowSelectCard
import com.example.scodd.objects.scoddFlows

@Composable
fun TimeModeScreen(
    onNavigateBack: () -> Unit){
    val suggestions = listOf(ADHD, Game, Motivation)
//    StatusBar(Marigold40)
    Column {
        ScoddModeHeader(onNavigateBack,"Time Crunch",
            "The ultimate solution for those seeking" +
                " a fun and motivating way to complete household tasks efficiently. This mode transforms the mundane " +
                "into the thrilling by using a specific time limit to each chore in your workflow. It's like turning " +
                "your cleaning routine into a high-stakes game where you race against the clock to conquer clutter " +
                "and chaos.",
            suggestions
        )
        Workflow()
    }
}

@Composable
fun Workflow(){
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(end = 15.dp)
    ) {

        items(scoddFlows){ workflow ->
            WorkflowSelectCard(workflow.title)
        }

    }
}