package com.example.scodd.ui.chore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.scodd.R
import com.example.scodd.data.Workflow3
import com.example.scodd.data.scoddChores
import com.example.scodd.data.scoddFlows
import com.example.scodd.model.Workflow
import com.example.scodd.ui.components.AddChoreButton
import com.example.scodd.ui.components.ChoreListItem
import com.example.scodd.ui.components.NavigationButton
import com.example.scodd.ui.components.StatusBar
import com.example.scodd.ui.theme.Marigold40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkflowScreen(workflowTitle : String?, onNavigateBack : () -> Unit, onAddChoreClick : (Workflow) -> Unit){ //Use unique ids
    StatusBar(Marigold40)
    val contentColor =  MaterialTheme.colorScheme.onPrimary
    val showCheckBox = remember { mutableStateOf(false) }
    val workflow = scoddFlows.find { it.title == workflowTitle} ?: Workflow3
    Column {
        TopAppBar(
            title = {
                Text(workflow.title)
            },
            navigationIcon = { NavigationButton(onNavigateBack) },
            actions = {
                val icon = if (showCheckBox.value) Icons.Default.List else Icons.Default.Check
                val contentDescription =
                    if (showCheckBox.value) stringResource(R.string.workflow_view_button_contDesc) else stringResource(R.string.workflow_complete_button_contDesc)
                IconButton(
                    onClick = {showCheckBox.value = !showCheckBox.value}
                ){
                    Icon(icon, contentDescription)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary,
                navigationIconContentColor = contentColor,
                titleContentColor = contentColor,
                actionIconContentColor = contentColor
            )
        )
        val count = scoddChores.count()
        Text(
            stringResource(R.string.chore_label) + ": $count",
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.inverseSurface,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp))
        LazyColumn{
            itemsIndexed(scoddChores){index, chore ->
                val checked = remember { mutableStateOf(false) }
                ChoreListItem("Kitchen", chore.title,checked.value,
                    onCheckChanged = {
                        checked.value = !checked.value
                    }, showCheckBox.value)
//                ListItem(
//                    headlineContent = {Text(chore.title, maxLines = 1,overflow = TextOverflow.Ellipsis )},
//                    overlineContent = {Text(chore.room.title)},
//                    trailingContent = {
//                        if(showCheckBox.value){
//                            Checkbox(
//                                checked = checked.value,
//                                onCheckedChange = {checked.value = !checked.value}
//                            )
//                        }
//                    },
//                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
//
//
//                )
                if (index < scoddChores.lastIndex)
                    Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)

            }
        }
        AddChoreButton(onClick = {
            onAddChoreClick(workflow)
        })
    }

}