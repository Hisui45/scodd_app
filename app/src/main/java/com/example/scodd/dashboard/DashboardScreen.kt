package com.example.scodd.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scodd.R
import com.example.scodd.ui.theme.ScoddTheme
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun DashboardScreen(){
    Surface{
        Column {
            Greeting("Jade")
            Header("6:00PM")
            Overview(12, 4)
            Message("It doesn't have to be perfect.")
            ActionCards()
        }
    }

}
@Composable
fun Greeting(name: String) {
    var hour by remember { mutableStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onTertiary),
            shape = RoundedCornerShape(size = 12.dp)
        ){
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(9.dp)
            ) {
                val period = getPeriod(hour)
                val resource = if (period == "Evening" || period == "Night") R.drawable.bedtime_24 else R.drawable.light_mode_24px

                Icon(
                    painterResource(id = resource ),
                    contentDescription = stringResource(R.string.sun_content_desc)
                )
                Text(text = "Good $period, $name!",
                    style = MaterialTheme.typography.headlineSmall,

                    )

            }

        }
    }






    LaunchedEffect(Unit) {
        while (true) {
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            delay(3600000) // Update hour every hour
        }
    }


}

@Composable
fun Header(time : String){

    val outline = MaterialTheme.colorScheme.outline
    Row(
        Modifier.padding(18.dp, 0.dp)
    ){

        Column{
            Text(
                text = "Today’s Roundup",
                style = MaterialTheme.typography.titleMedium,
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
            text = "$time",
            style = MaterialTheme.typography.titleLarge,
        )

    }
}

@Composable
fun Overview(numChore : Int, numRoom : Int ){
    ElevatedCard(
        Modifier.fillMaxWidth(1f).padding(14.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant, Color.Black)
    ) {
        Row(
            Modifier.fillMaxWidth(1f).height(IntrinsicSize.Min),
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
                        text = "$numChore",
                        style = MaterialTheme.typography.displayLarge,
                    )
                    Text(
                        text = "$numChore",
                        style = MaterialTheme.typography.displayMedium,
                    )
                }
                Text(
                    text = if (numChore > 1) "Chores" else "Chore",
                    style = MaterialTheme.typography.displaySmall
                )
            }
            Divider(
                Modifier
                    .padding(0.dp)
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(color = Color(0xFF000000))
            )
            Column(
                modifier = Modifier.weight(0.50f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.height(110.dp)
                ){
                    Text(
                        text = "$numRoom",
                        style = MaterialTheme.typography.displayLarge,
                    )
                    Text(
                        text = "$numRoom",
                        style = MaterialTheme.typography.displayMedium,
                    )
                }

                Text(
                    text = if (numRoom > 1) "Rooms" else "Room",
                    style = MaterialTheme.typography.displaySmall
                )

            }
        }
    }
}

@Composable
fun Message(message : String){
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("$message",
            style= MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun ActionCards(){

    //Schedule

    Column(Modifier.fillMaxHeight()) {
        ElevatedCard(
            Modifier.fillMaxWidth().padding(14.dp, 14.dp, 14.dp, 14.dp).weight(0.5f),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer,MaterialTheme.colorScheme.onTertiaryContainer)
        ){

            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ){
                IconButton(
                    onClick ={}
                ){
                    Icon(Icons.Default.MoreVert,"More")

                }
                Spacer(Modifier.weight(1f))
                Text("Schedule",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(16.dp))
            }

        }

        //Establishing Habits

        ElevatedCard(
            Modifier.fillMaxWidth().padding(14.dp, 0.dp, 14.dp, 14.dp).weight(0.5f),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer,MaterialTheme.colorScheme.onTertiaryContainer)
        ){
            Column(
                Modifier.fillMaxWidth().weight(0.5f),
                horizontalAlignment = Alignment.End
            ){
                IconButton(
                    onClick ={}
                ){
                    Icon(Icons.Default.MoreVert,"More")

                }
                Spacer(Modifier.weight(1f))
                Text("Establishing Habits",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(16.dp))
            }
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
        "Morning"
    }
}
