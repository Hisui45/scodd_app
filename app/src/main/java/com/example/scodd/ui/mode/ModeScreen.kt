package com.example.scodd.ui.mode

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scodd.R
import com.example.scodd.model.Mode
import com.example.scodd.model.ScoddMode
import com.example.scodd.ui.components.StatusBar
import com.example.scodd.ui.theme.White40
import timber.log.Timber


/**
 * TODO: add sound to quest
 * TODO: show money earned from Piggy Bank Mode
 */
@Composable
fun ModeScreen(
    onModeClick: (ScoddMode) -> Unit,
    viewModel: ModeViewModel = hiltViewModel()
    ) {

    val uiState by viewModel.uiState.collectAsState()

    Surface{
        StatusBar(White40)
        Column(
            Modifier.padding(12.dp, 0.dp)
        ){
            ModeTitleCard()
            LazyColumn(
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                Timber.tag("friend").d(uiState.bankAmount.toString())
                if(uiState.bankAmount > 0){
                    item{BankModeCard(onModeClick, uiState.bankAmount)}
                }else{
                    item{ModeCard(ScoddMode.BankMode, onModeClick)}
                }

                item{ ModeCard(ScoddMode.TimeMode, onModeClick)}
                item{ ModeCard(ScoddMode.QuestMode,onModeClick)}
                item{ ModeCard(ScoddMode.SandMode, onModeClick)}
                item{ ModeCard(ScoddMode.SpinMode, onModeClick)}
            }
        }

    }
}

@Composable
fun BankModeCard(onModeClick: (ScoddMode) -> Unit, value: Int){
    Box(){
        ModeCard(ScoddMode.BankMode, onModeClick)
        Row(
            modifier = Modifier.fillMaxHeight(1f).padding(12.dp),
            verticalAlignment = Alignment.Bottom
        ){
            Image(painterResource(R.drawable.ic_bank_outlined), contentDescription = stringResource(R.string.piggy_bank))
            Text("Total Earned: $$value")
        }

    }
}

@Composable
fun ModeTitleCard(){
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary),
        modifier = Modifier.fillMaxWidth(1f).padding(bottom = 8.dp).testTag(stringResource(R.string.modes_title))
    ){
        Column(
            Modifier.padding(24.dp)
        ){
            Text(
                stringResource(R.string.modes_title),
                style = MaterialTheme.typography.displaySmall)
            Text("Utilize different techniques to work through chores and workflows.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Light)
        }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeCard(mode : ScoddMode, onModeClick: (ScoddMode) -> Unit ){
        ElevatedCard(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary,MaterialTheme.colorScheme.onPrimary),
            onClick = {onModeClick(mode)}
        ){

            Column(
                Modifier.fillMaxWidth().fillMaxHeight().height(150.dp),
                horizontalAlignment = Alignment.End
            ){
//            IconButton(
//                onClick ={}
//            ){
//                Icon(Icons.Default.MoreVert,"More")
//
//            }
                Spacer(Modifier.weight(1f))
                Text(
                    stringResource(mode.title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp))
            }
    }

}
