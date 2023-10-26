package com.example.scodd.components
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.scodd.objects.ScoddDestination


@Composable
fun ScoddBottomBar(
    allScreens: List<ScoddDestination>,
    onNavSelected: (ScoddDestination) -> Unit,
    currentScreen: ScoddDestination
){

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.height(74.dp)
    ) {
        allScreens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(ImageVector.vectorResource(id = screen.icon), screen.route) },
                label = { Text(screen.route) },
                selected = currentScreen == screen || currentScreen.parentRoute == screen.route,
                onClick = { onNavSelected(screen)},
                colors = NavigationBarItemDefaults.colors(selectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer)
            )
        }

    }
}
