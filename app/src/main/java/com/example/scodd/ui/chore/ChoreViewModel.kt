package com.example.scodd.ui.chore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.Chore
import com.example.scodd.model.Room
import com.example.scodd.model.Workflow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.scodd.utils.WhileUiSubscribed
import kotlinx.coroutines.flow.*
import timber.log.Timber

data class ChoreUiState(
    val items: List<Chore> = listOf(),
    val workflows: List<Workflow> = listOf(),
    val rooms: List<Room> = listOf(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val favoriteSelected: Boolean = false
)
@HiltViewModel
class ChoreViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _myRooms = MutableStateFlow(savedStateHandle.get<List<Room>>(SELECTED_SAVED_STATE_KEY) ?: emptyList())
    private val myRooms: StateFlow<List<Room>> = _myRooms

    private val _showFavorite = MutableStateFlow(savedStateHandle.get<Boolean>(FAVORITE_SAVED_STATE_KEY) ?: false)
    private val showFavorite: StateFlow<Boolean> = _showFavorite

    fun toggleRoom(room : Room){
        val currentRooms = myRooms.value.toMutableList()
        if (myRooms.value.map{it.id}.contains(room.id)) {
            currentRooms.remove(currentRooms.find{ it.id == room.id })
        } else {
            currentRooms.add(room)
        }
        _myRooms.value = currentRooms
    }

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)


    private val _filteredChores = combine(choreRepository.getChoresStream(), _myRooms, _showFavorite) { chores, selectedRooms, isFavorite ->
        filterChores(chores, selectedRooms, isFavorite)
    }
        .catch { cause ->
            handleError(cause)
            emit(listOf(Chore("1C", "Vacuum")))
        }

    private val _workflows = choreRepository.getWorkflowsStream()
        .catch { cause ->
            handleError(cause)
            emit(emptyList())
        }

    private val _rooms = combine(choreRepository.getRoomsStream(), _myRooms) { chores, selectedRooms ->
        arrangeRooms(chores, selectedRooms)
    }
        .catch { cause ->
            handleError(cause)
            emit(emptyList())
        }

    val uiState: StateFlow<ChoreUiState> = combine(_isLoading, _userMessage, _filteredChores, _rooms, _workflows) { isLoading, userMessage, chores, rooms, workflows ->
        ChoreUiState(
            isLoading = isLoading,
            userMessage = if (chores.isEmpty()) R.string.loading_chores_error else userMessage,
            items = chores,
            rooms = rooms,
            workflows = workflows
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = ChoreUiState(isLoading = true)
        )

    private fun handleError(cause: Throwable) {
        // Handle the error, for example, log it or emit a default value
        Timber.e(cause, "Error loading data")
        _userMessage.value = R.string.loading_chores_error
        // Additional error handling logic if needed
    }

    fun setFavoriteFilterType(){
        _showFavorite.value = !showFavorite.value
    }

    fun getFavorite(): Boolean{
        return _showFavorite.value
    }

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
        }

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
            if (selectedRooms.isEmpty() && !isFavoriteSelected) {
                // Case: No rooms selected and not filtering by favorites
                choresToShow.add(chore)
            } else if (selectedRooms.any { selected ->
                    chore.rooms.any { room -> selected.id == room }
                } || (isFavoriteSelected && chore.isFavorite)) {
                // Case: Either rooms match the selected rooms or filtering by favorites and chore is a favorite
                choresToShow.add(chore)
            }
        }
        return choresToShow
    }

    private fun arrangeRooms(rooms: List<Room>, selectedRooms: List<Room>): List<Room> {
        val roomsToShow: MutableList<Room> = mutableListOf()

        rooms.forEach {
            if(selectedRooms.any {selectedRoom -> selectedRoom.id == it.id }){
                val newRoom = Room(it.id, it.title, true)
                roomsToShow.add(newRoom)
            }else{
                roomsToShow.add(it)
            }
        }

        return if(roomsToShow.any { it.selected }){
            roomsToShow.sortedByDescending { it.selected }

        }else{
            roomsToShow.sortedBy { it.title }
        }
    }

    fun getRoomTitle(chore: Chore, index: Int): String {
        val roomText: String = if(chore.rooms.isNotEmpty()){
            val allRooms = uiState.value.rooms
            val room = allRooms.find{ it.id == chore.rooms[index]}
            room?.title ?: " "
        }else{
            ""
        }
        return roomText
    }

}

// Used to save the current filtering in SavedStateHandle.
const val SELECTED_SAVED_STATE_KEY = "selected"
const val FAVORITE_SAVED_STATE_KEY = "favorites"
