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
import com.example.scodd.navigation.*
import com.example.scodd.ui.theme.ScoddTheme
import com.example.scodd.navigation.ModeBottomBar
import com.example.scodd.navigation.ScoddBottomBar
import com.example.scodd.ui.components.ScoddMainTopBar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
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
            scoddBottomNavScreens.find { it.route == currentDestination?.route } ?:
            scoddModeScreens.find { it.route == currentDestination?.route } ?:
            scoddChoreScreens.find { it.route == currentDestination?.route } ?: DashboardNav
        val isMainDestination = scoddBottomNavScreens.any { it.route == currentDestination?.route }
        val isModeDestination = scoddModeScreens.any { it.route == currentDestination?.route }
        val isChoreDestination = scoddChoreScreens.any { it.route == currentDestination?.route }
        Scaffold(
            topBar = {
                if (isMainDestination) {
                    ScoddMainTopBar()
                }
            },
            bottomBar = {
                if (isMainDestination) {
                    ScoddBottomBar(
                        bottomNavScreens = scoddBottomNavScreens,
                        onNavSelected = { newScreen ->
                            navController.navigateSingleTopTo(newScreen.route)},
                        currentScreen = currentScreen
                    )
                }else if(isModeDestination){
                    ModeBottomBar(onStartClick = {})
                }
            }
        ) { innerPadding ->
            ScoddNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
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