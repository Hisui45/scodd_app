package com.example.scodd.ui.chore

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.Chore
import com.example.scodd.model.Room
import com.example.scodd.utils.Async
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.scodd.utils.WhileUiSubscribed
import kotlinx.coroutines.flow.*

data class ChoreUiState(
    val items: List<Chore> = listOf(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

data class RoomUiState(
    val rooms: List<Room> = listOf(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val favoriteSelected: Boolean = false
)

@HiltViewModel
class ChoreViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiRoomState = MutableStateFlow(RoomUiState())
    val uiRoomState: StateFlow<RoomUiState> = _uiRoomState.asStateFlow()

    private val _selectedRooms = savedStateHandle.getStateFlow(SELECTED_ROOMS_SAVED_STATE_KEY,
        _uiRoomState.value.rooms.filter { it.selected })

     private val _isFavorite = savedStateHandle.getStateFlow(FAVORITE_ROOMS_SAVED_STATE_KEY, false)

    private fun loadRooms() {
        _uiRoomState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            choreRepository.getRooms().let { rooms ->
                if(rooms.isNotEmpty()){
                    _uiRoomState.update {
                        it.copy(
                            rooms = rooms,
                            isLoading = false
                        )
                    }
                }else{
                    _uiRoomState.update {
                         it.copy(isLoading = false)
                    }
                }
            }
            savedStateHandle[SELECTED_ROOMS_SAVED_STATE_KEY] = _uiRoomState.value.rooms.filter { it.selected }
        }
    }

    fun toggleRoom(room : Room){
        _uiRoomState.update { currentState ->
            // Modify the currentState to toggle the selected status of the room
            val updatedRooms = currentState.rooms.map { existingRoom ->
                if (existingRoom == room) {
                    // Toggle the selected status of the target room
                    existingRoom.copy(selected = !existingRoom.selected)
                } else {
                    // Keep other rooms unchanged
                    existingRoom
                }
            }

//            updatedRooms.sortedByDescending { it.selected } +
//            updatedRooms.sortedBy { it.selected }
            // Create and return a new ChoreUiState with the updated rooms
            currentState.copy(rooms = arrangeRooms(updatedRooms))
        }
        savedStateHandle[SELECTED_ROOMS_SAVED_STATE_KEY] = _uiRoomState.value.rooms.filter { it.selected }
    }

    init {
        loadRooms()
    }

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)

    private val _filteredChoresAsync =
        combine(choreRepository.getChoresStream(), _selectedRooms, _isFavorite) { chores, selectedRooms, isFavorite ->
            filterChores(chores, selectedRooms, isFavorite)
        }
            .map { Async.Success(it) }
            .catch<Async<List<Chore>>> { emit(Async.Error(R.string.loading_tasks_error)) }

    val uiState: StateFlow<ChoreUiState> = combine(_isLoading, _userMessage, _filteredChoresAsync){
            isLoading, userMessage, choresAsync ->
        when(choresAsync){
            Async.Loading -> {
                ChoreUiState(isLoading = true)
            }
            is Async.Error -> {
                ChoreUiState(
                    userMessage = R.string.error_chores
                )
            }
            is Async.Success -> {
                ChoreUiState(
                    items = choresAsync.data,
                    isLoading = isLoading,
                    userMessage = userMessage
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = ChoreUiState(isLoading = true)
        )

//        val selectedChips = _uiState.value.rooms.filter { it.selected }.map { it }
//
//        if(selectedChips.isNotEmpty()){
//            savedStateHandle[TASKS_FILTER_SAVED_STATE_KEY] = ROOM_TASKS
//        }
//        else{
//            savedStateHandle[TASKS_FILTER_SAVED_STATE_KEY] = ALL_TASKS
//        }
//
//        savedStateHandle[SELECTED_ROOMS_SAVED_STATE_KEY] = _uiState.value.rooms.toList()
//    }
    fun setFavoriteFilterType(){
        _uiRoomState.update { currentState ->
            // Modify the currentState to toggle the selected status of the room
            val favoriteValue = currentState.favoriteSelected
            currentState.copy(favoriteSelected = !favoriteValue )
        }
        savedStateHandle[FAVORITE_ROOMS_SAVED_STATE_KEY] = _uiRoomState.value.favoriteSelected
    }

//    fun clearCompletedTasks() {
//        viewModelScope.launch {
//            taskRepository.clearCompletedChores()
//            showSnackbarMessage(R.string.completed_chores_cleared)
//            refresh()
//        }
//    }

//    fun completeTask(task: Chore, completed: Boolean) = viewModelScope.launch {
//        if (completed) {
//            taskRepository.completeChore(task.id)
//            showSnackbarMessage(R.string.chore_marked_complete)
//        }
////        else {
////            taskRepository.activateTask(task.id)
////            showSnackbarMessage(R.string.task_marked_active)
////        }
//    }

    /**
     * TODO: Decide if we're showing snackbars and come up with appropriate messages
     */
        fun favoriteChore(chore: Chore) = viewModelScope.launch {
            if(chore.isFavorite){
                choreRepository.unFavoriteChore(chore.id)
                showSnackbarMessage(R.string.chore_unmarked_favorite)
            }else{
                choreRepository.favoriteChore(chore.id)
                showSnackbarMessage(R.string.chore_marked_favorite)
            }
//        refresh()
        }

//    fun showEditResultMessage(result: Int) {
//        when (result) {
//            EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_saved_task_message)
//            ADD_EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_added_task_message)
//            DELETE_RESULT_OK -> showSnackbarMessage(R.string.successfully_deleted_task_message)
//        }
//    }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackbarMessage(message: Int) {
        _userMessage.value = message
    }

    fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
            choreRepository.refresh()
            _isLoading.value = false
        }
    }

    private fun filterChores(chores: List<Chore>, selectedRooms: List<Room>, isFavoriteSelected: Boolean): List<Chore> {
        val choresToShow = ArrayList<Chore>()
        for (chore in chores) {
//            if(selectedRooms.isEmpty() && !isFavoriteSelected){
//                choresToShow.add(chore)
//            }else if(selectedRooms.isNotEmpty() && isFavoriteSelected) {
//                if (selectedRooms.any { selected -> chore.rooms.any { room -> selected.id == room.id } }) {
//                    choresToShow.add(chore)
//                } else if (chore.isFavorite) {
//                    choresToShow.add(chore)
//                }
//            }else if(selectedRooms.isNotEmpty() && !isFavoriteSelected){
//                if (selectedRooms.any { selected -> chore.rooms.any { room -> selected.id == room.id } }) {
//                    choresToShow.add(chore)
//                }
//            }else if(selectedRooms.isEmpty() && isFavoriteSelected){
//                if(chore.isFavorite){
//                    choresToShow.add(chore)
//                }
//            }
            if (selectedRooms.isEmpty() && !isFavoriteSelected) {
                // Case: No rooms selected and not filtering by favorites
                choresToShow.add(chore)
            } else if (selectedRooms.any { selected ->
                    chore.rooms.any { room -> selected.id == room.id }
                } || (isFavoriteSelected && chore.isFavorite)) {
                // Case: Either rooms match the selected rooms or filtering by favorites and chore is a favorite
                choresToShow.add(chore)
            }
        }
        return choresToShow
    }

    private fun arrangeRooms(rooms: List<Room>): List<Room> {
        val roomsToShow: MutableList<Room> = mutableListOf()
        roomsToShow.addAll(rooms)
        Log.d("CURIOUS", roomsToShow.toString())
        return if(rooms.any { it.selected }){
            roomsToShow.sortedByDescending { it.selected }

        }else{
            roomsToShow.sortedBy { it.title }
        }
    }
}

// Used to save the current filtering in SavedStateHandle.
const val SELECTED_ROOMS_SAVED_STATE_KEY = "SELECTED_ROOMS_SAVED_STATE_KEY"
const val FAVORITE_ROOMS_SAVED_STATE_KEY = "FAVORITE_ROOMS_SAVED_STATE_KEY"
