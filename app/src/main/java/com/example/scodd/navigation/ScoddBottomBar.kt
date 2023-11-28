package com.example.scodd.navigation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp


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
fun ModeBottomBar(
        enabled : Boolean,
        onStartClick : () -> Unit){
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxWidth().padding(8.dp).height(40.dp)
    ){
        Button(
            enabled = enabled,
            onClick = {onStartClick()},
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary)
        ){
            Text("START", style = MaterialTheme.typography.titleLarge)
        }
    }

}
