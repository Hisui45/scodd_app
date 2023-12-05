package com.example.scodd.navigation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.example.scodd.R


@Composable
fun ScoddBottomBar(
    bottomNavScreens: List<ScoddBottomNavDestination>,
    onNavSelected: (ScoddBottomNavDestination) -> Unit,
    currentScreen: Any
){

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
//        modifier = Modifier.height(74.dp)
    ) {
        bottomNavScreens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(painterResource(screen.icon), screen.label) },
                label = { Text(screen.label, style = MaterialTheme.typography.labelLarge) },
                selected = checkScreen(screen, currentScreen),
                onClick = { onNavSelected(screen)},
                colors = NavigationBarItemDefaults.colors(selectedTextColor = MaterialTheme.colorScheme.secondary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.secondary,
                    )
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
