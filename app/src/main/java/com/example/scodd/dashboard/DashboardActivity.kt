package com.example.scodd.dashboard

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
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
fun Header(time : String){
    Row(
        Modifier.padding(16.dp, 0.dp, 12.dp, 0.dp )
    ){

        Column{
            Text(
                text = "Today’s Roundup",
                style = MaterialTheme.typography.titleLarge,
                )
            Divider(
                Modifier
                    .padding(0.dp)
                    .width(134.dp)  //figure out how to grow with text
                    .height(1.dp)
                    .background(color = Color(0xFF000000))
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = "$time",
            style = MaterialTheme.typography.titleLarge,
        )

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



@Composable
fun DashboardScreen(){

    // A surface container using the 'background' color from the theme
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            Greeting("Jade")
            Header("6:00PM")
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