package com.example.scodd.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CustomFloatingActionButton(
    expandable: Boolean,
    topFloatingActionButton: @Composable () -> Unit,
    bottomFloatingActionButton: @Composable () -> Unit,
    fabIcon: ImageVector
) {
    var isExpanded by remember { mutableStateOf(false) }
    if (!expandable) { // Close the expanded fab if you change to non-expandable nav destination
        isExpanded = false
    }

    val fabSize = 64.dp
    val expandedFabWidth by animateDpAsState(
        targetValue = if (isExpanded) 70.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f)
    )
    val expandedFabHeight by animateDpAsState(
        targetValue = if (isExpanded) 70.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally){

        // ExpandedBox over the FAB
        Box(
            modifier = Modifier
                .offset(y = (25).dp)
                .size(
                    width = (animateDpAsState(if (isExpanded) 100.dp else 0.dp, animationSpec = spring(dampingRatio = 0.9f))).value,
                    height = (animateDpAsState(if (isExpanded) 215.dp else 0.dp, animationSpec = spring(dampingRatio = 4f))).value)
                .background(
                    Color.Transparent,
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Customize the content of the expanded box as needed
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ){
                topFloatingActionButton()
                bottomFloatingActionButton()

            }

        }

        FloatingActionButton(
            onClick = {
                if (expandable) {
                    isExpanded = !isExpanded
                }
            },
            modifier = Modifier
                .width(expandedFabWidth)
                .height(expandedFabHeight),
            shape = RoundedCornerShape(18.dp),
//            containerColor = MaterialTheme.colorScheme.primary,
//            contentColor = MaterialTheme.colorScheme.onPrimary

        ) {

            Icon(
                imageVector = fabIcon,
                contentDescription = null
            )
        }
    }
}


@Composable
fun ExpandableFABButtonItem(onButtonClick: () -> Unit, title : String, icon : ImageVector){
    FilledIconButton(
        onClick = {onButtonClick()},
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.size(48.dp).padding(bottom = 3.dp),
        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
//            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ){
        Icon(icon, "Add $title",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
    Card(
        modifier = Modifier.requiredWidthIn(min = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface),
        shape = RoundedCornerShape(4.dp)
    ){
        Text(title, style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(horizontal = 8.dp))
    }

}