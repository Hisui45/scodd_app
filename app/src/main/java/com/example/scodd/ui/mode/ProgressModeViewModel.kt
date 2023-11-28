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

data class SandListUiState(
    val completedChores: List<ChoreItem> = listOf()
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
                _timerValue.value = formatTime(i)
                _timerDuration.value = i
                delay(1000)
            }
            _timerValue.value = formatTime(_timerDuration.value)
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

    fun getCurrentChoreTitle(): String{
       return uiState.value.parentChores.find { it.id ==  uiState.value.chores[_currentChore.value].parentChoreId }?.title?: ""
    }

    fun getIndexChoreTitle(index: Int): String{
        return uiState.value.parentChores.find { it.id ==  uiState.value.chores[index].parentChoreId }?.title?: ""
    }

    fun getCurrentChoreBankAmount(): Int {
        return uiState.value.parentChores.find { it.id ==  uiState.value.chores[_currentChore.value].parentChoreId }?.bankModeValue?: 0
    }

    fun startMode(){
        if(timeDuration != null){
            startTimer(_timerDuration.value)
        }
    }

    private fun getCurrentChoreTimerDuration(): Long{
        val currentChore = uiState.value.parentChores.find { it.id ==  uiState.value.chores[_currentChore.value].parentChoreId }
        val duration = currentChore?.timerModeValue?.toLong()?: -1
        val unit = currentChore?.timerOption?: ScoddTime.SECOND

        return TimeUtils.getChoreTimerDurationInSeconds(duration, unit)
    }

    fun completeChore(){
        val chores = _chores
        val updatedChores: MutableList<ChoreItem> = mutableListOf()
        chores.forEachIndexed { index, choreItem ->
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

    private val _uiSandState = MutableStateFlow(SandListUiState())
    val uiSandState: StateFlow<SandListUiState> = _uiSandState.asStateFlow()

    fun completeIndexChore(choreIndex: Int) {
        val choreItem = _chores[choreIndex]
        val completedChores = uiSandState.value.completedChores.toMutableList()

        if(choreItem in completedChores){
            completedChores.remove(choreItem)
        }else{
            completedChores.add(choreItem)
        }

        _uiSandState.update {
            it.copy(completedChores = completedChores.toList())
        }
        Timber.tag("problem").d(completedChores.contains(choreItem).toString())
    }

    fun setFinished(){
        _isFinished.value = true
    }

     fun nextChore(){
        if (checkIsFinished()) _isFinished.value = true else _currentChore.value = _currentChore.value + 1
         if(modeId == ScoddMode.TimeMode.modeId){
             _timerDuration.value = getCurrentChoreTimerDuration()
             startTimer(_timerDuration.value)
         }
         _isTimeUp.value = false
         _choreTitle.value = getCurrentChoreTitle()
    }

    fun addTimeClick() {
        _isTimeUp.value = false
        startTimer(5 * 60)
    }
    fun checkIsFinished(): Boolean{
        return _currentChore.value >= _chores.size-1
    }

    private fun formatTime(seconds: Long): String {
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
//        if(_choreTitles.value.isEmpty()){
//            setFinished()
//        }
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