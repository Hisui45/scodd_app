package com.example.scodd.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.scodd.R
import com.example.scodd.data.scoddFlows
import com.example.scodd.model.*
import com.example.scodd.model.ScoddTime

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

    var text = if (value== -1) "" else value.toString()

    var errorText = ""
    errorMessage?.let { errorMessage ->
         errorText = stringResource(errorMessage)
    }
    var isError = errorText.isNotEmpty()

    val focusManager = LocalFocusManager.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        OutlinedTextField(
            value = text,
            onValueChange = {onValueChange(it)},
            modifier = Modifier.width(150.dp),
            singleLine = true,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoreCard(title : String, room : String,additionalAmount : Int, isFavorite : Boolean, onFavoriteClick : () -> Unit,
              onCardClick : () -> Unit, index : Int){
    val resource = if (isFavorite) R.drawable.filled_star_24 else R.drawable.star_24
    val selectedItems = remember {mutableStateMapOf<Int, Boolean>()}
    val selected by remember {mutableStateOf(false)}
    val onItemSelected = { selected: Boolean, index: Int ->
        selectedItems[index] = selected
    }
    var selectColor =  CardDefaults.outlinedCardColors(containerColor = Color.Transparent)

    if (selected) {
        selectColor =  CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.6f))
    }

//    Box(Modifier.padding(bottom = 8.dp)){
//        Card(
//            modifier = Modifier.width(179.dp).height(175.dp),
//            shape = RoundedCornerShape(size = 18.dp),
//            colors = selectColor,
//            border = BorderStroke(1.dp, color = Color.Black.copy(0.2f))
//            ){
//        }

    var roomText = room
    if(additionalAmount > 0){
        roomText = "$roomText..."
    }
        Card(
            modifier = Modifier.size(175.dp).padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
                .selectable(selected = selected, enabled = true, role = Role.Button,
                    onClick = {
                        onItemSelected.invoke(!selected, index)
                    }),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer),
            onClick = {onCardClick()}
        ){
            Column(
                Modifier.padding(12.dp, 0.dp, 0.dp, 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(roomText,
                        modifier = Modifier.height(30.dp).padding(end = 4.dp),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Light)
//                    Spacer(Modifier.weight(1f))
                    if(additionalAmount > 0) {
                        Text(
                            modifier = Modifier.height(30.dp),
                            text = "+$additionalAmount more",
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = TextDecoration.Underline,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Light
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = {
                        onFavoriteClick()
                    }){
                        Icon(
                            painterResource(id = resource)
                            , "favorites", tint = MaterialTheme.colorScheme.onSecondary)
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

@Composable
fun ChoreViewChores(chores : List<Chore>, onChoreSelected : (String) -> Unit, onFavoriteChanged : (Chore) -> Unit,
                    nestedScrollConnection : NestedScrollConnection){
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
                itemsIndexed(chores) { index, chore ->
                    val additionalAmount = chore.rooms.size-1
                    var roomText = chore.rooms[0].title
                    ChoreCard(
                        chore.title,
                        roomText, additionalAmount, chore.isFavorite,
                        onFavoriteClick = {
                            //Push to data class here
                            onFavoriteChanged(chore) //Pass selected chore through
                        },
                        onCardClick = {
                            onChoreSelected(chore.id)
                        },
                        index
                    )
                }
            }
        }
}

@Composable
fun CreateDialog(header : String, onDismissRequest: () -> Unit, onCreateClick : (String) -> Unit, show : MutableState<Boolean>){
    val title = remember { mutableStateOf("") }

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
                        onValueChange = {title.value = it},
                        modifier = Modifier.padding(vertical = 16.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(KeyboardCapitalization.Sentences),
                        singleLine = true
                    )
                    val buttonColor = MaterialTheme.colorScheme.onSurfaceVariant
                    Row{
                        Spacer(Modifier.weight(1f))
                        TextButton(
                            onClick = {onCreateClick(title.value)},
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
fun ChoreListItem(room : String, title: String, checked : Boolean, onCheckChanged : () -> Unit, showCheckBox : Boolean){
        ListItem(
            overlineContent = {Text(room)},
            headlineContent = {
                Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 1,overflow = TextOverflow.Ellipsis)
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            trailingContent = {
                if (showCheckBox) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { onCheckChanged() },
//                        colors = CheckboxDefaults.colors(
//                            checkedColor = MaterialTheme.colorScheme.primary,
//                            uncheckedColor = MaterialTheme.colorScheme.outline,
//                            checkmarkColor = MaterialTheme.colorScheme.onPrimary
//                        )
                    )
                }
            },
        )

}

@Composable
fun ModeChoreListItem(title: String, trailingContent : @Composable () -> Unit){
    ListItem(
        headlineContent = {
            Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 1,overflow = TextOverflow.Ellipsis)
        },
        trailingContent = {trailingContent()},
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )

}

@Composable
fun ChoreSelectModeHeaderRow(prefix : String, value : String){
    Row(
        Modifier.padding(start = 12.dp, end = 23.dp, top = 12.dp),
        verticalAlignment = Alignment.Bottom
    ){
        Text("Chores", modifier = Modifier.padding(bottom = 4.dp))
        Spacer(Modifier.weight(1f))
        LabelText("$prefix $value")
    }
}

@Composable
fun WorkflowSelectModeRow() {
    val showWorkflows = remember { mutableStateOf(true) }
    Row(
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Workflows")
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = {
                showWorkflows.value = !showWorkflows.value
            }
        ) {
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
            itemsIndexed(scoddFlows) { index, workflow -> //Use the index value to know what workflow to get chores from
                val selected = remember { mutableStateOf(false) } //If user has used mode before pre-fill
                WorkflowSelectCard(workflow.title, selected.value, onClick = {
                    selected.value = !selected.value
                    //Update workflow selected value here
                })
            }
        }
}
}

@Composable
fun AddChoreButton(onClick : () -> Unit){
    Row(
        Modifier.fillMaxWidth().background(Color.Transparent),
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = {onClick()},
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
        ){
            Icon(Icons.Default.Add, "add")
            Text("Add Chore")
        } }

}