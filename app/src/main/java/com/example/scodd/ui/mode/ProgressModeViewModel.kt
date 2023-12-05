package com.example.scodd.ui.mode

import androidx.lifecycle.*
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.*
import com.example.scodd.utils.WhileUiSubscribed
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.floor

/**
 * TODO: migrate from data passed through navigation to taking straight from chore repository
 */
data class ProgressModeUiState(
    val chores: List<ChoreItem> = listOf(),
    val parentChores: List<Chore> = emptyList(),
    val timerValue: String = "",
    val isFinished: Boolean = false,
    val isPaused: Boolean = false,
    val isTimeUp: Boolean = false,
    val choreTitle: String = " "
)

data class SpinModeUiState(
    val choreTitles: List<String> = emptyList(),
    val incomingChoreTitles: MutableList<String> = mutableListOf(),
    val choreTitlesCount: Int = 0
)
@HiltViewModel
class ProgressModeViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val modeId: String? = savedStateHandle["modeId"]
    private val incomingSelectedChores: String? = savedStateHandle["incomingSelectedChores"]
    private val timeDuration: Long? = savedStateHandle["timeDuration"]

    private val _currentChore = MutableStateFlow(savedStateHandle.get<Int>("currentChore") ?: 0)

    private val _chores = savedStateHandle.get<MutableList<ChoreItem>>("chores") ?: mutableListOf()

    private val _parentChores: MutableList<Chore> = mutableListOf()

    private val _choreTitle: MutableStateFlow<String> = MutableStateFlow("")

    private val _isFinished = MutableStateFlow(savedStateHandle.get<Boolean>("isFinished") ?: false)

    private val _isPaused = MutableStateFlow(savedStateHandle.get<Boolean>("isPaused") ?: false)

    private val _isTimeUp = MutableStateFlow(savedStateHandle.get<Boolean>("isTimeUp") ?: false)

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)

    private var job: Job? = null

    private val _timerDuration = MutableStateFlow(savedStateHandle.get<Long>("timerDuration") ?: (timeDuration ?: 45))

    private val _timerValue: MutableStateFlow<String> = MutableStateFlow("")

    private val _completeTimerValues: MutableList<Long> = mutableListOf()

    private val moreTimeDuration: Long = 300

    private val _hasAddedMoreTime = MutableStateFlow(savedStateHandle.get<Boolean>("hasAddedMoreTime") ?: false)
    init{
        if(modeId != null){
            if (incomingSelectedChores != null){
                val convertedChores = Gson().fromJson(incomingSelectedChores, Array<String>::class.java).toList()
                val chores = _chores.toMutableList()
                var index = 0
                convertedChores.forEach {
                   chores.add(ChoreItem(
                       id = index.toString(),
                       parentChoreId = it,
                       parentWorkflowId = "mode",
                       isComplete = false
                   ))
                    index++
                }
                _chores.addAll(chores)
                loadChores()
            }
        }
    }

    val uiState: StateFlow<ProgressModeUiState> = combine( _timerValue, _isFinished, _isPaused, _isTimeUp, _choreTitle) {
            timerValue,  isFinished, isPaused, isTimeUp, choreTitle ->
        ProgressModeUiState(
            timerValue = timerValue,
            chores = _chores,
            parentChores = _parentChores,
            isFinished = isFinished,
            isPaused = isPaused,
            isTimeUp = isTimeUp,
            choreTitle = choreTitle
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = ProgressModeUiState()
        )

    private fun startTimer(durationInSeconds: Long) {
        job?.cancel()

        job = viewModelScope.launch {
            for (i in durationInSeconds downTo 0) {
                _timerValue.value = formatTimerTime(i)
                _timerDuration.value = i
                delay(1000)
            }
            _timerValue.value = formatTimerTime(_timerDuration.value)
            _isTimeUp.value = true
        }
    }

    fun pauseTimer(): Boolean {
        if (_isPaused.value){
            startTimer(_timerDuration.value)
        }else{
            onCleared()
        }
        _isPaused.value = !_isPaused.value
        return _isPaused.value
    }

    private fun getCurrentChoreTitle(): String{
       return uiState.value.parentChores.find { it.id ==  uiState.value.chores[_currentChore.value].parentChoreId }?.title?: ""
    }

    fun getChoreTitle(parentChoreId: String): String {
        return uiState.value.parentChores.find { it.id == parentChoreId }?.title?: ""
    }

    fun getIndexChoreTitle(index: Int): String{
        return uiState.value.parentChores.find { it.id ==  uiState.value.chores[index].parentChoreId }?.title?: ""
    }

    fun getCurrentChoreBankAmount(): Int {
        return uiState.value.parentChores.find { it.id ==  uiState.value.chores[_currentChore.value].parentChoreId }?.bankModeValue?: 0
    }

    fun getChoreBankModeValue(parentChoreId: String): Int {
        val choreItem = uiState.value.parentChores.find { it.id == parentChoreId }
        if (choreItem != null) {
            return if(choreItem.isBankModeActive){
                Timber.tag("problem").d(choreItem.bankModeValue.toString())
                choreItem.bankModeValue
            }else{
                0
            }
        }
        return 0
    }

    fun startMode(){
        if(timeDuration != null){
            startTimer(_timerDuration.value)
        }
    }

    private fun getCurrentChoreTimerDuration(): Long{
        val currentChore = uiState.value.parentChores.find { it.id ==  uiState.value.chores[_currentChore.value].parentChoreId }
        val duration = currentChore?.timerModeValue?.toLong()?: -1
        val unit = currentChore?.timerOption?: ScoddTime.MINUTE

        return TimeUtils.getChoreTimerDurationInSeconds(duration, unit)
    }

    fun completeChore(){
        val updatedChores: MutableList<ChoreItem> = mutableListOf()
        _chores.forEachIndexed { index, choreItem ->
            if(index == _currentChore.value){
                updatedChores.add(ChoreItem(choreItem.id, choreItem.parentChoreId, choreItem.parentWorkflowId, true))
            }else{
                updatedChores.add(ChoreItem(choreItem.id, choreItem.parentChoreId, choreItem.parentWorkflowId, choreItem.isComplete))
            }
        }
        _chores.clear()
        _chores.addAll(updatedChores)
        nextChore()
    }

    fun calculatePayout(): Int {
        val parentChores = uiState.value.parentChores

        val payout = _chores.sumOf { choreItem ->
            if(choreItem.isComplete){
                if(parentChores.find { it.id == choreItem.parentChoreId }?.isBankModeActive == true){
                    parentChores.find { it.id == choreItem.parentChoreId }?.bankModeValue?: 0
                }else{
                    0
                }
            }else{
                0
            }
        }

        return payout
    }

    fun calculateTotalTime(): String {
        val totalTimeValues = _completeTimerValues.sumOf { timeValue ->
            timeValue
        }
        return formatSpentTime(totalTimeValues)

    }

    fun calculateSandTotalTime(): String {
        if(timeDuration != null){
            return formatSpentTime(timeDuration - _timerDuration.value)
        }
        return "?"
    }

    fun completeSandChore(choreIndex: Int){
        val updatedChores: MutableList<ChoreItem> = mutableListOf()
        _chores.forEachIndexed { index, choreItem ->
            if(index == choreIndex){
                if(choreItem.isComplete){
                    updatedChores.add(ChoreItem(choreItem.id, choreItem.parentChoreId, choreItem.parentWorkflowId, false))
                }else{
                    updatedChores.add(ChoreItem(choreItem.id, choreItem.parentChoreId, choreItem.parentWorkflowId, true))
                }
            }else{
                updatedChores.add(ChoreItem(choreItem.id, choreItem.parentChoreId, choreItem.parentWorkflowId, choreItem.isComplete))
            }
        }
        _chores.clear()
        _chores.addAll(updatedChores)
    }

    fun setFinished(){
        _isFinished.value = true
        onCleared()
    }

     fun nextChore(){
         if(_hasAddedMoreTime.value){
             _hasAddedMoreTime.value = false
             _completeTimerValues.add((moreTimeDuration - _timerDuration.value) + getCurrentChoreTimerDuration())
         }else{
             Timber.tag("problem").d(getCurrentChoreTimerDuration().toString())
             _completeTimerValues.add(getCurrentChoreTimerDuration() - _timerDuration.value)

         }
        if (checkIsFinished()) _isFinished.value = true else _currentChore.value = _currentChore.value + 1

         if(_isFinished.value && modeId == ScoddMode.BankMode.modeId){
             viewModelScope.launch {
                 val user = choreRepository.getUser("scodd_user")
                 val newValue = user?.let {
                     val totalPayout = calculatePayout() + it.bankModeValue
                     if (totalPayout >= Integer.MAX_VALUE) {
                         Integer.MAX_VALUE
                     } else {
                         totalPayout
                     }
                 } ?: calculatePayout()

                 choreRepository.updateUser("scodd_user", newValue)
             }
         }

         if(modeId == ScoddMode.TimeMode.modeId){
             _timerDuration.value = getCurrentChoreTimerDuration()
             startTimer(_timerDuration.value)
         }
         _isTimeUp.value = false
         _choreTitle.value = getCurrentChoreTitle()
    }

    fun getCompleteTimerValue(index: Int): String{
        return formatSpentTime(_completeTimerValues[index])
    }

    fun addTimeClick() {
        _isTimeUp.value = false
        _hasAddedMoreTime.value = true
        startTimer(moreTimeDuration)
    }
    fun checkIsFinished(): Boolean{
        return _currentChore.value >= _chores.size-1
    }

    private fun formatTimerTime(seconds: Long): String {
        val hours = TimeUnit.SECONDS.toHours(seconds)
        val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
        val remainingSeconds = seconds % 60

        return if (hours > 0) {
            "${hours}:${String.format("%02d", minutes)}:${String.format("%02d", remainingSeconds)}"
        } else if (minutes > 0) {
            "${minutes}:${String.format("%02d", remainingSeconds)}"
        } else {
            "$remainingSeconds"
        }
    }

    private fun formatSpentTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return when {
            minutes >= 60 -> String.format("%d:%02d:%02d", minutes / 60, minutes % 60, remainingSeconds)
            minutes >= 1 -> String.format("%d:%02d", minutes, remainingSeconds)
            else -> {
                val formattedSeconds = if (remainingSeconds == 0L) {
                    "00"
                } else {
                    String.format("%02d", remainingSeconds)
                }
                String.format("0:%s", formattedSeconds)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    private val _choreTitles: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    private val _incomingChoreTitles = choreRepository.getChoresStream().map {
        getChoreTitles(it)
    }.catch { cause ->
        handleError(cause)
        emit(mutableListOf())

    }

    val uiSpinState: StateFlow<SpinModeUiState> = combine(_choreTitles, _incomingChoreTitles ) {
            choreTitles, incomingChoreTitles ->
        SpinModeUiState(
            choreTitles = choreTitles,
            incomingChoreTitles = incomingChoreTitles,

        )
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = SpinModeUiState()
        )


    fun updateChoreTitles(selectedItems: List<String>) {
        val updatedChoreTitles = uiSpinState.value.choreTitles.toMutableSet()
        updatedChoreTitles.clear()
        updatedChoreTitles.addAll(selectedItems)
        _choreTitles.value = updatedChoreTitles.toList()
    }

    fun removeChore(title: String){
        val updatedChoreTitles = uiSpinState.value.choreTitles.toMutableSet()
        updatedChoreTitles.remove(title)
        _choreTitles.value = updatedChoreTitles.toList()
    }

    private fun getChoreTitles(parentChores: List<Chore>): MutableList<String>{
        val choreList: MutableList<String> = mutableListOf()
        uiState.value.chores.forEach { choreItem ->
            val title = parentChores.find { it.id == choreItem.parentChoreId }?.title
            if (title != null ) {
                if(!_choreTitles.value.contains(title)) {
                        choreList.add(title)
                }
            }
        }
        return choreList
    }
    private fun handleError(cause: Throwable) {
        // Handle the error, for example, log it or emit a default value
        Timber.e(cause, "Error loading data")
        _userMessage.value = R.string.loading_chores_error
        // Additional error handling logic if needed
    }

    private fun loadChores(){
        viewModelScope.launch {
            choreRepository.getChores().let { parentChores ->
                _parentChores.addAll(parentChores)
                if(_chores.isNotEmpty()) {
                    _choreTitle.value =
                        parentChores.find { parentChore -> parentChore.id == _chores[0].parentChoreId }?.title ?: ""
                }
            }
        }
    }

}