package com.example.scodd.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.scodd.model.ScoddTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableRoomFilterChip(title : String, selected:Boolean, onSelectedChanged: () -> Unit, modifier : Modifier){
    FilterChip(
        modifier = modifier,
        selected = selected,
        onClick = {onSelectedChanged()},
        label = {Text(title) },
        colors = FilterChipDefaults.filterChipColors(labelColor = MaterialTheme.colorScheme.outline,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.outline,
            selectedLeadingIconColor = MaterialTheme.colorScheme.outline
        ),
        border = FilterChipDefaults.filterChipBorder(selectedBorderColor = Color.Transparent)
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchableFilterChip(scoddTime : ScoddTime, selected: ScoddTime, onSelectedChanged: () -> Unit) {
        FilterChip(
            selected = scoddTime == selected,
            onClick = {
                onSelectedChanged()
            },
            label = { Text(scoddTime.title) },
            colors = FilterChipDefaults.filterChipColors(
                labelColor = MaterialTheme.colorScheme.outline,
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.outline,
                selectedLeadingIconColor = MaterialTheme.colorScheme.outline
            ),
            border = FilterChipDefaults.filterChipBorder(selectedBorderColor = Color.Transparent)
        )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchableIconFilterChips(scoddTime : ScoddTime, selected: ScoddTime, onSelectedChanged: () -> Unit){
    FilterChip(
        selected = scoddTime == selected,
        onClick = {
            onSelectedChanged()
        },
        label = { Text(scoddTime.title) },
        colors = FilterChipDefaults.filterChipColors(
            labelColor = MaterialTheme.colorScheme.outline,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.outline,
            selectedLeadingIconColor = MaterialTheme.colorScheme.outline
        ),
        border = FilterChipDefaults.filterChipBorder(selectedBorderColor = Color.Transparent),
        leadingIcon = {
            if(scoddTime == selected){
                Icon(Icons.Default.Check, null)
            }
        },
    )
}
