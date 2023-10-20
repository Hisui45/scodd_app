package com.example.scodd.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.scodd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoddTopBar(){
    CenterAlignedTopAppBar(
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
                Icon(Icons.Default.AccountCircle, "account", tint = MaterialTheme.colorScheme.onBackground)
            }
        }
    )
}