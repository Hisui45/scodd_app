package com.example.scodd.ui.chore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.scodd.R
import com.example.scodd.ui.components.AddChoreButton
import com.example.scodd.ui.components.StatusBar
import com.example.scodd.ui.theme.Marigold40

@Composable
fun CreateWorkflowScreen(onAddChoreButtonClick : () -> Unit){
    StatusBar(Marigold40) // Temporary
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(stringResource(R.string.instructions_text))
        AddChoreButton(onClick = {onAddChoreButtonClick()}) //Should create a temp object and pass it through
    }
}