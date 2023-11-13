package com.example.scodd.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scodd.R
import com.example.scodd.ui.components.LoginButton
import com.example.scodd.ui.components.StatusBar
import com.example.scodd.ui.theme.Burgundy40
import com.example.scodd.ui.theme.ScoddTheme

@Composable
fun Logo(){
    Image(
        painter = painterResource(id = R.drawable.logo_vector),
        contentDescription = stringResource(R.string.logo_content_desc),
        contentScale = ContentScale.None,
    )
}

@Composable
fun Rooster(){
    Image(
        modifier = Modifier.width(300.dp).height(300.dp),
        painter = painterResource(id = R.drawable.rooster),
        contentDescription = stringResource(R.string.rooster_content_desc),
        contentScale = ContentScale.FillHeight
    )
}

@Composable
fun CreateAccountButton(onClick: () -> Unit){
    LoginButton(onClick,stringResource(R.string.create_acc_button))
}

@Composable
fun LogInButton(onClick: () -> Unit){
    LoginButton(onClick, stringResource(R.string.log_in_button))
}

@Composable
fun LoginScreen(onLoginClick: () -> Unit, onRegisterClick : () -> Unit) {
    StatusBar(Burgundy40)
    // A surface container using the 'secondaryContainer' color from the theme
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.secondaryContainer) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp, 24.dp, 8.dp, 0.dp)
        ) {
            Logo()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Rooster()
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Bottom),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ){
            CreateAccountButton(onClick = {onRegisterClick()})
            LogInButton(onClick = {onLoginClick()})
        }

    }
}



@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp")
//@Preview(showSystemUi = true, device = "spec:width=673.5dp,height=841dp,dpi=480")
//@Preview(showSystemUi = true, device = "spec:width=1280dp,height=800dp,dpi=480")
//@Preview(showSystemUi = true, device = "spec:width=1920dp,height=1080dp,dpi=480")
@Composable
fun LoginPreview(){
    ScoddTheme {
        LoginScreen(onLoginClick = {}, onRegisterClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun LogoPreview() {
    ScoddTheme {
        Logo()
    }
}

@Preview(showBackground = true)
@Composable
fun RoosterPreview() {
    ScoddTheme {
        Rooster()
    }
}

@Preview(showBackground = true)
@Composable
fun AccountPreview() {
    ScoddTheme {
        CreateAccountButton(onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun LogInPreview() {
    ScoddTheme {
        LogInButton(onClick = {})
    }
}