package com.example.scodd.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scodd.R
import com.example.scodd.ui.theme.ScoddTheme
import kotlinx.coroutines.delay
import java.util.*

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ScoddTheme {
                DashboardScreen()


            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(name: String) {
    var hour by remember { mutableStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp, 10.dp)
    ) {
        Card(
            modifier = Modifier.width(400.dp).height(57.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer),
            shape = RoundedCornerShape(size = 12.dp)
        ){
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp, 10.dp, 10.dp,10.dp)
            ) {

                Icon(
                    painterResource(id = R.drawable.sun_icon),
                    contentDescription = stringResource(R.string.sun_content_desc),
                    Modifier.width(24.dp).height(24.dp).padding(1.dp)
                )
                Text(text = "Good ${getPeriod(hour)}, $name!",
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
fun TopBar(){
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.logo_vector),
                contentDescription = stringResource(R.string.logo_content_desc),
                contentScale = ContentScale.Inside,
                modifier = Modifier.size(145.dp)
            )
        },
        actions = {
            IconButton(
                onClick = {},
            ){
                 Icon(Icons.Default.AccountCircle, "account")
            }
        }
    )
}

@Composable
fun Header(time : String){

    val outline = MaterialTheme.colorScheme.outline
    Row(
        Modifier.padding(16.dp, 0.dp, 12.dp, 0.dp )
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
            text = "$time",
            style = MaterialTheme.typography.titleLarge,
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
               Box{
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
                Box{
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
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("$message",
            style= MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCards(){

    Column(Modifier.fillMaxHeight()) {
        ElevatedCard(
            Modifier.fillMaxWidth().padding(14.dp).weight(0.5f),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
        ){
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.End
            ){
                Icon(Icons.Default.MoreVert,"More")
                Spacer(Modifier.weight(1f))
                Text("Schedule", style = MaterialTheme.typography.titleSmall)
            }

        }

        ElevatedCard(
            Modifier.fillMaxWidth().padding(14.dp).weight(0.5f),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
        ){
            Column(
                Modifier.fillMaxWidth().padding(16.dp).weight(0.5f),
                horizontalAlignment = Alignment.End
            ){
                Icon(Icons.Default.MoreVert,"More")
                Spacer(Modifier.weight(1f))
                Text("Establishing Habits", style = MaterialTheme.typography.titleSmall)
            }
        }

    }


}

@Composable
fun NavigationBar(){

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {

        IconButton(
            onClick = { /* Handle navigation icon click */ }
        ) {
            Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
        }

        IconButton(
            onClick = { /* Handle navigation icon click */ }
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        }

        IconButton(
            onClick = { /* Handle navigation icon click */ }
        ) {
            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile")
        }
    }
}

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Chores : Screen("chore")
    object Modes : Screen("mode")

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(){
    // A surface container using the 'background' color from the theme

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {TopBar()},
            bottomBar = {NavigationBar()},
            content = {
                Surface(Modifier.padding(it)){
                    Column {
                    Greeting("Jade")
                    Header("6:00PM")
                    Overview(12, 4)
                    Message("It doesn't have to be perfect.")
                    ActionCards()
                    }
                }
               }
        )
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