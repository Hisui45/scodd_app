package com.example.scodd.ui.chore

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.Room
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.scodd.ui.chore.ChoreInputType.FREQUENCY
import com.example.scodd.ui.chore.ChoreInputType.TIMER
import com.example.scodd.ui.chore.ChoreInputType.BANK
import com.example.scodd.model.RoutineInfo
import com.example.scodd.model.ScoddTime
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

data class CreateChoreUiState(
    val title: String = "",
    val workflows: List<String> = emptyList(),
    val rooms: List<Room> = emptyList(),
    val scheduleTimeText: String = "",
    val routineInfo: RoutineInfo = RoutineInfo(),
    val frequencyErrorMessage: Int? = null,
    val isTimeModeActive: Boolean = false,
    val timerModeValue: Int = 1,
    val timerOption: ScoddTime = ScoddTime.MINUTE,
    val timerErrorMessage: Int? = null,
    val isBankModeActive: Boolean = false,
    val bankModeValue: Int = 0,
    val isFavorite: Boolean = false,
    val isChoreSaved: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
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

    init {
        if (choreId != null) {
            loadChore(choreId)
//            setTime(choreId)
        }
    }

    // Called when clicking on 'Create' button
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

    fun selectRoom(room: Room) {
        val updatedRooms = uiState.value.rooms.toMutableSet()

        if (room in updatedRooms) {
            updatedRooms.remove(room)
        } else {
            updatedRooms.add(room)
        }

        _uiState.update { it.copy(rooms = updatedRooms.toList()) }
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

    private fun createNewChore() = viewModelScope.launch {
        choreRepository.createChore(
            uiState.value.title, uiState.value.rooms, uiState.value.workflows, uiState.value.routineInfo,
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
                rooms = uiState.value.rooms,
                workflows = uiState.value.workflows,
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

    private fun loadChore(choreId: String) { //When editing and we need to populate previous values maybe here?
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            choreRepository.getChore(choreId).let { chore ->
                if (chore != null) {
                    _uiState.update {
                        it.copy(
                            title = chore.title,
                            rooms = chore.rooms,
                            workflows = chore.workflows,
                            routineInfo = chore.routineInfo,
                            isTimeModeActive = chore.isTimeModeActive,
                            timerModeValue = chore.timerModeValue,
                            timerOption = chore.timerOption,
                            isBankModeActive = chore.isBankModeActive,
                            bankModeValue = chore.bankModeValue,
                            isLoading = false
                        )
                    }
                    setTime(choreId)
                } else {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private fun setTime(choreId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            choreRepository.getChore(choreId).let { chore ->
                if (chore != null) {
                    val date = chore.routineInfo.date
                    val hour = chore.routineInfo.hour
                    val minute = chore.routineInfo.minute
                    val scheduleTimeText = updateScheduleTimeText(date, hour, minute)
                    if(scheduleTimeText != null){
                        _uiState.update {
                            it.copy(scheduleTimeText = scheduleTimeText)
                        }
                    }else{
                        _uiState.update {
                            it.copy(userMessage = R.string.loading_date_error)
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
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

    private fun updateScheduleTimeText(date: Long?, hour: Int, minute: Int): String?{
        val scheduleTimeText: String
        return if (date != null) {
            val selectedDate = Instant.ofEpochMilli(date).atOffset(ZoneOffset.UTC)
            val selectedTime =
                LocalTime.of(hour, minute).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
            scheduleTimeText =
                selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) + " " + context.getString(
                    R.string.date_label_conj
                ) + " " +
                        selectedTime
            scheduleTimeText
        } else {
            null
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
}