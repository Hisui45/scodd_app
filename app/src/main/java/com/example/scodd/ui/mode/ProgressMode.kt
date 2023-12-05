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
import com.example.scodd.ui.components.*
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
    val uiSpinState by viewModel.uiSpinState.collectAsState()

    var showExitConfirmation by remember { mutableStateOf(false) }
    var showDoneConfirmation by remember { mutableStateOf(false) }

    val choreTitles = remember { mutableStateListOf<String>() }
    val playSound = remember { mutableStateOf(false) }

    val questNumber = rememberSaveable { mutableStateOf(0)}
    val roomNumber = rememberSaveable { mutableStateOf(0) }

    StatusBar(Color.White)

    when(modeId){
        ScoddMode.SandMode.modeId, ScoddMode.TimeMode.modeId ->
            if(!uiState.isPaused && !uiState.isFinished){
                viewModel.startMode()
            }
    }

    if(modeId != null){
        Scaffold(
            topBar = {
                var color = Color.Transparent
                var title = ""
                if(uiState.isFinished) {
                    when (modeId) {
                        ScoddMode.TimeMode.modeId, ScoddMode.BankMode.modeId -> {
                            color = MaterialTheme.colorScheme.primaryContainer
                            title = stringResource(R.string.overview)
                        }
                    }
                }
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
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(end = 8.dp))
                                }
                        }
                    }
                }
                TopAppBar(
                    title = {Text(title,style = MaterialTheme.typography.titleLarge)},
                    navigationIcon = {
                        IconButton(onClick = {
                            if(ScoddMode.QuestMode.modeId == modeId && roomNumber.value == incomingSelectedItems.size){
                                onNavigateBack()
                            }else if(!uiState.isFinished){
                                viewModel.pauseTimer()
                                showExitConfirmation = true
                            }else{
                                onNavigateBack()
                            }
                        })
                        {Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close)) }
                    },
                    actions = {actions()},
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = color),
                )
            },
            containerColor = if (modeId == ScoddMode.SpinMode.modeId) MaterialTheme.colorScheme.primaryContainer else Color.White
            ){
            Box(
                modifier = Modifier.padding(it)
            ){
                if(uiState.isFinished) {
                    StatusBar(LightMarigold40)
                    FinishSound()
                    Column(
                        modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp).fillMaxHeight().fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        when (modeId) {
                            ScoddMode.BankMode.modeId -> {
                                val payout = viewModel.calculatePayout()
                                ChoreSelectModeHeaderRow(stringResource(R.string.bank_payout), "$$payout")
                                LazyColumn(
                                   contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    itemsIndexed(uiState.chores) { index, choreItem ->
                                        val title = viewModel.getChoreTitle(choreItem.parentChoreId)
                                        OverviewModeListItem(
                                            title = title,
                                            isComplete = choreItem.isComplete,
                                            trailingContent = {
                                                val bankValue =
                                                    if(choreItem.isComplete){
                                                        viewModel.getChoreBankModeValue(choreItem.parentChoreId)
                                                    }else{
                                                        0
                                                    }
                                                Text(text ="$$bankValue",style = MaterialTheme.typography.labelLarge,
                                                    color = if (bankValue > 0) MaterialTheme.colorScheme.surfaceTint
                                                    else MaterialTheme.colorScheme.error
                                                )
                                            }
                                        )
                                        if (index < uiState.chores.lastIndex)
                                            Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                                    }
                                    item {
                                        WellDone()
                                    }
                                }
                            }
                            ScoddMode.TimeMode.modeId -> {
                                val totalTime = viewModel.calculateTotalTime()
                                ChoreSelectModeHeaderRow(stringResource(R.string.time_total), totalTime)
                                LazyColumn {
                                    itemsIndexed(uiState.chores) { index, choreItem ->
                                        val title = viewModel.getChoreTitle(choreItem.parentChoreId)
                                        OverviewModeListItem(
                                            title = title,
                                            isComplete = choreItem.isComplete,
                                            trailingContent = {
                                                val timerValue = viewModel.getCompleteTimerValue(index)
                                                LabelText(timerValue)
                                            }
                                        )
                                        if (index < uiState.chores.lastIndex)
                                            Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                                    }
                                    item {
                                        WellDone()
                                    }
                                }
                            }
                            ScoddMode.SandMode.modeId -> {
                                StatusBar(Color.White)
                                val sandTotalTime = viewModel.calculateSandTotalTime()
                                Text(stringResource(R.string.time_total), style = MaterialTheme.typography.displayMedium,
                                    color = MaterialTheme.colorScheme.secondary)
                                Text(sandTotalTime, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Normal)
                                if(uiState.chores.isNotEmpty()){
                                    LazyColumn   {
                                        itemsIndexed(uiState.chores) { index, choreItem ->
                                            val title = viewModel.getChoreTitle(choreItem.parentChoreId)
                                            OverviewModeListItem(
                                                title = title,
                                                isComplete = choreItem.isComplete,
                                                trailingContent = {
                                                    Checkbox(checked = choreItem.isComplete, onCheckedChange = null)
                                                }
                                            )
                                            if (index < uiState.chores.lastIndex)
                                                Divider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                                        }
                                        item {
                                            Spacer(Modifier.weight(1f))
                                            WellDone()
                                        }
                                    }
                                }else{
                                    Spacer(Modifier.weight(1f))
                                    WellDone()
                                }
                            }
                        }
                    }
                }else{
                    Column(
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp).fillMaxHeight().fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ){
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
                                        viewModel :: getIndexChoreTitle,
                                        { index, isComplete -> viewModel.completeSandChore(index); if (!isComplete) playSound.value = true},
                                        uiState.isPaused
                                    )
                                    CompleteTimeSound(playSound)
                                }
                                if(uiState.chores.all { choreItem -> choreItem.isComplete }){
                                    ModeButton(text = R.string.finish, onClick = {viewModel.setFinished()})
                                }else if(uiState.chores.any{choreItem -> choreItem.isComplete }){
                                    ModeButton(text = R.string.done,
                                        onClick = {
                                            viewModel.pauseTimer()
                                            showDoneConfirmation = true
                                        })
                                }else{
                                    ModeButton(text = R.string.done, onClick = {},isVisible = false)
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
            DoneConfirmationDialog(onConfirm = {showDoneConfirmation = false; viewModel.setFinished()},
                onDismiss = {showDoneConfirmation = false; viewModel.pauseTimer()}, showDoneConfirmation)

            ExitConfirmationDialog(onConfirm = {showDoneConfirmation = false; onNavigateBack()},
                onDismiss = {showExitConfirmation = false; viewModel.pauseTimer()}, showExitConfirmation)
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
fun WellDone(){
    Row(
        modifier = Modifier.fillMaxWidth(1f),
        horizontalArrangement = Arrangement.Center
    ){
        Text(stringResource(R.string.well_done), style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp))
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
    val color by infiniteTransition.animateColor( initialValue = Color.Black, targetValue = Color.Transparent,
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

@Composable
fun QuestButton(text: Int, onCompleteClick: () -> Unit){
    TextButton(
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
            painter = painterResource(image),
            contentDescription = stringResource(R.string.trash_content_desc),
            contentScale = ContentScale.FillHeight
        )
        QuestTips()
        Row(
            modifier = Modifier.fillMaxWidth(1f),
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
fun ChoreList(chores: List<ChoreItem>, getTitle: (Int) -> String, completeChore: (Int, Boolean) -> Unit,
              isPaused: Boolean){
    Column {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.8f)
        ) {
            itemsIndexed(chores){ index, choreItem ->
                var isComplete by remember { mutableStateOf(false) } //Quick Fix
                ChoreListItem(
//                    firstRoom = "",
//                    additionalAmount = 0,
                    title = getTitle(index),
                    isComplete = isComplete,
                    onCheckChanged = {
                        isComplete = !isComplete
                        completeChore(index,choreItem.isComplete)},
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
            color = MaterialTheme.colorScheme.surfaceTint
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