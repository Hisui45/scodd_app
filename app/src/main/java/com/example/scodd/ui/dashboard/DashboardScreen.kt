package com.example.scodd.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scodd.R
import com.example.scodd.ui.components.StatusBar
import com.example.scodd.model.ChoreItem
import com.example.scodd.ui.components.ChoreListItem
import com.example.scodd.ui.components.FilterRoomsContent
import com.example.scodd.ui.theme.*
import com.example.scodd.utils.LazyAnimations
import kotlinx.coroutines.delay
import java.util.*

/**
 * TODO: long click delete
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    ){

    var hour by remember { mutableStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)})
        { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues = paddingValues)
            ) {
                StatusBar(White40)
                Greeting(uiState.userName, hour)
                Header(viewModel.getEarliestTime())
                Overview(viewModel.getTotalChoreItemAmount(), viewModel.getDistinctRoomAmount(), hour)
                FilterRoomsContent(
                    roomChips = uiState.rooms,
                    toggleChip = viewModel :: toggleRoom,
                    favoriteSelected = viewModel.getFavorite(),
                    onFavoriteChipSelected = viewModel :: setFavoriteFilterType
                )
                ChoreList(
                    choreItems = uiState.filteredChoreItems,
                    choreTitle = viewModel :: getChoreTitle,
                    roomTitle = viewModel :: getRoomTitle,
                    additionalAmount = viewModel :: getAdditionalAmount,
                    completeItem = viewModel :: setCompleted,
                    onClearClicked = viewModel :: clearAll,
                    onClearCompletedClicked = viewModel :: clearCompleted,
                    onDeleteClicked = viewModel :: deleteItem
                )
            }
    }

    uiState.userMessage?.let { userMessage ->
        val snackbarText = stringResource(userMessage)
        LaunchedEffect(scope, viewModel, userMessage, snackbarText) {
            snackbarHostState.showSnackbar(message = snackbarText, duration = SnackbarDuration.Short)
            viewModel.snackbarMessageShown()
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            delay(3600000) // Update every hour
        }
    }

}
@Composable
fun Greeting(name: String, hour: Int) {

    Card(
        modifier = Modifier.fillMaxWidth().padding(6.dp, 6.dp, 6.dp, 0.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(size = 12.dp)
    ){
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            val period = getPeriod(hour)
            val resource = if (period == "Evening" || period == "Night") R.drawable.bedtime_24 else R.drawable.light_mode_24px

            Icon(
                painterResource(id = resource ),
                contentDescription = stringResource(R.string.sun_content_desc)
            )
            if(name.isEmpty()){
                Text(text = "Good $period",
                    style = MaterialTheme.typography.headlineSmall
                )
            }else{
                Text(text = "Good $period, $name",
                    style = MaterialTheme.typography.headlineSmall
                )
            }


        }

    }
}

@Composable
fun Header(time : String){
    val outline = MaterialTheme.colorScheme.outline
    Row(
        Modifier.padding(12.dp)
    ){

        Column{
            Text(
                text = stringResource(R.string.roundup_header),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.drawBehind {
                    val strokeWidthPx = 1.dp.toPx()
                    val verticalOffset = size.height + 1.sp.toPx()
                    drawLine(
                        color = outline,
                        strokeWidth = strokeWidthPx,
                        start = Offset(0f, verticalOffset),
                        end = Offset(size.width, verticalOffset)
                    )
                }
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = time,
            style = MaterialTheme.typography.titleLarge,
//            color = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}

@Composable
fun Overview(numChore : Int, numRoom : Int, hour: Int ){
    var skyColor: Color
    var sunColor: Color
    var grassColor: Color
    var positionY = 25f
    when (hour) {
        in 6..11 -> {
            //Morning
            skyColor = Color(0xFF87CEEB)
            sunColor = Color(0xFFFFFAA0)
            grassColor = Color(0xFF4F7942)
            positionY = 25f
        }

        in 12..16 -> {
            //Afternoon
            skyColor = Color(0xFF87CEEB)
            sunColor = Color(0xFFf9d71c)
            grassColor = Color(0xFF4F7942)
            positionY = 25f
        }

        in 17..20 -> {
//            "Evening"
            skyColor = Color(0xFF141852)
            sunColor = Color.White
            grassColor = Color(0xFF355E3B)
            positionY = 25f
        }

        in 21..23 -> {
//            "Night"
            skyColor = Color(0xFF141c3a)
            sunColor = Color(0xFFe5e5e5)
            grassColor = Color(0xFF4F7942)
            positionY = 25f
        }
        else -> {
//            "Night For A Night Owl"
            skyColor = Color(0xFF141c3a)
            sunColor = Color.White//Color(0xFFdcdcdc)
            grassColor = Color(0xFF355E3B)
            positionY = 25f
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth().height(200.dp).padding(12.dp, 6.dp),
        colors = CardDefaults.cardColors(containerColor = skyColor),
//        border = BorderStroke(1.dp,Color.Black)

    ) {
        val size = 300.dp
        val color = sunColor
        val radius = with(LocalDensity.current) { size.toPx() / 2 }
        Box(
            contentAlignment = Alignment.BottomEnd
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ){
                // Draw the sun
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(1f).height(100.dp)
                        .background(Color.Transparent)
                ) {

                    val offset = Offset(size.toPx() / 4f, -size.toPx() / positionY)
                    drawArc(
                        color = color,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        style = Fill,
                        size = Size(radius * 2, radius * 2),
                        topLeft = Offset(offset.x, offset.y)
                    )
                }
                // Draw the grass
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(60.dp)
                        .background(grassColor)
                )
            }
            Row(
                Modifier.fillMaxWidth(1f).height(IntrinsicSize.Min).padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ){
                OverviewComponent(numChore, "Chore")


                OverviewComponent(numRoom, "Room")
            }
        }
    }
//    Card(
//        Modifier.fillMaxWidth(1f).padding(12.dp, 6.dp),
//        colors = CardDefaults.cardColors(Color.Transparent, MaterialTheme.colorScheme.onBackground),
//    ) {
//
//        Divider(color = MaterialTheme.colorScheme.secondary, thickness = 8.dp)
//    }
}

@Composable
fun OverviewComponent(number : Int, text : String){
    Row(
        Modifier.width(IntrinsicSize.Min).height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        Column(
            modifier = Modifier.weight(0.50f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Box(
                Modifier.height(122.dp)
            ){
//                Text(
//                    text = "$number",
//                    style = TextStyle(
//                        fontFamily = londrinaShadow,
//                        fontWeight = FontWeight.Normal,
//                        fontSize = 100.sp,
//                        lineHeight = 28.sp,
//                        letterSpacing = 0.sp
//                    ),
//                    color = Color.LightGray,
//                )
                Text(
                    text = "$number",
                    style = TextStyle(
                        fontFamily = londrinaSolid,
                        fontWeight = FontWeight.Normal,
                        fontSize = 100.sp,
                        lineHeight = 28.sp,
                        letterSpacing = 0.sp
                    ),
                    color = RoosterRed40,
                )
            }
            Text(
                text = if (number > 1) text+"s" else text,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

    }

}

//@Composable
//fun Message(message : String){
//    Surface(
//        color = MaterialTheme.colorScheme.primaryContainer,
//        modifier = Modifier.fillMaxWidth(),
//    ) {
//        Text(message,
//            style= MaterialTheme.typography.titleMedium,
//            fontWeight = FontWeight.Light,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.padding(8.dp),
//            color = MaterialTheme.colorScheme.onPrimaryContainer
//        )
//    }
//}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChoreList(
    choreItems: Set<ChoreItem>,
    choreTitle: (String) -> String,
    roomTitle: (String, Int) -> String,
    additionalAmount: (String) -> Int,
    completeItem: (String, Boolean) -> Unit,
    onClearClicked: () -> Unit,
    onClearCompletedClicked: () -> Unit,
    onDeleteClicked: (String) -> Unit
){
    val focusManager =  LocalFocusManager.current
    val hideCompleted = rememberSaveable { mutableStateOf(false) }

    if(choreItems.isNotEmpty()){
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
        ){
            item(key = 0){
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(stringResource(R.string.horizon), style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = {onClearClicked()}
                    ){
                        Text(stringResource(R.string.clear_all), color = MaterialTheme.colorScheme.outline)
                    }
                    if(choreItems.any { it.isComplete }){
                        val text = if (hideCompleted.value) R.string.show_completed else R.string.hide_completed
                        TextButton(
                            onClick = {hideCompleted.value = !hideCompleted.value}
                        ){
                            Text(stringResource(text), color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }

            }
            itemsIndexed(choreItems.filter { !it.isComplete }){ index, choreItem ->

                val showDelete = remember { mutableStateOf(false)}
                ChoreListItem(
                    modifier = Modifier.animateItemPlacement(LazyAnimations.WORKFLOW.animation)
                        .combinedClickable(
                            onClick = {
                                focusManager.clearFocus()
                                showDelete.value = false
                            },
                            onLongClick = {
                                showDelete.value = true
                            }
                        ),
                    firstRoom = roomTitle(choreItem.parentChoreId, 0),
                    additionalAmount = additionalAmount(choreItem.parentChoreId),
                    title = choreTitle(choreItem.parentChoreId),
                    isComplete = choreItem.isComplete,
                    onCheckChanged = {
                        completeItem(choreItem.id, it)
                        focusManager.clearFocus()
                    },
                    showCheckBox = true,
                    showDelete = showDelete.value,
                    onDeleteClicked = {onDeleteClicked(choreItem.id); showDelete.value = false})
                if (index < choreItems.filter { !it.isComplete }.lastIndex)
                    Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
            }
            if(!hideCompleted.value){
                if(choreItems.any { it.isComplete }){
                    item{
                        Row(
                            modifier = Modifier.fillMaxWidth(1f).padding(vertical = 1.dp),
//                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(stringResource(R.string.completed), style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.weight(1f))
                            TextButton(
                                onClick = {onClearCompletedClicked()}
                            ){
                                Text(stringResource(R.string.clear), color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                    itemsIndexed(choreItems.filter { it.isComplete }){ index, choreItem ->
                        val showDelete = remember { mutableStateOf(false)}
                        ChoreListItem(
                            modifier = Modifier.animateItemPlacement(LazyAnimations.WORKFLOW.animation)
                                .combinedClickable(
                                    onClick = {
                                        focusManager.clearFocus()
                                        showDelete.value = false
                                    },
                                    onLongClick = {
                                        showDelete.value = true
                                    }
                                ),
                            firstRoom = roomTitle(choreItem.parentChoreId, 0),
                            additionalAmount = additionalAmount(choreItem.parentChoreId),
                            title = choreTitle(choreItem.parentChoreId),
                            isComplete = choreItem.isComplete,
                            onCheckChanged = {
                                completeItem(choreItem.id, it)
                                focusManager.clearFocus()
                            },
                            showCheckBox = true,
                            showDelete = showDelete.value,
                            onDeleteClicked = {onDeleteClicked(choreItem.id); showDelete.value = false}
                            )
                        if (index < choreItems.filter { it.isComplete }.lastIndex)
                            Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                    }

                }
            }

        }
    }else{
        Column(
            Modifier.fillMaxSize(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.dashboard_no_chores))
        }
    }

}

@Preview //(showSystemUi = true)
@Composable
fun DashboardPreview() {
    ScoddTheme {
        DashboardScreen()
    }
}


@Preview //(showSystemUi = true)
@Composable
fun GreetingPreview() {
    ScoddTheme {
        Greeting("Jade", 16)
    }
}

@Preview //(showSystemUi = true)
@Composable
fun HeaderPreview() {
    ScoddTheme {
        Header("6:00PM")
    }
}

private fun getPeriod(hour: Int) = when (hour) {
    in 6..11 -> {
        "Morning"
    }

    in 12..16 -> {
        "Afternoon"
    }

    in 17..20 -> {
        "Evening"
    }

    in 21..23 -> {
        "Night"
    }

    else -> {
        "Night For A Night Owl"
    }
}

//@Composable
//fun Overview(numChore : Int, numRoom : Int ){
//    Card(
//        Modifier.fillMaxWidth(1f).padding(12.dp, 6.dp),
//        colors = CardDefaults.cardColors(Color.Transparent, MaterialTheme.colorScheme.onBackground),
//    ) {
//        Row(
//            Modifier.fillMaxWidth(1f).height(IntrinsicSize.Min),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ){
//            OverviewComponent(numChore, "Chore")
//
////            Divider(
////                Modifier
////                    .padding(0.dp)
////                    .width(1.dp)
////                    .fillMaxHeight()
////                    .background(color = Color(0xFF000000))
////            )
//
//            OverviewComponent(numRoom, "Room")
//        }
//        Divider(color = MaterialTheme.colorScheme.secondary, thickness = 8.dp)
//    }
//}

//@Composable
//fun OverviewComponent(number : Int, text : String){
//    Row(
//        Modifier.width(IntrinsicSize.Min).height(IntrinsicSize.Min),
//        horizontalArrangement = Arrangement.SpaceEvenly
//    ){
//        Column(
//            modifier = Modifier.weight(0.50f).fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ){
//            Box(
//                Modifier.height(110.dp)
//            ){
//                Text(
//                    text = "$number",
//                    style = TextStyle(
//                        fontFamily = londrinaShadow,
//                        fontWeight = FontWeight.Normal,
//                        fontSize = 100.sp,
//                        lineHeight = 28.sp,
//                        letterSpacing = 0.sp
//                    ),
//                    color = Marigold40,
//                )
//                Text(
//                    text = "$number",
//                    style = TextStyle(
//                        fontFamily = londrinaSolid,
//                        fontWeight = FontWeight.Normal,
//                        fontSize = 100.sp,
//                        lineHeight = 28.sp,
//                        letterSpacing = 0.sp
//                    ),
//                    color = Burgundy40,
//                )
//            }
//            Text(
//                text = if (number > 1) text+"s" else text,
//                style = MaterialTheme.typography.displayMedium
//            )
//        }
//
//    }
//
//}
