package com.example.scodd.ui.mode.quest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.Room
import com.example.scodd.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestUiState(
    val rooms: List<Room> = emptyList(),
    //Pre-load
    val selectedRooms: List<String> = emptyList(),
    
    val isLoading: Boolean = false,
    var userMessage: Int? = null
    )
/**
 * ViewModel for the Details screen.
 */
@HiltViewModel
class QuestModeViewModel @Inject constructor(
    private val choreRepository: ChoreRepository
) : ViewModel() {

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _choreItemError: MutableStateFlow<String?> = MutableStateFlow(null)
    
    //Chores that show in UI
    private val _selectedRooms: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    //Used to make items
    private val _rooms = choreRepository.getRoomsStream()
        .catch {
            handleChoreStreamError()
            emit(emptyList())
        }

    
    val uiState: StateFlow<QuestUiState> = combine(
         _rooms, _selectedRooms, _userMessage
    ){rooms, selectedRooms, userMessage ->
            QuestUiState(
                rooms = rooms,
                selectedRooms = selectedRooms,
                userMessage = userMessage,
            )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = QuestUiState()
    )


//    private val _isLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
//            loadWorkflows()
        }
    }

    private fun handleChoreStreamError() {
        _userMessage.value = R.string.loading_chores_error
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
    }


//    fun checkIsDistinct(choreId: String): Boolean {
//        val count = uiState.value.choresFromWorkflow.count { it == choreId } + uiState.value.selectedChores.count{ it == choreId}
//        if(count > 1){
//            return false
//        }
//        return true
//    }

    fun getChoreTitle(parentRoomId: String): String {
        return uiState.value.rooms.find { it.id == parentRoomId }?.title?: ""
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
        _userMessage.value = null
        _choreItemError.value = null
    }

    fun showItemErrorMessage(message: Int, choreId: String){
        _choreItemError.value = choreId
        _userMessage.value = message
    }

    fun selectAll(selectAll: Boolean) {
        if(selectAll){
            _selectedRooms.value = emptyList()
        }else{
            val selectedRooms = uiState.value.selectedRooms.toMutableList()
                selectedRooms.addAll(uiState.value.rooms.map { it.id })
            _selectedRooms.value = selectedRooms
        }
    }

}

