package com.example.scodd.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scodd.utils.ScoddSuggestions

@Composable
fun ScoddModeHeader(onNavigateBack: () -> Unit, mode : String, description : String, suggestions: List<String>,) {
    val horizontalPadding = 13.dp
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(0.dp, 0.dp, 18.dp, 18.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 19.dp, bottom = 8.dp, end = horizontalPadding)
        ){
            IconButton(
                onClick = {onNavigateBack()}
            ){
                Icon(Icons.Default.ArrowBack, "back")
            }
            Spacer(Modifier.weight(1f))
            Text(mode,
                style = MaterialTheme.typography.headlineLarge)
        }
        Column(
            Modifier.padding(start = horizontalPadding, end = horizontalPadding, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(description, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Light)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ){
                suggestions.forEach { suggestion ->
                    ScoddSuggestionChip(suggestion)
                }
            }
        }
    }
}

@Composable
fun ScoddSuggestionChip(title : String){
    SuggestionChip(
        label = {Text(title, fontWeight = FontWeight.Light)},
        onClick = {},
        icon = { Icon(Icons.Default.Check,"check") },
        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer, iconContentColor = MaterialTheme.colorScheme.onSecondary),
        border = SuggestionChipDefaults.suggestionChipBorder(borderColor = MaterialTheme.colorScheme.onSecondary)
    )

}