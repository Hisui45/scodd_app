package com.example.scodd.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scodd.R
import com.example.scodd.components.ChoreListItem
import com.example.scodd.objects.ScoddChore
import com.example.scodd.components.SelectableRoomFilterChip
import com.example.scodd.components.StatusBar
import com.example.scodd.objects.scoddChores
import com.example.scodd.objects.scoddRooms
import com.example.scodd.ui.theme.*
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun DashboardScreen(){

    Surface{
        Column {
            StatusBar(White40)
            Greeting("Jade")
            Header("6:00PM")
            Overview(12, 4)
            Chores()
        }
    }
}
@Composable
fun Greeting(name: String) {
    var hour by remember { mutableStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }

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
            Text(text = "Good $period, $name",
                style = MaterialTheme.typography.headlineSmall
            )

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
fun Header(time : String){
    val outline = MaterialTheme.colorScheme.outline
    Row(
        Modifier.padding(12.dp)
    ){

        Column{
            Text(
                text = "Today’s Roundup",
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
fun Overview(numChore : Int, numRoom : Int ){
    Card(
        Modifier.fillMaxWidth(1f).padding(12.dp, 6.dp),
        colors = CardDefaults.cardColors(Color.Transparent, MaterialTheme.colorScheme.onBackground),
    ) {
        Row(
            Modifier.fillMaxWidth(1f).height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            OverviewComponent(numChore, "Chore")

//            Divider(
//                Modifier
//                    .padding(0.dp)
//                    .width(1.dp)
//                    .fillMaxHeight()
//                    .background(color = Color(0xFF000000))
//            )

            OverviewComponent(numRoom, "Room")
        }
        Divider(color = MaterialTheme.colorScheme.secondary, thickness = 8.dp)
    }
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
                Modifier.height(110.dp)
            ){
                Text(
                    text = "$number",
                    style = TextStyle(
                        fontFamily = londrinaShadow,
                        fontWeight = FontWeight.Normal,
                        fontSize = 100.sp,
                        lineHeight = 28.sp,
                        letterSpacing = 0.sp
                    ),
                    color = Marigold40,
                )
                Text(
                    text = "$number",
                    style = TextStyle(
                        fontFamily = londrinaSolid,
                        fontWeight = FontWeight.Normal,
                        fontSize = 100.sp,
                        lineHeight = 28.sp,
                        letterSpacing = 0.sp
                    ),
                    color = Burgundy40,
                )
            }
            Text(
                text = if (number > 1) text+"s" else text,
                style = MaterialTheme.typography.displayMedium
            )
        }

    }

}

@Composable
fun Message(message : String){
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(message,
            style= MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun FocusArea(){
    Column(
        modifier = Modifier.fillMaxWidth(1f).padding(12.dp, 0.dp)
    ){
        Text("Focus Area",
            style = MaterialTheme.typography.titleLarge)
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ){
        itemsIndexed(scoddRooms){ index, room -> //Use index to know what room is selected
            val selected = remember { mutableStateOf(false) } //Take from room object to pre-fill value
            SelectableRoomFilterChip(room.title, selected.value,
                onSelectedChanged = {
                    selected.value = !selected.value
                    //Update room selected value here


                })
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoreList(){
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        itemsIndexed(scoddChores){ index, chore ->
            val checked = remember { mutableStateOf(false) }
            SwipeToDismiss(
                state = rememberDismissState(),
                background = {},
                dismissContent = {
                    Column{
                        ChoreListItem(chore.room.title, chore.title,checked.value,
                            onCheckChanged = {
                                checked.value = !checked.value
                            }, true)
                        if (index < scoddChores.lastIndex)
                        Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                    }
                })


        }
    }
}

@Composable
fun Chores(){
    if(scoddChores.isEmpty()){
        Message("It doesn't have to be perfect.")
        Text("Nothing to see here.")
    }else{
        FocusArea()
        ChoreList()
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
        Greeting("Jade")
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
