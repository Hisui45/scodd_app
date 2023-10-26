package com.example.scodd.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.scodd.objects.scoddFlows

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkflowSelectCard(workflow : String){
    Card(
        modifier = Modifier.width(149.dp).height(110.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.outline),
        onClick = {}
    ){
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(workflow,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkflowCard(onCreateWorkflowClick : () -> Unit, colors : CardColors){
    Card(
        modifier = Modifier.width(60.dp).height(110.dp),
        shape = RoundedCornerShape(28.dp),
        colors = colors,
        onClick = {onCreateWorkflowClick()}

    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Add, "add workflow")

        }

    }
}

@Composable
fun WorkflowSelectRow(onCreateWorkflowClick : () -> Unit){
    val colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.outline)
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(scoddFlows){ workflow ->
            WorkflowSelectCard(workflow.title)
        }

        item{
            AddWorkflowCard(onCreateWorkflowClick, colors)
        }
    }
}