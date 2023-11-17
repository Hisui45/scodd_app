package com.example.scodd.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scodd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoddMainTopBar(){
    CenterAlignedTopAppBar(
        navigationIcon = {
             IconButton(
                 onClick = {}
             ){
                 Icon(Icons.Default.DateRange, "schedule", tint = MaterialTheme.colorScheme.outline)
             }
        },
        title = {
            Image(
                painter = painterResource(id = R.drawable.logo_vector),
                contentDescription = stringResource(R.string.logo_content_desc),
                contentScale = ContentScale.Inside,
                modifier = Modifier.size(145.dp)
            )
        },
        actions = {
            IconButton(
                onClick = {},
            ){
                Icon(Icons.Default.AccountCircle, "account", tint = MaterialTheme.colorScheme.outline)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldTopBar(title : String,
                    onNavigateBack : () -> Unit,
                    focusManager: FocusManager,
                    type : TextFieldTopBarType,
                    onTitleChanged : (String) -> Unit,
                    actions: @Composable() () -> Unit,
                    contentColor: Color
){

    val isError = remember { mutableStateOf(false) }
    fun validate(value : String){isError.value = value.isEmpty()}

    var colors: TopAppBarColors = when(type){
        TextFieldTopBarType.CHORE->{
            TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondary,
                navigationIconContentColor = MaterialTheme.colorScheme.onSecondary)
        }
        TextFieldTopBarType.WORKFLOW->{
            TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary, actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
    TopAppBar(
        title = {
            AppBarTextField(
                value = title,
                onValueChange = {onTitleChanged(it) ; validate(it)},
                hint = stringResource(R.string.title_hint),
                focusManager = focusManager,
                isError = isError.value,
                contentColor = contentColor,
                )
            },
        navigationIcon = { NavigationButton(onNavigateBack) },
        colors = colors,
        actions = {actions()}
    )
}

/**
 * Stack Overflow : Stypox
 * https://stackoverflow.com/questions/73664765/showing-a-text-field-in-the-app-bar-in-jetpack-compose-with-material3
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    focusManager : FocusManager,
    isError : Boolean,
    contentColor : Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val textStyle = LocalTextStyle.current
    // make sure there is no background color in the decoration box
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        errorContainerColor = MaterialTheme.colorScheme.inversePrimary.copy(0.20f)
    )

    // If color is not provided via the text style, use content color as a default
    val textColor = contentColor

    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor, lineHeight = 50.sp))

    // request focus when this composable is first initialized
//    val focusRequester = FocusRequester()
//    SideEffect {
//        focusRequester.requestFocus()
//    }

    // set the correct cursor position when this composable is first initialized
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(value, TextRange(value.length)))
    }
    textFieldValue = textFieldValue.copy(text = value) // make sure to keep the value updated

    CompositionLocalProvider(
        LocalTextSelectionColors provides LocalTextSelectionColors.current
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                // remove newlines to avoid strange layout issues, and also because singleLine=true
                onValueChange(it.text.replace("\n", ""))
            },
            modifier = modifier
                .fillMaxWidth()
                .heightIn(32.dp)
                .indicatorLine(
                    enabled = true,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors
                )
//                .focusRequester(focusRequester)
                ,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSecondary),
            keyboardOptions = keyboardOptions.copy(KeyboardCapitalization.Sentences, autoCorrect = true),
            keyboardActions = KeyboardActions{focusManager.clearFocus()},
            interactionSource = interactionSource,
            singleLine = true,
            decorationBox = { innerTextField ->
                // places text field with placeholder and appropriate bottom padding
                TextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = isError,
                    placeholder = {
                        Text(
                            text = hint,
                            style = mergedTextStyle,
                            color = MaterialTheme.colorScheme.inversePrimary
                        )},
                    colors = colors,
                    contentPadding = PaddingValues(bottom = 4.dp),
                    shape = RectangleShape
                )
            }
        )
    }
}
@Composable
fun ContextMenu(
    tint: Color,
    dropDownItems: @Composable() (expanded: MutableState<Boolean>) -> Unit

){
    val expanded = remember { mutableStateOf(false) }
    Row{
        IconButton(onClick = {expanded.value = !expanded.value}){Icon(Icons.Default.MoreVert, stringResource(R.string.options), tint = tint) }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            dropDownItems(expanded)
        }
    }
}

@Composable
fun NavigationButton(onNavigateBack: () -> Unit){
    IconButton(
        onClick = {onNavigateBack()}
    ){
        Icon(Icons.Default.ArrowBack, "back")
    }
}

enum class TextFieldTopBarType{
    WORKFLOW,
    CHORE
}