package com.example.scodd.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.scodd.R
import com.example.scodd.model.*
import com.example.scodd.model.ScoddTime
import com.example.scodd.utils.LazyAnimations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LoginButton(onClick: () -> Unit, text : String){
    Button(onClick = { onClick() },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
    ){
        Text(text)
    }
}

@Composable
fun LabelText(text : String){
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
fun ChoreSwitch(checked : Boolean, label : String, onCheckChanged: (Boolean) -> Unit){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(label)
        Spacer(Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = {onCheckChanged(it)},
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onSecondary,
                checkedTrackColor = MaterialTheme.colorScheme.secondary)
        )
    }

}

@Composable
fun ChoreDivider(){
    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoreDropdownNumberInput(value : Int, selectedOption : ScoddTime, onOptionSelect: (ScoddTime) -> Unit, options: List<ScoddTime>,
                             onValueChange : (String) -> Unit, errorMessage : Int?){
    var expanded by remember { mutableStateOf(false) }

    val text = if (value == -1) "" else value.toString()

    var errorText = ""
    errorMessage?.let { message ->
         errorText = stringResource(message)
    }
    val isError = errorText.isNotEmpty()

    val focusManager = LocalFocusManager.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        OutlinedTextField(
            value = text,
            onValueChange = {onValueChange(it)},
            modifier = Modifier.width(150.dp),
            singleLine = true,
            placeholder = {Text("Timer Value")},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions{focusManager.clearFocus()},
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
                focusedLabelColor = MaterialTheme.colorScheme.secondary),
            isError = isError,
            label = {},
            supportingText = {
                if (isError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = errorText,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(if (value <= 1) option.title else option.title + "s")
                        },
                        onClick = {
                            onOptionSelect(option)
                            expanded = false
                        }
                    )
                }
            }
            OutlinedTextField(
                readOnly = true,
                value = if (value <= 1) selectedOption.title else selectedOption.title + "s",
                onValueChange = { },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.outline,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.menuAnchor(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { },
                supportingText = {

                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChoreCard(
    title: String,
    firstRoom: String,
    additionalAmount: Int,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    animateModifier: Modifier,
    isSelected: Boolean,
    removeSelection: () -> Unit,
    addSelection: () -> Unit,
    isSelectionActive: Boolean,
    isInSelectionMode : MutableState<Boolean>,
    onCardClick: () -> Unit
){
    val resource = if (isFavorite) R.drawable.filled_star_24 else R.drawable.star_24

    val colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.onSecondaryContainer)

    Card(
        modifier = Modifier.size(175.dp).padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
            .combinedClickable(
            onClick = {
                if(isSelectionActive){
                    if (isInSelectionMode.value) {
                        if (isSelected) {
                            removeSelection()
                        } else {
                            addSelection()
                        }
                    } else {
                        onCardClick()
                    }
                }else{
                    onCardClick()
                }
            },
            onLongClick = {
                if(isSelectionActive){
                    if (isInSelectionMode.value) {
                        if (isSelected) {
                            removeSelection()
                        } else {
                            addSelection()
                        }
                    } else {
                        isInSelectionMode.value = true
                        addSelection()
                    }
                }
            },
        ).then(animateModifier),
        colors = colors
    ){
        Column(
            Modifier.padding(12.dp, 0.dp, 0.dp, 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ){
                Column(Modifier.padding(top = 10.dp)){
                    Text(firstRoom,
                        modifier = Modifier.padding(end = 4.dp),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Light)
                    if(additionalAmount > 0) {
                        Text(
                            text = "(+$additionalAmount more)",
                            textAlign = TextAlign.End,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = TextDecoration.Underline,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
                Spacer(Modifier.weight(1f))

                if(isSelected) {
                    Icon(Icons.Default.CheckCircle,
                        stringResource(R.string.chore_selected),
                        Modifier.padding(12.dp))
                }else{
                    IconButton(
                        onClick = {
                            onFavoriteClick()
                        }){
                        Icon(
                            painterResource(id = resource), "favorites", tint = MaterialTheme.colorScheme.onSecondary)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Text(title,
                textAlign = TextAlign.End,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp), fontWeight = FontWeight.Light, style = MaterialTheme.typography.titleLarge)
        }

//        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChoreContent(
    onChoreSelected : (String) -> Unit,
    roomChips: List<Room>,
    nestedScrollConnection : NestedScrollConnection,
    chores : List<Chore>,
    toggleChip : (Room) -> Unit,
    onFavoriteChipSelected : () -> Unit,
    favoriteSelected : Boolean,
    onFavoriteChanged : (Chore) -> Unit,
    getRoomTitle: (Chore, Int) -> String,
    isSelectionActive: Boolean,
    isInSelectionMode : MutableState<Boolean>,
    selectedItems: SnapshotStateList<String>

){
    Column(
        Modifier.padding(15.dp,8.dp, 15.dp, 0.dp)
    ) {
        LabelText(stringResource(R.string.chore_label))
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var doAnimation by remember { mutableStateOf(false) }
    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ){
        item(key = "0") {}
        itemsIndexed(
            items = roomChips,
            key = { _, item -> item.id })
        { _,room ->
            val modifier = Modifier.animateItemPlacement(LazyAnimations.ROOM.animation)
            SelectableRoomFilterChip(room.title, room.selected,
                onSelectedChanged = {
                    toggleChip(room)
                    doAnimation = true
                },
                modifier = modifier)
            /**
             * TODO: restart animation delay if user toggles another chip priority = 5
             */
        }

        item {
            val modifier = Modifier.animateItemPlacement(LazyAnimations.ROOM.animation)
            SelectableRoomFilterChip("Favorites", favoriteSelected,
                onSelectedChanged = {
                    onFavoriteChipSelected()
                },
                modifier = modifier)
        }
    }
    /**
     * Chore Section
     */
    if (chores.isEmpty()) {
        Column(
            Modifier.fillMaxHeight(1f).fillMaxWidth(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.no_chores_message))
        }
    } else {

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.nestedScroll(nestedScrollConnection),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 8.dp)
        ) {
            itemsIndexed(
                items = chores,
                key = { _, item -> item.id }
            ){ _, chore ->
                val isSelected = selectedItems.contains(chore.id)
                ChoreCard(
                    title = chore.title,
                    firstRoom = getRoomTitle(chore, 0),
                    additionalAmount = chore.rooms.size-1,
                    isFavorite = chore.isFavorite,
                    onFavoriteClick = {onFavoriteChanged(chore)},
                    animateModifier = Modifier.animateItemPlacement(LazyAnimations.CHORE.animation),
                    isSelected = isSelected,
                    isSelectionActive = isSelectionActive,
                    isInSelectionMode = isInSelectionMode,
                    addSelection = {selectedItems.add(chore.id)},
                    removeSelection = {selectedItems.remove(chore.id)},
                    onCardClick = {onChoreSelected(chore.id)}
                )
            }
        }
    }


    if(doAnimation){
        LaunchedEffect(key1 = listState) {
            coroutineScope.launch {
                delay(800)
                listState.animateScrollToItem(0)
                doAnimation = false
            }
        }
    }
}

@Composable
fun CreateDialog(header : String, onDismissRequest: () -> Unit, onCreateClick : (String) -> Unit, show : MutableState<Boolean>){
    val title = remember { mutableStateOf("") }
    var isError = false

    if(show.value){
        Dialog(
            onDismissRequest = {onDismissRequest()}
        ){
            ElevatedCard(
                shape = RoundedCornerShape(28.dp)
            ){
                Column(Modifier.padding(16.dp)){
                    Text(header, modifier = Modifier.padding(vertical = 12.dp))
                    OutlinedTextField(
                        value = title.value,
                        onValueChange = { title.value = it ; isError = title.value.isEmpty()
                        },
                        modifier = Modifier.padding(vertical = 16.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(KeyboardCapitalization.Sentences),
                        singleLine = true,
                        isError = isError,
                        supportingText = {
                            if(isError){
                                Text(stringResource(R.string.no_title_message))
                            }
                        }
                    )
                    val buttonColor = MaterialTheme.colorScheme.onSurfaceVariant
                    Row{
                        Spacer(Modifier.weight(1f))
                        TextButton(
                            onClick = { if(!isError){onCreateClick(title.value)}},
                            colors = ButtonDefaults.textButtonColors(contentColor = buttonColor)
                        ){
                            Text("Create")
                        }
                    }
                }

            }
        }
    }

}

@Composable
fun ChoreListItem(
        firstRoom : String,
        additionalAmount: Int,
        title: String,
        isComplete : Boolean,
        onCheckChanged : (Boolean) -> Unit,
        showCheckBox : Boolean,
        animateModifier : Modifier){
    /**
     * TODO: option to hide additional rooms priority: 5
     */
    ListItem(
        modifier = animateModifier,
        overlineContent = {
            Row(Modifier.fillMaxWidth()){
                if(firstRoom.isNotEmpty()){
                    Text(firstRoom)
                    if(additionalAmount > 0){
                        Text(" (+$additionalAmount more)")
                    }
                }
            }
        },
        headlineContent = {
            Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 1,overflow = TextOverflow.Ellipsis,
                textDecoration = if (isComplete && showCheckBox) {
                    TextDecoration.LineThrough
                } else {
                    null
                })
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        trailingContent = {
            if (showCheckBox) {
                Checkbox(checked = isComplete, onCheckedChange = {onCheckChanged(it)})
            }
        },
    )
}

@Composable
fun TimeModeChoreListItem(
        title: String,
        timerValue: Int,
        isDistinct : Boolean,
        timeUnit: String,
        onErrorClick:(Int) -> Unit
){
    ListItem(
        headlineContent = {
            Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 1,overflow = TextOverflow.Ellipsis)
        },
        trailingContent = {
            if(timerValue > 0 && isDistinct){
            LabelText("$timerValue $timeUnit")
        }else if(!isDistinct){
            IconButton(
                onClick = {onErrorClick(R.string.chore_repeat) }
            ){
                Icon(Icons.Default.Warning, stringResource(R.string.chore_repeat), tint = MaterialTheme.colorScheme.error)
            }
        }else{
            IconButton(
                onClick = {onErrorClick(R.string.chore_timer_mode_not_active) }
            ){
                Icon(Icons.Default.Warning, stringResource(R.string.chore_timer_mode_not_active), tint = MaterialTheme.colorScheme.error)
            }

        }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )

}

@Composable
fun BankModeChoreListItem(
    title: String,
    bankValue: Int,
    isDistinct : Boolean,
    onErrorClick:(Int) -> Unit
){
    ListItem(
        headlineContent = {
            Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 1,overflow = TextOverflow.Ellipsis)
        },
        trailingContent = {
            if(bankValue > 0 && isDistinct){
                LabelText("$$bankValue")
            }else if(!isDistinct){
                IconButton(
                    onClick = {onErrorClick(R.string.chore_repeat) }
                ){
                    Icon(Icons.Default.Warning, stringResource(R.string.chore_repeat), tint = MaterialTheme.colorScheme.error)
                }
            }else{
                IconButton(
                    onClick = {onErrorClick(R.string.chore_bank_mode_not_active) }
                ){
                    Icon(Icons.Default.Warning, stringResource(R.string.chore_bank_mode_not_active), tint = MaterialTheme.colorScheme.error)
                }

            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )

}

@Composable
fun ModeChoreListItem(title: String, isDistinct: Boolean, onErrorClick: (Int) -> Unit){
    ListItem(
        headlineContent = {
            Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 1,overflow = TextOverflow.Ellipsis)
        },
        trailingContent = {
            if(!isDistinct){
                IconButton(
                    onClick = {onErrorClick(R.string.chore_repeat) }
                ){
                    Icon(Icons.Default.Warning, stringResource(R.string.chore_repeat), tint = MaterialTheme.colorScheme.error)
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )

}

@Composable
fun ChoreSelectModeHeaderRow(prefix : String, value : String){
    Row(
        Modifier.padding(start = 12.dp, end = 23.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text("Chores", modifier = Modifier.padding(bottom = 4.dp))
        Spacer(Modifier.weight(1f))
        LabelText("$prefix $value")
    }
}



@Composable
fun AddChoreButton(onClick : () -> Unit, choreNumber: Int){
    /**
     * TODO: consider re-designing
     */
    Row(
        Modifier.fillMaxWidth().background(Color.Transparent),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = {onClick()},
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
        ){
            var contentDescription = stringResource(R.string.add_chore_items)
            var text = stringResource(R.string.add_chore_items)

            if(choreNumber > 0){
                text = stringResource(R.string.edit_chore_items)
                contentDescription = stringResource(R.string.edit_chore_items)
            }

            Text(text,
//                style = MaterialTheme.typography.bodyLarge
            )
            Icon(Icons.Default.AddCircle, contentDescription )

        } }

}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    showDialog: Boolean
) {
    if(showDialog){
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(R.string.delete_item)) },
            text = { Text(text = stringResource(R.string.confirm_delete_message)) },
            confirmButton = {
                Button(
                    onClick = {onConfirm()}
                ) { Text(text = stringResource(R.string.delete)) }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}