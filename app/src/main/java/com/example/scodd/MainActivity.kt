package com.example.scodd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scodd.ui.theme.ScoddTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoddTheme {
                // A surface container using the 'secondaryContainer' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.secondaryContainer) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp, 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_vector),
                            contentDescription = "SCODD",
                            contentScale = ContentScale.None,
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp, 16.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.rooster_png),
                            contentDescription = "picture of a rooster with a mop in hand and" +
                                    " bucket on it's head covered in water",
                            contentScale = ContentScale.FillHeight,
                            modifier = Modifier.height(300.dp).width(300.dp)
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ){
                        CreateAccountButton(onClick = {})
                        LogInButton(onClick = {})
                    }

                }
            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}


@Composable
fun Logo(){

}


@Composable
fun CreateAccountButton(onClick: () -> Unit){
    Button(onClick = { onClick() },
        modifier = Modifier.width(380.dp).height(40.dp)){
        Text("Create Account")
    }
}

@Composable
fun LogInButton(onClick: () -> Unit){
    Button(onClick = { onClick() },
        modifier = Modifier.width(380.dp).height(40.dp)){
        Text("Log-In")
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ScoddTheme {
        // A surface container using the 'secondaryContainer' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.secondaryContainer) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp, 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_vector),
                    contentDescription = "SCODD",
                    contentScale = ContentScale.None,
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp, 16.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.rooster_png),
                    contentDescription = "picture of a rooster with a mop in hand and" +
                            " bucket on it's head covered in water",
                    contentScale = ContentScale.None
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ){
                CreateAccountButton(onClick = {})
                LogInButton(onClick = {})
            }

        }

    }
}