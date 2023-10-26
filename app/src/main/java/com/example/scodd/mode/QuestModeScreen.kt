package com.example.scodd.mode

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.scodd.objects.ADHD
import com.example.scodd.objects.Game
import com.example.scodd.components.ScoddModeHeader


@Composable
fun QuestModeScreen(
    onNavigateBack: () -> Unit
){
    val suggestions = listOf(ADHD, Game)
    Column {
        ScoddModeHeader(onNavigateBack,"", "", suggestions)
    }
}