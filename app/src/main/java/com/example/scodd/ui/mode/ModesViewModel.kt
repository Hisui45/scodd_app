package com.example.scodd.ui.mode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.*
import com.example.scodd.model.ScoddMode.*
import com.example.scodd.utils.WhileUiSubscribed
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*

data class ModesUiState(
    val parentWorkflows: List<Workflow> = emptyList(),
    val parentChores: List<Chore> = emptyList(),
    val choreItems: List<ChoreItem> = emptyList(),
    val rooms: List<Room> = emptyList(),

    //Pre-load
    val selectedWorkflows: List<String> = emptyList(),
    //Individual Chores from Workflow
    val choresFromWorkflow: List<String> = emptyList(),
    val selectedChores: List<String> = emptyList(),
    val selectedRooms: List<String> = emptyList(),

    val isLoading: Boolean = false,
    var userMessage: Int? = null,
    val choreItemError: String? = null,
    
    val mode: ScoddMode = BankMode

)
/**
 * ViewModel for the Details screen.
 */
@HiltViewModel
class ModesViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val modeId: String? = savedStateHandle["modeId"]

    init {
        viewModelScope.launch {
            if(modeId != null){
                loadMode(modeId)
            }
            loadWorkflows()
        }
    }

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _choreItemError: MutableStateFlow<String?> = MutableStateFlow(null)

    private val _choreItems = choreRepository.getChoreItemsStream()
        .catch {
            handleChoreStreamError()
            emit(emptyList())
        }

    //Chores that show in UI
    private val _selectedChores: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    //Chores that show in UI
    private val _choresFromWorkflow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    //Workflow that show selection of workflows
    private val _selectedWorkflows: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    //Workflows that show in UI and are not empty for the user to select
    private var _parentWorkflows: MutableStateFlow<List<Workflow>> = MutableStateFlow(emptyList())

    //Used to make items
    private val _parentChores = choreRepository.getChoresStream()
        .catch {
            handleChoreStreamError()
            emit(emptyList())
        }

    //Chores that show in UI
    private val _selectedRooms: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    //Used to make items
    private val _rooms = choreRepository.getRoomsStream()
        .catch {
            handleChoreStreamError()
            emit(emptyList())
        }

    private fun removeEmptyWorkflows(workflows: List<Workflow>, choreItems: List<ChoreItem>): List<Workflow>{
        val notEmptyWorkflows: MutableList<Workflow> = mutableListOf()
        if(choreItems.isNotEmpty()){
            workflows.forEach { workflow ->
                if(choreItems.any{choreItem -> choreItem.parentWorkflowId == workflow.id}){
                    notEmptyWorkflows.add(workflow)
                }
            }
        }
        return notEmptyWorkflows
    }

    val uiState: StateFlow<ModesUiState> =
        when(modeId){
        QuestMode.modeId -> {
            combine(
                _rooms, _selectedRooms, _userMessage
            ){rooms, selectedRooms, userMessage  ->
                ModesUiState(
                    rooms = rooms,
                    userMessage = userMessage,
                    selectedRooms = selectedRooms,
                    mode = QuestMode,
                    )
            }.stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = ModesUiState()
            )
        }
        else -> {
            combine(
                _parentChores, _choresFromWorkflow, _selectedChores, _choreItems, _userMessage
            ){parentChores, choresFromWorkflow,selectedChores, choreItems, userMessage  ->
                ModesUiState(
                    parentWorkflows = removeEmptyWorkflows(_parentWorkflows.value, choreItems),
                    choreItems = choreItems,
                    parentChores = parentChores,
                    choresFromWorkflow = choresFromWorkflow,
                    selectedChores = selectedChores,
                    userMessage = userMessage,
                    choreItemError = _choreItemError.value,
                    mode = when(modeId){
                        BankMode.modeId ->{
                            BankMode
                        }
                        TimeMode.modeId ->{
                            TimeMode
                        }
                        SandMode.modeId ->{
                            SandMode
                        }
                        SpinMode.modeId ->{
                            SpinMode
                        }
                        else -> {
                            BankMode
                        }
                    },
                    )
            }.stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = ModesUiState()
            )
        }
    }
    private fun handleChoreStreamError() {
        _userMessage.value = R.string.loading_chores_error
    }

    fun isWorkflowSelected(workflowId: String): Boolean {
        return _selectedWorkflows.value.any{it == workflowId}
    }

    private fun updateModeWorkflows(modeId: String) {
        viewModelScope.launch {
            choreRepository.updateModeWorkflows(
                modeId = modeId,
                selectedWorkflows = _selectedWorkflows.value,
                workflowChores = _choresFromWorkflow.value
            )
        }
    }

    private fun updateModeWorkflowChores(modeId: String) {
        viewModelScope.launch {
            choreRepository.updateModeWorkflowChores(
                modeId = modeId,
                workflowChores = _choresFromWorkflow.value
            )
        }
    }
    private fun updateModeChores(modeId: String) {
        viewModelScope.launch {
            choreRepository.updateModeChores(
                modeId = modeId,
                chores = _selectedChores.value
            )
        }
    }
    fun selectWorkflow(workflow: Workflow) {
        val selectedWorkflows = _selectedWorkflows.value.toMutableList()
        val choresFromWorkflow = _choresFromWorkflow.value.toMutableList()
        if (workflow.id in selectedWorkflows) {
            selectedWorkflows.remove(workflow.id)
            val choresToRemove = workflowChores(workflow.id)
            choresToRemove.forEach {choreId ->
                choresFromWorkflow.remove(choreId)
            }
        } else {
            selectedWorkflows.add(workflow.id)
            choresFromWorkflow.addAll(workflowChores(workflow.id))
        }
        _selectedWorkflows.value = selectedWorkflows.toList()
        _choresFromWorkflow.value = choresFromWorkflow.toList()
        if (modeId != null) {
            updateModeWorkflows(modeId)
        }
    }

    private fun workflowChores(workflowId: String): Set<String>{
        val workflowChores: MutableList<String> = mutableListOf()
        uiState.value.choreItems.forEach { choreItem ->
            if(choreItem.parentWorkflowId == workflowId){
                workflowChores.add(choreItem.parentChoreId)
            }
        }
        return workflowChores.toSet()
    }

    fun selectedItems(selectedItems: List<String>) {
        val selectedChores = uiState.value.selectedChores.toMutableSet()
        selectedChores.clear()
        selectedChores.addAll(selectedItems)
        _selectedChores.value = selectedChores.toList()
        if (modeId != null) {
            updateModeChores(modeId)
        }
    }

    fun checkIsDistinct(choreId: String): Boolean {
        val count = uiState.value.choresFromWorkflow.count { it == choreId } + uiState.value.selectedChores.count{ it == choreId}
        if(count > 1){
            return false
        }
        return true
    }

    fun checkExistInWorkflow(choreId: String): Boolean{
        uiState.value.choreItems.forEach {choreItem ->
            if(choreItem.parentChoreId == choreId && _selectedWorkflows.value.any { it == choreItem.parentWorkflowId }){
                return true
            }
        }
        return false
    }

    fun getChoreTitle(parentChoreId: String): String {
        return uiState.value.parentChores.find { it.id == parentChoreId }?.title?: ""
    }

//    fun refresh() {
//        _isLoading.value = true
//        viewModelScope.launch {
//            choreRepository.refreshWorkflow(workflowId)
//            _isLoading.value = false
//        }
//    }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    fun itemErrorMessageShown() {
        _choreItemError.value = null
        _userMessage.value = null
    }

    fun showItemErrorMessage(message: Int, choreId: String){
        _choreItemError.value = choreId
        _userMessage.value = message
    }

    fun removeDuplicateItem(choreItem: String) {
        val selectedChores = _selectedChores.value.toMutableList()
        var found = false
        selectedChores.removeIf {
            if (!found && it == choreItem) {
                found = true
                true
            } else {
                false
            }
        }
        _selectedChores.value = selectedChores
        if (modeId != null) {
            updateModeChores(modeId)

        }
    }

    private suspend fun loadWorkflows(){
        choreRepository.getWorkflows().let { workflows ->
            _parentWorkflows.value = workflows
        }
    }

    private suspend fun loadMode(modeId: String) {
        choreRepository.getMode(modeId).let {
            mode ->
            if (mode != null) {
                _selectedWorkflows.value = mode.selectedWorkflows
                _choresFromWorkflow.value = mode.workflowChores
                _selectedChores.value = mode.chores
                _selectedRooms.value = mode.rooms
                when(modeId){
                    SandMode.modeId ->{
                        _hourValue.value = mode.rooms[0]
                        _minuteValue.value = mode.rooms[1]
                        _secondValue.value = mode.rooms[2]
                        _showGuided.value = mode.rooms[3] == "1"
                    }
                }
            }
        }
    }

    /**
     * BANK MODE
     */
    fun calculatePotentialPayout(): Int {
        val parentChores = uiState.value.parentChores
        val selectedChores = uiState.value.selectedChores


        val potentialPayoutFromWorkflow = uiState.value.choresFromWorkflow.sumOf { parentChoreId ->
            if(parentChores.find { it.id == parentChoreId }?.isBankModeActive == true){
                parentChores.find { it.id == parentChoreId }?.bankModeValue?: 0
            }else{
                0
            }
        }

        val potentialPayoutFromSelectedChores = selectedChores.sumOf { parentChoreId ->
            if(parentChores.find { it.id == parentChoreId }?.isBankModeActive == true){
                parentChores.find { it.id == parentChoreId }?.bankModeValue?: 0
            }else{
                0
            }
        }

        return potentialPayoutFromWorkflow + potentialPayoutFromSelectedChores
    }

    fun getChoreBankModeValue(parentChoreId: String): Int {
        val choreItem = uiState.value.parentChores.find { it.id == parentChoreId }
        if (choreItem != null) {
            return if(choreItem.isBankModeActive){
                choreItem.bankModeValue
            }else{
                -1
            }
        }
        return -1
    }

    /**
     * TIME MODE
     */
    fun getChoreTimeModeValue(parentChoreId: String): Int {
        val choreItem = uiState.value.parentChores.find { it.id == parentChoreId }
        if (choreItem != null) {
            return if(choreItem.isTimeModeActive){
                choreItem.timerModeValue
            }else{
                -1
            }
        }
        return -1
    }

    fun getChoreTimeUnit(parentChoreId: String): String {
        val parentChore = uiState.value.parentChores.find { it.id == parentChoreId }
        val timerModeValue = parentChore?.timerModeValue ?: 0
        return when (parentChore?.timerOption ?: ScoddTime.MINUTE) {
            ScoddTime.SECOND -> if (timerModeValue > 1) "secs" else "sec"
            ScoddTime.MINUTE -> if (timerModeValue > 1) "mins" else "min"
            ScoddTime.HOUR -> if (timerModeValue > 1) "hrs" else "hr"
            else -> " "
        }
    }

    fun calculateEstimatedTime(): String {
        val parentChores = uiState.value.parentChores
        val selectedChores = uiState.value.selectedChores

        val potentialPayoutFromWorkflow = uiState.value.choresFromWorkflow.sumOf { parentChoreId ->
            if(parentChores.find { it.id == parentChoreId }?.isTimeModeActive == true){
                val value = parentChores.find { it.id == parentChoreId }?.timerModeValue ?: 0
                val unit = parentChores.find { it.id == parentChoreId }?.timerOption ?: ScoddTime.MINUTE
                TimeUtils.convertToMinutes(value,unit)
            }else{
                TimeUtils.convertToMinutes(0, ScoddTime.MINUTE)
            }
        }

        val potentialPayoutFromSelectedChores = selectedChores.sumOf { parentChoreId ->
            if(parentChores.find { it.id == parentChoreId }?.isTimeModeActive == true){
                val value = parentChores.find { it.id == parentChoreId }?.timerModeValue ?: 0
                val unit = parentChores.find { it.id == parentChoreId }?.timerOption ?: ScoddTime.MINUTE
                TimeUtils.convertToMinutes(value,unit)
            }else{
                TimeUtils.convertToMinutes(0, ScoddTime.MINUTE)
            }
        }

        val totalTimeInMinutes = potentialPayoutFromWorkflow + potentialPayoutFromSelectedChores

        return TimeUtils.convertToString(totalTimeInMinutes)

    }


    fun allSelectedChores(): List<String>{
        return _selectedChores.value + _choresFromWorkflow.value
    }
    fun getFirstChoreTimeDuration(): Long {

        val currentChore = uiState.value.parentChores.find { it.id ==  allSelectedChores()[0]}
        val duration = currentChore?.timerModeValue?.toLong()?: -1
        val unit = currentChore?.timerOption?: ScoddTime.MINUTE

        return TimeUtils.getChoreTimerDurationInSeconds(duration, unit)
    }

    /**
     * QUEST MODE
     */

    fun selectAll(selectAll: Boolean) {
        if(selectAll){
            _selectedRooms.value = emptyList()
        }else{
            val selectedRooms = uiState.value.selectedRooms.toMutableList()
            selectedRooms.addAll(uiState.value.rooms.map { it.id })
            _selectedRooms.value = selectedRooms
        }
        updateModeRooms(QuestMode.modeId)
    }

    private fun updateModeRooms(modeId: String){
        viewModelScope.launch {
            choreRepository.updateModeRooms(
                modeId = modeId,
                rooms = _selectedRooms.value
            )
        }
    }

    fun isRoomSelected(roomId: String): Boolean {
        return _selectedRooms.value.any{it == roomId}
    }

    fun selectRoom(room: Room) {
        val selectedRooms = _selectedRooms.value.toMutableList()
        if (room.id in selectedRooms) {
            selectedRooms.remove(room.id)
        } else {
            selectedRooms.add(room.id)
        }
        _selectedRooms.value = selectedRooms.toList()
        updateModeRooms(QuestMode.modeId)
    }

    fun getRoomTitles(): List<String>{
        val selectedTitles: MutableList<String> = mutableListOf()
        _selectedRooms.value.forEach {
            val title = uiState.value.rooms.find { room -> room.id == it }?.title
            if(title != null){
                selectedTitles.add(title)
            }
        }
        return selectedTitles
    }


    /**
     * SAND MODE
     */

    data class TimerUiState(
        val hourValue: String = "",
        val minuteValue: String = "",
        val secondValue: String = "",
        val showGuided: Boolean = false
    )

    private val _hourValue: MutableStateFlow<String> = MutableStateFlow("")
    private val _minuteValue: MutableStateFlow<String> = MutableStateFlow("")
    private val _secondValue: MutableStateFlow<String> = MutableStateFlow("")

    private val _showGuided: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val uiTimerState: StateFlow<TimerUiState> = combine(
        _hourValue, _minuteValue, _secondValue, _showGuided
    ){hourValue, minuteValue, secondValue, showGuided  ->
        TimerUiState(
            hourValue = hourValue,
            minuteValue = minuteValue,
            secondValue = secondValue,
            showGuided = showGuided
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = TimerUiState()
    )

    private fun updateTimeValue(index: Int){
        val selectedRooms = _selectedRooms.value.toMutableList()
        selectedRooms[index] =
            when(index){
                0 ->{
                    _hourValue.value
                }
                1 ->{
                    _minuteValue.value
                }
                2->{
                    _secondValue.value
                }
                3->{
                    if (_showGuided.value) "1" else "0"
                }
                else -> {
                    ""
                }
            }
        _selectedRooms.value = selectedRooms
        updateModeRooms(SandMode.modeId)
    }

    fun changeHrValue(newValue: String) {
        if (newValue.length >= 2) _hourValue.value = newValue.substring(0, 2) else _hourValue.value = newValue
        updateTimeValue(0)
    }

    fun changeMinValue(newValue: String) {
        if (newValue.length >= 2) _minuteValue.value = newValue.substring(0, 2) else _minuteValue.value = newValue
        updateTimeValue(1)
    }

    fun changeSecValue(newValue: String) {
        if (newValue.length >= 2) _secondValue.value = newValue.substring(0, 2) else _secondValue.value = newValue
        updateTimeValue(2)
    }

    fun switchShowGuided() {
        _showGuided.value = !_showGuided.value
        updateTimeValue(3)
    }

}

