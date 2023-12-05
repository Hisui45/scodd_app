package com.example.scodd.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scodd.R
import com.example.scodd.ui.components.LoginButton
import com.example.scodd.ui.components.StatusBar
import com.example.scodd.ui.theme.*


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
        modifier = Modifier.size(250.dp),
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
fun StartButton(onClick: () -> Unit){
    LoginButton(onClick, stringResource(R.string.get_started))
}

@Composable
fun LoginScreen(
    navigateToScodd: () -> Unit,
//    onRegisterClick : () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    if(uiState.user == null){
        StatusBar(LightMarigold40)

        val screenNumber = rememberSaveable { mutableStateOf(0) }
        val name = rememberSaveable {mutableStateOf("")}

        when(screenNumber.value){
            0 -> WelcomeScreen(screenNumber)
            1 -> GettingStartedScreen(name, onNameEntered = { viewModel.saveName(name.value)})
        }

        LaunchedEffect(uiState.isLoggedIn) {
            if (uiState.isLoggedIn) {
                navigateToScodd()
            }
        }
    }else{
        LoadingScreen()
        navigateToScodd()
    }

}

@Composable
fun WelcomeScreen(
    screenNumber: MutableState<Int>
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primaryContainer) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Rooster()
            // Welcome Message

            Row(
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ){
                Text(
                    text = "This is",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                )
                Text(
                    text = "Scodd",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = ".",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Text(
                text = "A chore/cleaning app.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )


            Text(
                text = "Designed for people who struggle with cleaning or maybe just like to be organized about it. Whether you're a person with ADHD, someone struggling with depression, or just trying to repair your relationship with cleaning. Or maybe someone who really just enjoys cleaning. This app strives to be the bridge between you and your cleaning goals. There's something that can help everyone even if only a little bit in here. You shouldn't have to do it alone and you don't, especially now that Scodd is here. ",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            TextButton(
                onClick = {
                    screenNumber.value = screenNumber.value + 1
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Get Started", modifier = Modifier.padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}

@Composable
fun GettingStartedScreen(
    name: MutableState<String>,
    onNameEntered: (String) -> Unit = {},
){
    val focusManager = LocalFocusManager.current

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.onSecondary){
        Column(
            modifier = Modifier
                .fillMaxSize()
        ){
            Text("Getting Started", style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer).fillMaxWidth(1f)
                    .padding(16.dp))

            Column(
                modifier = Modifier.fillMaxHeight(0.3f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ){
                Text("Enter your name", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    placeholder = { Text("Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done, capitalization = KeyboardCapitalization.Words
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {focusManager.clearFocus()}
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, top = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
                        focusedLabelColor = MaterialTheme.colorScheme.secondary)
                )
            }

            Row(
               modifier = Modifier.fillMaxWidth(1f).padding(horizontal =16.dp, vertical = 100.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Text("Just some onboarding stuff," +
                        " very important necessary information for the fundamental functionalities of this app. Trust me.",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light)
            }

            Row(
                modifier = Modifier.fillMaxHeight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                TextButton(
                    onClick = {
                        onNameEntered(name.value)

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = name.value.isNotEmpty()
                ) {
                Text("Onward To Scodd", modifier = Modifier.padding(end = 8.dp),
                    color = if (name.value.isNotEmpty()) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge)
                }
            }


        }
    }
}

@Composable
fun LoadingScreen(){
        // A surface container using the 'secondaryContainer' color from the theme
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.secondaryContainer) {
//        Column(
//            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.padding(8.dp, 24.dp, 8.dp, 0.dp)
//        ) {
//            Logo()
//        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Rooster()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChoreToDoListScreen() {
    ScoddTheme {
        WelcomeScreen(remember { mutableStateOf(0) })
    }
}



@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp")
//@Preview(showSystemUi = true, device = "spec:width=673.5dp,height=841dp,dpi=480")
//@Preview(showSystemUi = true, device = "spec:width=1280dp,height=800dp,dpi=480")
//@Preview(showSystemUi = true, device = "spec:width=1920dp,height=1080dp,dpi=480")
@Composable
fun LoginPreview(){
    ScoddTheme {
        LoginScreen(navigateToScodd = {})
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
        StartButton(onClick = {})
    }
}

