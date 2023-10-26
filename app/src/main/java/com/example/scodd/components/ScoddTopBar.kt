package com.example.scodd.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
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
fun ScoddSmallTopBar(title : String, onNavigateBack : () -> Unit, focusManager: FocusManager){
    var value by rememberSaveable { mutableStateOf(title) }
    TopAppBar(
        title = {
                AppBarTextField(
                    value = value,
                    onValueChange = { newValue -> value = newValue },
                    hint = "descriptive name",
                    focusManager = focusManager
                    )
                },
        navigationIcon = {
            IconButton(
                onClick = {onNavigateBack()}
            ){
                Icon(Icons.Default.ArrowBack, "back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondary,
            actionIconContentColor = MaterialTheme.colorScheme.onSecondary,
            navigationIconContentColor = MaterialTheme.colorScheme.onSecondary,
            titleContentColor = MaterialTheme.colorScheme.onSecondary),
        actions = {
            IconButton(
                onClick = {}
            ){
                Icon(Icons.Default.MoreVert, "more")
            }
        }
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
) {
    val interactionSource = remember { MutableInteractionSource() }
    val textStyle = LocalTextStyle.current
    // make sure there is no background color in the decoration box
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent
    )

    // If color is not provided via the text style, use content color as a default
    val textColor = MaterialTheme.colorScheme.onSecondary

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
                    isError = false,
                    interactionSource = interactionSource,
                    colors = colors
                )
//                .focusRequester(focusRequester)
                ,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSecondary),
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
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
                    isError = false,
                    placeholder = { Text(text = hint) },
                    colors = colors,
                    contentPadding = PaddingValues(bottom = 4.dp),
                )
            }
        )
    }
}