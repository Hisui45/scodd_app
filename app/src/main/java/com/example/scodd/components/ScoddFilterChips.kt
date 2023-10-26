package com.example.scodd.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.scodd.objects.scoddRooms

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(title : String, selected : MutableState<Boolean>, onClick: () -> Unit){

    FilterChip(
        selected = selected.value,
        label = { Text(title) },
        onClick = { onClick() },
        colors = FilterChipDefaults.filterChipColors(labelColor = MaterialTheme.colorScheme.outline,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.inversePrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.inversePrimary),
        border = FilterChipDefaults.filterChipBorder(selectedBorderColor = MaterialTheme.colorScheme.outline,
            selectedBorderWidth = 1.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterIconChip(title : String, selected : MutableState<Boolean>, onClick: () -> Unit){

    FilterChip(
        selected = selected.value,
        label = { Text(title) },
        onClick = { onClick() },
        colors = FilterChipDefaults.filterChipColors(labelColor = MaterialTheme.colorScheme.outline,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.inversePrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.inversePrimary),
        border = FilterChipDefaults.filterChipBorder(selectedBorderColor = MaterialTheme.colorScheme.outline,
            selectedBorderWidth = 1.dp),
        leadingIcon = {
            if(selected.value){
                Icon(Icons.Default.Check, null)
            }

        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomFiltersIconFlowRow(){
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        scoddRooms.forEach { room ->
            val selected = remember { mutableStateOf(room.selected) }
            FilterIconChip(room.title,
                selected,
                onClick = {}
            )

        }
    }
}
