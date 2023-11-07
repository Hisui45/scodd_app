package com.example.scodd.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scodd.objects.ScoddRoom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableRoomFilterChip(title : String, selected:Boolean, onSelectedChanged: () -> Unit){
    FilterChip(
        selected = selected,
        onClick = {onSelectedChanged()},
        label = {Text(title) },
        colors = FilterChipDefaults.filterChipColors(labelColor = MaterialTheme.colorScheme.outline,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.inversePrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.inversePrimary),
        border = FilterChipDefaults.filterChipBorder(selectedBorderColor = Color.Transparent)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectableRoomFilterChips(chips : List<ScoddRoom>){
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        chips.forEachIndexed { index, scoddRoom -> //Use index to know what room was selected
            val selected = remember { mutableStateOf(false) } //Input pre-filled here
            SelectableRoomFilterChip(scoddRoom.title,selected.value,
                onSelectedChanged= {
                    selected.value = !selected.value
                    //Update room selected value here
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchableFilterChip(title : String, index : Int, selected: Int, onSelectedChanged: () -> Unit) {
        FilterChip(
            selected = index == selected,
            onClick = {
                onSelectedChanged()
            },
            label = { Text(title) },
            colors = FilterChipDefaults.filterChipColors(
                labelColor = MaterialTheme.colorScheme.outline,
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.inversePrimary,
                selectedLeadingIconColor = MaterialTheme.colorScheme.inversePrimary
            ),
            border = FilterChipDefaults.filterChipBorder(selectedBorderColor = Color.Transparent)
        )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchableIconFilterChips(title : String, index : Int, selected: Int, onSelectedChanged: () -> Unit){
    FilterChip(
        selected = index == selected,
        onClick = {
            onSelectedChanged()
        },
        label = { Text(title) },
        colors = FilterChipDefaults.filterChipColors(
            labelColor = MaterialTheme.colorScheme.outline,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.inversePrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.inversePrimary
        ),
        border = FilterChipDefaults.filterChipBorder(selectedBorderColor = Color.Transparent),
        leadingIcon = {
            if(selected == index){
                Icon(Icons.Default.Check, null)
            }
        },
    )
}
