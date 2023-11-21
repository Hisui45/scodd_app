package com.example.scodd.ui.chore

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.scodd.ui.chore.ChoreInputType.FREQUENCY
import com.example.scodd.ui.chore.ChoreInputType.TIMER
import com.example.scodd.ui.chore.ChoreInputType.BANK
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

data class CreateChoreUiState(
    val title: String = "",
    val rooms: List<Room> = emptyList(),
    val choreRooms: List<String> = emptyList(),
    val choreWorkflows: List<String> = emptyList(),
    val choreItems: List<ChoreItem> = emptyList(),
    val workflows: List<Workflow> = emptyList(),
    val scheduleTimeText: String = "",
    val routineInfo: RoutineInfo = RoutineInfo(),
    val frequencyErrorMessage: Int? = null,
    val isTimeModeActive: Boolean = false,
    val timerModeValue: Int = -1,
    val timerOption: ScoddTime = ScoddTime.MINUTE,
    val timerErrorMessage: Int? = null,
    val isBankModeActive: Boolean = false,
    val bankModeValue: Int = 0,
    val isFavorite: Boolean = false,
    val isChoreSaved: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isChoreDeleted: Boolean = false
)

/**
 * ViewModel for the Add/Edit screen.
 */
@HiltViewModel
class CreateChoreViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val choreId: String? = savedStateHandle["choreId"]

    // A MutableStateFlow needs to be created in this ViewModel. The source of truth of the current
    // editable Chore is the ViewModel, we need to mutate the UI state directly in methods such as
    // `updateTitle` or `updateDescription`
    private val _uiState = MutableStateFlow(CreateChoreUiState())
    val uiState: StateFlow<CreateChoreUiState> = _uiState.asStateFlow()

    /**
     * TODO: warning message when changes have been made but not saved priority:3
     * TODO: shows save button when changes have been made, exit/close/done when saved
     */
    init {
        if (choreId != null) {
            loadChore(choreId)
        }else{
            viewModelScope.launch {
                loadRooms()
                loadWorkflows()
            }
        }
    }

    // Called when clicking on 'Save' button
    fun saveChore() {
        if (uiState.value.title.isEmpty()) {
            _uiState.update {
                it.copy(userMessage = R.string.chore_no_title_message)
            }
            return
        }

        if (choreId == null) {
            createNewChore()
        } else {
            updateChore()
        }
    }

    private fun createNewChore() = viewModelScope.launch {
        choreRepository.createChore(
            uiState.value.title, uiState.value.choreRooms, uiState.value.choreWorkflows, uiState.value.routineInfo,
            uiState.value.isTimeModeActive, uiState.value.timerModeValue, uiState.value.timerOption,
            uiState.value.isBankModeActive, uiState.value.bankModeValue, uiState.value.isFavorite
        )
        _uiState.update {
            it.copy(isChoreSaved = true)
        }
    }

    private fun updateChore() {
        if (choreId == null) {
            throw RuntimeException("updateChore() was called but chore is new.")
        }
        viewModelScope.launch {
            choreRepository.updateChore(
                choreId = choreId,
                title = uiState.value.title,
                rooms = uiState.value.choreRooms,
                workflows = uiState.value.choreWorkflows,
                routineInfo = uiState.value.routineInfo,
                isTimeModeActive = uiState.value.isTimeModeActive,
                timerModeValue = uiState.value.timerModeValue,
                timerOption = uiState.value.timerOption,
                isBankModeActive = uiState.value.isBankModeActive,
                bankModeValue = uiState.value.bankModeValue,
                isFavorite = uiState.value.isFavorite
            )
            _uiState.update {
                it.copy(isChoreSaved = true)
            }
        }
    }

    fun snackbarMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(title = newTitle)
        }
    }

    fun isRoomSelected(room: Room): Boolean {
        return uiState.value.choreRooms.any { it == room.id }
    }

    fun selectRoom(roomId: String) {
        val updatedRooms = uiState.value.choreRooms.toMutableSet()

        if (roomId in updatedRooms) {
            updatedRooms.remove(roomId)
        } else {
            updatedRooms.add(roomId)
        }
        _uiState.update { it.copy(choreRooms = updatedRooms.toList()) }
    }

    fun isWorkflowSelected(workflowId: String): Boolean {
        return _uiState.value.choreWorkflows.any{it == workflowId}
    }

    fun selectWorkflow(workflow: Workflow) {
        val selectedWorkflows = uiState.value.choreWorkflows.toMutableSet()

        if (workflow.id in selectedWorkflows) {
            selectedWorkflows.remove(workflow.id)
        } else {
            selectedWorkflows.add(workflow.id)
        }
        _uiState.update { it.copy(choreWorkflows = selectedWorkflows.toList()) }
    }

    fun changeValue(value: String, type: ChoreInputType) {
        var newValue = if (value.isEmpty()) -1 else value.toInt()
        var errorMessage: Int? = null
        when (newValue) {
            -1 -> {
                errorMessage = R.string.value_missing_error
                newValue = -1
            }

            0 -> {
                errorMessage = R.string.greater_value_error
                newValue = 0
            }
        }
        when (type) {
            FREQUENCY -> {
                _uiState.update {
                    val currentRoutineInfo = it.routineInfo
                    it.copy(
                        frequencyErrorMessage = errorMessage,
                        routineInfo = currentRoutineInfo.copy(frequencyValue = newValue)
                    )
                }
            }

            TIMER -> {
                _uiState.update {
                    it.copy(timerErrorMessage = errorMessage, timerModeValue = newValue)
                }
            }

            BANK -> {
                _uiState.update {
                    it.copy(bankModeValue = newValue)
                }
            }
        }
    }

    fun switchOneTime(oneTime: Boolean) {
        _uiState.update {
            val currentRoutineInfo = it.routineInfo
            it.copy(routineInfo = currentRoutineInfo.copy(isOneTime = oneTime))
        }
    }

    fun selectFrequencyOption(scoddTime: ScoddTime) {
        _uiState.update {
            val currentRoutineInfo = it.routineInfo
            it.copy(routineInfo = currentRoutineInfo.copy(frequencyOption = scoddTime))
        }

    }

    fun selectScheduleType(scoddTime: ScoddTime) {
        _uiState.update {
            val currentRoutineInfo = it.routineInfo
            it.copy(routineInfo = currentRoutineInfo.copy(scheduleType = scoddTime))
        }
    }

    fun selectWeekday(scoddTime: ScoddTime) {
        _uiState.update {
            val currentRoutineInfo = it.routineInfo
            it.copy(routineInfo = currentRoutineInfo.copy(weeklyDay = scoddTime))
        }

    }

    fun selectTimerOption(scoddTime: ScoddTime) {
        _uiState.update {
            it.copy(timerOption = scoddTime)
        }
    }

    fun switchTimerMode(timerModeActive: Boolean) {
        _uiState.update {
            it.copy(isTimeModeActive = timerModeActive )
        }
    }

    fun switchBankMode(bankModeActive: Boolean) {
        _uiState.update {
            it.copy(isBankModeActive = bankModeActive)
        }
    }

    fun dateChange(date: Long?) {
        _uiState.update {
            val currentRoutineInfo = it.routineInfo
            val scheduleTimeText = updateScheduleTimeText(date, currentRoutineInfo.hour, currentRoutineInfo.minute)
            if (scheduleTimeText == null) {
                it.copy(userMessage = R.string.loading_date_error)
            }else{
                it.copy(
                    routineInfo = currentRoutineInfo.copy(date = date),
                    scheduleTimeText = scheduleTimeText
                )
            }
        }
    }

    fun timeChange(hour: Int, minute: Int) {
        _uiState.update {
            val currentRoutineInfo = it.routineInfo
            val scheduleTimeText = updateScheduleTimeText(currentRoutineInfo.date, hour, minute)
            if (scheduleTimeText == null) {
                it.copy(userMessage = R.string.loading_date_error)
            }else{
                it.copy(
                    routineInfo = currentRoutineInfo.copy(hour = hour, minute = minute),
                    scheduleTimeText = scheduleTimeText
                )
            }
        }
    }

    private fun loadChore(choreId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            loadChoreData(choreId)
            loadRooms()
            loadWorkflows()
            setChoreWorkflows()
        }
        _uiState.update {
            it.copy(isLoading = false)
        }
    }
    private suspend fun loadChoreData(choreId: String) {
        _uiState.update { it.copy(isLoading = true) }

        val chore = choreRepository.getChore(choreId)

        _uiState.update { currentState ->
            chore?.let {
                val date = it.routineInfo.date
                val hour = it.routineInfo.hour
                val minute = it.routineInfo.minute
                val scheduleTimeText = updateScheduleTimeText(date, hour, minute)

                currentState.copy(
                    scheduleTimeText = scheduleTimeText ?: "",
                    title = it.title,
                    choreRooms = it.rooms,
//                    choreWorkflows = it.workflows,
                    routineInfo = it.routineInfo,
                    isTimeModeActive = it.isTimeModeActive,
                    timerModeValue = it.timerModeValue,
                    timerOption = it.timerOption,
                    isBankModeActive = it.isBankModeActive,
                    bankModeValue = it.bankModeValue,
                    isFavorite = it.isFavorite,
                    isLoading = false,
                    userMessage = if (scheduleTimeText == null) R.string.loading_date_error else null
                )
            } ?: currentState.copy(isLoading = false)
        }
    }

    private suspend fun loadRooms(){
        choreRepository.getRooms().let { rooms ->
            _uiState.update {
                it.copy(
                    rooms = rooms
                )
            }
        }
    }

    private suspend fun loadWorkflows(){
        choreRepository.getWorkflows().let { workflows ->
            _uiState.update {
                it.copy(
                    workflows = workflows
                )
            }
        }
    }

    private suspend fun setChoreWorkflows(){
        choreRepository.getChoreItems().let { choreItems ->
            val choresWorkflows: MutableList<String> = mutableListOf()
            choreItems.forEach { choreItem ->
                if(choreItem.parentChoreId == choreId){
                    choresWorkflows.add(choreItem.parentWorkflowId)
                }
            }
            _uiState.update {
                it.copy(
                    choreWorkflows = choresWorkflows.toList()
                )
            }
        }
    }

    private fun updateScheduleTimeText(date: Long?, hour: Int, minute: Int): String? {
        return date?.let {
            val selectedDate = Instant.ofEpochMilli(date).atOffset(ZoneOffset.UTC)
            val selectedTime = LocalTime.of(hour, minute)
                .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

            "${selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))} ${
                context.getString(R.string.date_label_conj)
            } $selectedTime"
        }
    }

    fun createWorkflow(title: String) {
        viewModelScope.launch {
            choreRepository.createWorkflow(title)
            loadWorkflows()
        }
    }

    fun deleteChore() = viewModelScope.launch {
        if (choreId != null) {
            choreRepository.deleteChore(choreId)
            _uiState.update {
                it.copy(isChoreDeleted = true)
            }
        }
    }
}


