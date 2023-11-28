package com.example.scodd.ui.mode.quest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scodd.R
import com.example.scodd.navigation.ModeBottomBar
import com.example.scodd.ui.theme.Marigold40
import com.example.scodd.ui.components.LabelText
import com.example.scodd.ui.components.ScoddModeHeader
import com.example.scodd.ui.components.StatusBar
import com.example.scodd.utils.ADHD
import com.example.scodd.utils.Game

@Composable
fun QuestModeScreen(
    onNavigateBack: () -> Unit,
    viewModel: QuestModeViewModel = hiltViewModel()
){
    StatusBar(Marigold40)
    val suggestions = listOf(ADHD, Game)

    Scaffold(
        snackbarHost = {},
        bottomBar = {ModeBottomBar(false, onStartClick = {})}
    ){
        val uiState by viewModel.uiState.collectAsState()
        val isSelectAll = uiState.selectedRooms.containsAll(uiState.rooms.map { room ->  room.id })
        Column(
            modifier = Modifier.padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScoddModeHeader(onNavigateBack,
                stringResource(R.string.quest_mode_title),
                stringResource(R.string.quest_mode_desc),
                suggestions)

            Text("This mode focuses on rooms rather than chores.", style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
            Row(
                modifier = Modifier.padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text("Focus Areas")
                Spacer(Modifier.weight(1f))
                LabelText("Select All")
                RadioButton(
                    selected = isSelectAll,
                    onClick = {viewModel.selectAll(isSelectAll)},
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.secondary)
                )
            }
            LazyColumn {
                itemsIndexed(uiState.rooms){index, room ->
                    val isSelected = viewModel.isRoomSelected(room.id)
                    ListItem(
                        headlineContent = {
                            Text(room.title, style = MaterialTheme.typography.titleLarge, maxLines = 1,overflow = TextOverflow.Ellipsis)
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        trailingContent = {
                                Checkbox(checked = isSelected, onCheckedChange = {viewModel.selectRoom(room)})
                        },
                    )
                    if (index < uiState.rooms.lastIndex)
                        Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                }
            }
        }
    }
}