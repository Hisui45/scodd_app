package com.example.scodd.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.scodd.R
import com.example.scodd.model.Workflow
import kotlin.reflect.KFunction1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkflowSelectCard(workflow : String, selected : Boolean, onClick : () -> Unit, animateModifier: Modifier ){
    var colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)

    if(selected){
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
    }
    Card(
        modifier = Modifier.width(189.dp).height(110.dp).then(animateModifier),
        shape = RoundedCornerShape(28.dp),
        colors = colors,
        onClick = { onClick() }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkflowSelectRow(
    workflows: List<Workflow>,
    isSelected: (String) -> Boolean,
    onWorkflowSelect: (Workflow) -> Unit,
    onCreateWorkflowClick: () -> Unit
){
    val colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        itemsIndexed(
            items = workflows,
            key= {_, item -> item.id}
        ){ _ ,workflow -> //Use the index value to know what workflow to add new chore to
            WorkflowSelectCard(workflow.title, isSelected(workflow.id),
                onClick = {onWorkflowSelect(workflow)},
                Modifier.animateItemPlacement())
        }
        item{
            AddWorkflowCard(onCreateWorkflowClick, colors)
        }
    }
}


@Composable
fun WorkflowSelectModeRow(
    workflows: List<Workflow>,
    isSelected: (String) -> Boolean,
    onWorkflowSelect: (Workflow) -> Unit,
) {
    val showWorkflows = remember { mutableStateOf(true) }
    Row(
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.workflow_label))
        Spacer(Modifier.weight(1f))
        IconButton(onClick = {showWorkflows.value = !showWorkflows.value }
        ){
            val iconResource = if (showWorkflows.value) R.drawable.expand_less_24 else R.drawable.expand_more_24
            Icon(ImageVector.vectorResource(iconResource), "workflow dropdown")
        }
    }
    if (showWorkflows.value){
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            itemsIndexed(workflows) {_, workflow ->
                WorkflowSelectCard(workflow.title,isSelected(workflow.id),
                    onClick = {onWorkflowSelect(workflow)}, Modifier)
            }
        }
    }
}

