package com.example.scodd.chore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ChoreScreen() {
    Surface{
        Workflow()

    }
}

@Composable
fun Workflow(){
    Column(
        Modifier.padding(8.dp)
    ) {
        Text("Workflows", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground)
    }
}