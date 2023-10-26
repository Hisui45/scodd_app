package com.example.scodd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.scodd.components.ScoddBottomBar
import com.example.scodd.components.ScoddMainTopBar
import com.example.scodd.components.ScoddSmallTopBar
import com.example.scodd.components.StatusBar
import com.example.scodd.objects.Dashboard
import com.example.scodd.objects.scoddChoreScreens
import com.example.scodd.objects.scoddModeScreens
import com.example.scodd.objects.scoddScreens
import com.example.scodd.ui.theme.Marigold40
import com.example.scodd.ui.theme.ScoddTheme
import com.example.scodd.ui.theme.White40

class ScoddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoddApp()
        }
    }
}

@Composable
fun ScoddApp(){

    ScoddTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen =
            scoddScreens.find { it.route == currentDestination?.route } ?:
            scoddModeScreens.find { it.route == currentDestination?.route } ?:
            scoddChoreScreens.find { it.route == currentDestination?.route } ?: Dashboard
        val isMainDestination = scoddScreens.any { it.route == currentDestination?.route }
        val isModeDestination = scoddModeScreens.any { it.route == currentDestination?.route }
        val isChoreDestination = scoddChoreScreens.any { it.route == currentDestination?.route }
        val focusManager = LocalFocusManager.current

        if(isMainDestination || isChoreDestination){
            StatusBar(White40)
        }else if(isModeDestination){
            StatusBar(Marigold40)
        }

        Scaffold(
            topBar = {
                if (isMainDestination) {
                    ScoddMainTopBar()
                }else if(isChoreDestination){
                    ScoddSmallTopBar(currentScreen.route,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        focusManager
                    )
                }
            },
            bottomBar = {
                if (isMainDestination) {
                    ScoddBottomBar(
                        allScreens = scoddScreens,
                        onNavSelected = {newScreen ->
                            navController.navigateSingleTopTo(newScreen.route)},
                        currentScreen = currentScreen
                    )
                }
            }
        ) { innerPadding ->
            ScoddNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                focusManager
            )
        }

    }

}