package com.example.scodd.ui.mode

import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scodd.spin_wheel_compose.SpinWheel
import com.example.scodd.spin_wheel_compose.SpinWheelDefaults
import com.example.scodd.R
import com.example.scodd.model.ChoreItem
import com.example.scodd.model.ScoddMode
import com.example.scodd.spin_wheel_compose.state.rememberScoddSpinWheelState
import com.example.scodd.ui.components.ChoreListItem
import com.example.scodd.ui.components.StatusBar
import com.example.scodd.ui.theme.LightMarigold40
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteModeScreen(
    incomingSelectedItems: List<String>,
    modeId: String?,
    onSelectFinish : (List<String>) -> Unit,
    onNavigateBack: () -> Unit,
    navigateToChoreCreate : () -> Unit,
    viewModel: ProgressModeViewModel = hiltViewModel()
) {


    val uiState by viewModel.uiState.collectAsState()
    val uiSandState by viewModel.uiSandState.collectAsState()
    val uiSpinState by viewModel.uiSpinState.collectAsState()

    val choreTitles = remember { mutableStateListOf<String>() }
    val playSound = remember { mutableStateOf(false) }

    val questNumber = rememberSaveable { mutableStateOf(0)}
    val roomNumber = rememberSaveable { mutableStateOf(0) }
    val currentRoom = rememberSaveable { mutableStateOf(incomingSelectedItems[roomNumber.value]) }

    when(modeId){
        ScoddMode.SandMode.modeId, ScoddMode.TimeMode.modeId ->
            if(!uiState.isPaused){
                viewModel.startMode()
            }
    }

    if(modeId != null){
        Scaffold(
            topBar = {
                val actions: @Composable () -> Unit = {
                    if(uiState.isFinished){

                    }else{
                        when(modeId){
                            ScoddMode.TimeMode.modeId, ScoddMode.BankMode.modeId -> {
                                TextButton(
                                    onClick = {viewModel.nextChore()}
                                ){
                                    Text(stringResource(R.string.skip),
                                        color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                        when(modeId){
                            ScoddMode.TimeMode.modeId, ScoddMode.SandMode.modeId ->
                                if(!uiState.isTimeUp){
                                    TextButton(
                                        onClick = { viewModel.pauseTimer() }
                                    ){
                                        val text = if (uiState.isPaused) R.string.resume else R.string.pause
                                        Text(stringResource(text),
                                            color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            ScoddMode.QuestMode.modeId ->
                                if(questNumber.value != 0){
                                    Text(stringResource(R.string.current_room) + " " + incomingSelectedItems[roomNumber.value],
                                        textAlign = TextAlign.End,
                                        style = MaterialTheme.typography.titleLarge)
                                }
                        }
                    }
                }
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = {
                            //TODO: PopUp confirming progress loss
                            onNavigateBack()})
                        {Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close)) }
                    },
                    actions = {actions()},
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            containerColor = if (modeId == ScoddMode.SpinMode.modeId) MaterialTheme.colorScheme.primaryContainer else Color.White
            ){
            Box(
                modifier = Modifier.padding(it)
            ){
                Column(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp).fillMaxHeight().fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ){
                    if(uiState.isFinished){
                        FinishSound()
                        when(modeId){
                            ScoddMode.BankMode.modeId-> {

                            }
                            ScoddMode.TimeMode.modeId-> {

                            }
                            ScoddMode.SandMode.modeId-> {

                            }
                        }
                    }else{
                        when(modeId){
                            ScoddMode.BankMode.modeId-> {
                                ChoreTitle(uiState.choreTitle)
                                AmountInEscrow(viewModel.getCurrentChoreBankAmount())
                                CompleteChoreButtons(viewModel.checkIsFinished(),
                                    onClick = {viewModel.completeChore()
                                        playSound.value = true})
                                CompleteMoneySound(playSound)
                            }
                            ScoddMode.TimeMode.modeId-> {
                                ChoreTitle(uiState.choreTitle)
                                if(uiState.isTimeUp){
                                    TimesUp()
                                    BetweenChoresButtons(viewModel :: addTimeClick, viewModel :: completeChore, viewModel.checkIsFinished())
                                }else {
                                    TimeRemaining(uiState.timerValue, uiState.isPaused)
                                    CompleteChoreButtons(viewModel.checkIsFinished(),
                                        onClick = {viewModel.completeChore()
                                            playSound.value = true})
                                    CompleteTimeSound(playSound)
                                }
                                TimesUpAlarm(uiState.isTimeUp)
                            }
                            ScoddMode.SandMode.modeId-> {
                                if(uiState.isTimeUp){
                                    TimesUp()
                                }else{
                                    TimeRemaining(uiState.timerValue, uiState.isPaused)
                                }
                                if(uiState.chores.isNotEmpty()){
                                    ChoreList(
                                        uiState.chores,
                                        uiSandState.completedChores,
                                        viewModel :: getIndexChoreTitle,
                                        { index, isComplete -> viewModel.completeIndexChore(index); if (!isComplete) playSound.value = true},
                                        uiState.isPaused
                                    )
                                    CompleteTimeSound(playSound)
                                }
                                if(uiSandState.completedChores.size == uiState.chores.size){
                                    ModeButton(text = R.string.finish, onClick = {viewModel.setFinished()})
                                }else if(uiSandState.completedChores.isNotEmpty()){
                                    ModeButton(text = R.string.done,
                                        onClick = {
                                            //TODO: PopUp confirming completion even though chores are left undone
                                            viewModel.setFinished()
                                        })
                                }else{
                                    ModeButton(R.string.done, {}, false)
                                }
                                TimesUpAlarm(uiState.isTimeUp)
                            }
                            ScoddMode.SpinMode.modeId->{
                                StatusBar(LightMarigold40)
                                val hasChores = remember { mutableStateOf(true) }
                                var randomChoreTitle by rememberSaveable { mutableStateOf("") }
                                if(uiSpinState.choreTitles.isNotEmpty() && hasChores.value) {
                                    Text(text = stringResource(R.string.spin_wheel), style = MaterialTheme.typography.displayMedium)

                                    val scope = rememberCoroutineScope()
                                    val state = rememberScoddSpinWheelState(
                                        pieCount = uiSpinState.choreTitles.count(), durationMillis = 4500)

                                    val spinWheelSize = 380

                                    val mContext = LocalContext.current
                                    val mMediaPlayer1 = MediaPlayer.create(mContext, R.raw.wheel_sound)
                                    val mMediaPlayer2 = MediaPlayer.create(mContext, R.raw.spin_done)


                                    val truncatedTitles: MutableList<String> = mutableListOf()
                                    uiSpinState.choreTitles.forEach { title ->
                                        if(title.length > 14){
                                            truncatedTitles.add(title.substring(0, 12).plus("..."))
                                        }else{
                                            truncatedTitles.add(title)
                                        }
                                    }
                                    SpinWheel(
                                        state = state,
                                        onClick = {
                                            mMediaPlayer1.start()
                                            scope.launch {
                                                state.animate { pieIndex ->
                                                    randomChoreTitle = uiSpinState.choreTitles[pieIndex]
                                                    viewModel.removeChore(randomChoreTitle)
                                                    mMediaPlayer2.start()
                                                    if(uiSpinState.choreTitles.isEmpty()){
                                                        hasChores.value = false
                                                    }
                                                }
                                            }
                                        },
                                        dimensions = SpinWheelDefaults.spinWheelDimensions(
                                            spinWheelSize = spinWheelSize.dp,
                                            frameWidth = 20.dp,
                                            selectorWidth = 10.dp
                                        ),
                                        titles = truncatedTitles
                                    )
                                    Text(text = randomChoreTitle, style = MaterialTheme.typography.displayMedium)
                                }else{
                                    Text(stringResource(R.string.chores_completed), style = MaterialTheme.typography.displayMedium,
                                        textAlign = TextAlign.Center)
                                    Text(text = randomChoreTitle, style = MaterialTheme.typography.displayMedium)
                                    WheelEmptyButtons(onFinishClick = {onNavigateBack()},
                                        onRestartClick = {
                                            viewModel.updateChoreTitles(uiSpinState.incomingChoreTitles)
                                            hasChores.value = true
                                            randomChoreTitle = ""
                                        })
                                }
                            }
                            ScoddMode.QuestMode.modeId->{
                                StatusBar(Color.White)

                                when(questNumber.value){
                                    0->
                                        BriefingScreen(questNumber, incomingSelectedItems, roomNumber.value,
                                            onFinished = {onNavigateBack()})
                                    1->
                                        QuestScreen(R.string.quest_one_title,R.string.quest_one_desc,
                                            questNumber, R.drawable.trash_pixel)
                                    2->
                                        QuestScreen(R.string.quest_two_title, R.string.quest_two_desc,
                                            questNumber, R.drawable.clothes)
                                    3->
                                        QuestScreen(R.string.quest_three_title, R.string.quest_three_desc,
                                            questNumber, R.drawable.dishes_1)
                                     4->
                                        QuestScreen(R.string.quest_four_title, R.string.quest_four_desc,
                                            questNumber, R.drawable.found)
                                    5->
                                        QuestScreen(R.string.quest_five_title, R.string.quest_five_desc,
                                           questNumber, R.drawable.box, roomNumber)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    var addedItems by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        if (!addedItems) {
            choreTitles.addAll(incomingSelectedItems)
            addedItems = true
        }
    }

    uiSpinState.incomingChoreTitles.let { items ->
        LaunchedEffect(items){
            viewModel.updateChoreTitles(items)
        }
    }
}

@Composable
fun BriefingScreen(questNumber: MutableState<Int>, titles: List<String>, currentRoom: Int, onFinished: () -> Unit){
    Column(
        modifier = Modifier.fillMaxHeight(1f).fillMaxWidth(1f),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        if(currentRoom == titles.size){
            Text(stringResource(R.string.quest_finished),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }else{
            Text(
                stringResource(R.string.briefing_desc),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        LazyColumn {
            itemsIndexed(titles) { index, title ->
                ListItem(
                    headlineContent = {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleLarge,
                            textDecoration = if( currentRoom > index ) TextDecoration.LineThrough else TextDecoration.None,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    trailingContent = {
                        if(currentRoom == index){
                            Checkbox(checked = true, onCheckedChange = {})
                        }
                    },
                )
                if (index < titles.lastIndex)
                    Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
            }
        }
        if(currentRoom == titles.size){
            Text(stringResource(R.string.quest_finished_message), textAlign = TextAlign.Center, fontWeight = FontWeight.Light
                ,color = MaterialTheme.colorScheme.onPrimary)
        }else{
            Text(stringResource(R.string.quest_message), textAlign = TextAlign.Center, fontWeight = FontWeight.Light
                ,color = MaterialTheme.colorScheme.onPrimary)
        }
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.End
        ){
            if(currentRoom == titles.size){
                QuestButton(R.string.done,onCompleteClick ={
                    onFinished()
                })
            }else{
                QuestButton(R.string.next,onCompleteClick ={
                    questNumber.value = questNumber.value + 1
                })
            }
        }
    }
}

@Composable
fun QuestTips() {
    val messages = listOf("It doesn't have to be perfect.", "Just take that first step.", "Play some music.",
        "Do what you can.", "Focus on one thing at a time.", "Visualize the end result." ,
        "Done is better than perfect.","Celebrate progress, not just completion." )
    var currentIndex by remember { mutableStateOf(Random.nextInt(messages.count())) }

    LaunchedEffect(key1 = currentIndex) {
        while (true) {
            delay(8000) // Adjust the delay as needed

            // Switch to the next string in the list
            currentIndex = (currentIndex + 1) % messages.size
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val color by infiniteTransition.animateColor(
        initialValue = Color.Black,
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000),
            repeatMode = RepeatMode.Reverse
        )
    )

        Text(
            text = "Tip: " + messages[currentIndex],
            color = color
        )

}

//@Composable
//fun QuestTips() {
//    var currentIndex by remember { mutableStateOf(0) }
//    val stringList = listOf("String 1", "String 2", "String 3")
//    var isVisible by remember { mutableStateOf(true) }
//
//    LaunchedEffect(key1 = currentIndex) {
//        while (true) {
//            delay(2000) // Adjust the delay as needed
//            isVisible = !isVisible
//            delay(4000)
//            // Switch to the next string in the list
//            currentIndex = (currentIndex + 1) % stringList.size
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        AnimatedVisibility(
//            visible = isVisible, // Change this condition based on your logic
//            enter = fadeIn(),
//            exit = fadeOut()
//        ) {
//            Text(
//                text = "Tip: " + stringList[currentIndex],
//            )
//        }
//    }
//}

@Composable
fun QuestButton(text: Int, onCompleteClick: () -> Unit){
    TextButton(
//        modifier = Modifier.fillMaxWidth(0.4f),
//                                        .alpha( if (isVisible) 1f else 0f),
        onClick = {onCompleteClick()},
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
    ){
        Text(stringResource(text), style = MaterialTheme.typography.titleSmall, )
    }
}

@Composable
fun QuestScreen(title: Int, description: Int, questNumber: MutableState<Int>, image: Int, roomNumber: MutableState<Int> = mutableStateOf(0)){
    Column(
        modifier = Modifier.fillMaxHeight(1f).fillMaxWidth(1f),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.Start
        ){
            QuestHeader(stringResource(title))
        }
        Text(
            stringResource(description),
            style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary)
        Image(
//            modifier = Modifier.fillMaxSize(1f),
            painter = painterResource(image),
            contentDescription = stringResource(R.string.trash_content_desc),
            contentScale = ContentScale.FillHeight
        )
//        Text("Tip: Take a deep breath.")
        QuestTips()
        Row(
            modifier = Modifier.fillMaxWidth(1f),
//            horizontalArrangement = Arrangement.SpaceBetween
        ){
            QuestButton(R.string.back,onCompleteClick ={
                questNumber.value = questNumber.value - 1
            })
            Spacer(Modifier.weight(1f))
            QuestButton(R.string.complete,onCompleteClick = {
                if(questNumber.value == 5){
                    roomNumber.value = roomNumber.value +1
                    questNumber.value = 0
                }else{
                    questNumber.value = questNumber.value + 1
                }

            })
        }

    }

}

@Composable
fun QuestHeader(title: String){
    Text(title, style = MaterialTheme.typography.displayMedium)
}
fun calculateAngle(index: Int, frequencies: List<Double>, sliceWidth: Double): Double {
    // Step 1: Calculate the center of each slice
    val cumulativeSum = frequencies.subList(0, index + 1).sum()
    val sliceCenter = cumulativeSum - sliceWidth / 2.0

    // Step 2: Rescale to 360 degrees
    val maxCumulativeSum = frequencies.sum()
    val scaledPosition = (sliceCenter / maxCumulativeSum) * 360.0

    // Step 3: Add 90 degrees for outward orientation

    return scaledPosition + 90.0
}

@Composable
fun ChoreList(chores: List<ChoreItem>, completedChores: List<ChoreItem>, getTitle: (Int) -> String, completeChore: (Int, Boolean) -> Unit,
              isPaused: Boolean){
    Column {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.8f)
        ) {

            itemsIndexed(chores){ index, choreItem ->
                val isComplete = choreItem in completedChores
                ChoreListItem(
//                    firstRoom = "",
//                    additionalAmount = 0,
                    title = getTitle(index),
                    isComplete = isComplete,
                    onCheckChanged = {completeChore(index,isComplete)},
                    showCheckBox = true,
                    isEnabled = !isPaused
                )
                if (index < chores.lastIndex)
                    Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
            }
        }
    }
}
@Composable
fun TimesUpAlarm(watchBoolean: Boolean) {
    val mContext = LocalContext.current
    val mMediaPlayer = MediaPlayer.create(mContext, R.raw.rooster_alarm)

    var hasAlarmPlayed by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(watchBoolean) {
        if (watchBoolean && !hasAlarmPlayed) {
            mMediaPlayer.start()
            hasAlarmPlayed = true
        }else if(!watchBoolean){
            hasAlarmPlayed = false
            mMediaPlayer.stop()
        }
    }
}

@Composable
fun FinishSound() {
    val mContext = LocalContext.current
    val mMediaPlayer = MediaPlayer.create(mContext, R.raw.win_sound)

    var hasAlarmPlayed by rememberSaveable { mutableStateOf(false) }

        if (hasAlarmPlayed) {
            mMediaPlayer.stop()
        }else{
            mMediaPlayer.start()
            hasAlarmPlayed = true
        }
}

@Composable
fun CompleteMoneySound(watchBoolean: MutableState<Boolean>) {
    val mContext = LocalContext.current
    val mMediaPlayer = MediaPlayer.create(mContext, R.raw.money_sound)
    if (watchBoolean.value) {
        mMediaPlayer.start()
        watchBoolean.value = false
    }
}

@Composable
fun CompleteTimeSound(watchBoolean: MutableState<Boolean>) {
    val mContext = LocalContext.current
    val mMediaPlayer = MediaPlayer.create(mContext, R.raw.time_sound) //TODO: sound not loud enough
    if (watchBoolean.value) {
        mMediaPlayer.start()
        watchBoolean.value = false
    }
}
@Composable
fun BetweenChoresButtons(onAddTimeClick: () -> Unit, onCompleteClick: () -> Unit, isFinished: Boolean){
   Column(
       modifier = Modifier.fillMaxWidth(),
       verticalArrangement = Arrangement.SpaceAround,
       horizontalAlignment = Alignment.CenterHorizontally
   ){
       ModeButton(text = R.string.more_time, onClick = {onAddTimeClick()})
       CompleteChoreButtons(isFinished,onCompleteClick)
   }
}

@Composable
fun WheelEmptyButtons(onFinishClick: () -> Unit, onRestartClick: () -> Unit){
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        SpinButton(R.string.finish, onFinishClick)
        SpinButton(R.string.restart, onRestartClick)
    }
}

@Composable
fun SpinButton(text: Int, onClick: () -> Unit, isVisible: Boolean = true){
    TextButton(
        modifier = Modifier.fillMaxWidth(0.6f).alpha( if (isVisible) 1f else 0f),
        onClick = {onClick()},
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary),
        border = BorderStroke(2.dp, Color.Black)
    ){
        Text(stringResource(text).uppercase(Locale.ROOT), style = MaterialTheme.typography.titleLarge, )
    }
}
@Composable
fun CompleteChoreButtons(isFinished: Boolean, onClick: () -> Unit){
    if(isFinished){
        ModeButton(text = R.string.finish, onClick = { onClick() })
    }else {
        ModeButton(text = R.string.complete, onClick = { onClick() })
    }
}

@Composable
fun ChoreTitle(title: String){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
        Text(title, style = MaterialTheme.typography.displayMedium)
    }
}

@Composable
fun AmountInEscrow(amount: Int){
        Text(
            text = stringResource(R.string.amount_escrow),
            style = MaterialTheme.typography.displayMedium,
//            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "$$amount",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF089630)
//            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
}

@Composable
fun ModeButton(text: Int, onClick: () -> Unit, isVisible: Boolean = true){
    TextButton(
        modifier = Modifier.fillMaxWidth(0.6f).alpha( if (isVisible) 1f else 0f),
        onClick = {onClick()},
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
    ){
        Text(stringResource(text).uppercase(Locale.ROOT), style = MaterialTheme.typography.titleLarge, )
    }
}
@Composable
fun TimeRemaining(timerValue: String, isPaused: Boolean){
    Text(stringResource(R.string.time_remaining), style = MaterialTheme.typography.displayMedium)

    var color = if (isPaused) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onPrimaryContainer
    if(timerValue.isNotEmpty() && !timerValue.contains(":")){
        if(timerValue.toInt() < 10){
            color = MaterialTheme.colorScheme.error
        }
    }
    Text(timerValue, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Normal, color = color)
}

@Composable
fun TimesUp(){
    FlashingText(stringResource(R.string.time_up), MaterialTheme.colorScheme.error)
}

@Composable
fun FlashingText(text: String, flashingColor: Color, period: Int = 500) {
    val infiniteTransition = rememberInfiniteTransition()
    val color by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.onPrimaryContainer,
        targetValue = flashingColor,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = period),
            repeatMode = RepeatMode.Restart
        )
    )
    Text(text = text, color = color, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Normal)
}