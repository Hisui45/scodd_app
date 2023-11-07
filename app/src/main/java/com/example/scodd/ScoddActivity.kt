package com.example.scodd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.scodd.components.*
import com.example.scodd.objects.*
import com.example.scodd.ui.theme.Burgundy40
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
        val WORKFLOW_CREATE = 1
        var createType = 0
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen =
            scoddScreens.find { it.route == currentDestination?.route } ?:
            scoddModeScreens.find { it.route == currentDestination?.route } ?:
            scoddChoreScreens.find { it.route == currentDestination?.route } ?: Dashboard
//            scoddWorkflowScreens.find { it.route == currentDestination?.route } ?:
        val isMainDestination = scoddScreens.any { it.route == currentDestination?.route }
        val isModeDestination = scoddModeScreens.any { it.route == currentDestination?.route }
        val isChoreDestination = scoddChoreScreens.any { it.route == currentDestination?.route }
//        val isWorkflowDestination = scoddWorkflowScreens.any { it.route == currentDestination?.route }
        val focusManager = LocalFocusManager.current

        Scaffold(
            topBar = {
                if (isMainDestination) {
                    ScoddMainTopBar()
                }else if(isChoreDestination){
                    if(currentScreen.route == Chore.CreateWorkflow.route){
                        createType = WORKFLOW_CREATE
                    }
                    ScoddSmallTopBar(currentScreen.route,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        focusManager, createType
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
                }else if(isModeDestination){
                    ModeBottomBar(onStartClick = {})
                }
            }
        ) { innerPadding ->
            ScoddNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                focusManager
            )
//            if(isMainDestination || isChoreDestination){ //Organize these
//                StatusBar(White40)
//            }else if(isModeDestination){ // of all destinations for bar color once all screens are made
//                StatusBar(Marigold40)
//            }else{
//                StatusBar(Burgundy40)
//            }
        }

    }

}