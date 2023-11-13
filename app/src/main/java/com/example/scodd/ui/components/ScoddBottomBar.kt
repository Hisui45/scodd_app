package com.example.scodd.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.scodd.navigation.ScoddBottomNavDestination
import com.example.scodd.navigation.ScoddDestination


@Composable
fun ScoddBottomBar(
    bottomNavScreens: List<ScoddBottomNavDestination>,
    onNavSelected: (ScoddBottomNavDestination) -> Unit,
    currentScreen: Any
){


    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.height(74.dp)
    ) {
        bottomNavScreens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(ImageVector.vectorResource(id = screen.icon), screen.route) },
                label = { Text(screen.label) },
                selected = checkScreen(screen, currentScreen),
                onClick = { onNavSelected(screen)},
                colors = NavigationBarItemDefaults.colors(selectedTextColor = MaterialTheme.colorScheme.onSecondary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.tertiary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSecondary)
            )
        }

    }
}

private fun checkScreen(bottomNavScreen : ScoddBottomNavDestination, currentScreen: Any) : Boolean {
    var isSelected: Boolean = when (currentScreen) {
        is ScoddBottomNavDestination -> {
            currentScreen == bottomNavScreen
        }

        is ScoddDestination -> {
            currentScreen.parentRoute == bottomNavScreen.route
        }

        else -> {
            false
        }
    }

    return isSelected
}

@Composable
fun ModeBottomBar(onStartClick : () -> Unit){
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxWidth().padding(8.dp).height(40.dp)
    ){
        Button(
            onClick = {onStartClick()},
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary)
        ){
            Text("START", style = MaterialTheme.typography.titleLarge)
        }
    }

}
