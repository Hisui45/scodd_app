package com.example.scodd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.scodd.components.ScoddBottomBar
import com.example.scodd.components.ScoddTopBar
import com.example.scodd.ui.theme.ScoddTheme

class ScoddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoddApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoddApp(){

    ScoddTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen =
            scoddScreens.find { it.route == currentDestination?.route } ?: Dashboard

        Scaffold(
            topBar = {
                ScoddTopBar()
            },
            bottomBar = { ScoddBottomBar(
                allScreens = scoddScreens,
                onNavSelected = {newScreen ->
                    navController.navigateSingleTopTo(newScreen.route)},
                currentScreen = currentScreen
            ) }
        ) { innerPadding ->
            ScoddNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }

    }

}