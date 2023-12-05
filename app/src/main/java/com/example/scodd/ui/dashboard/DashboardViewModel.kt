package com.example.scodd.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.*
import com.example.scodd.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class DashboardUiState(
    val parentChores: List<Chore> = listOf(),
    val filteredChoreItems: Set<ChoreItem> = setOf(),
    val choreItems: List<ChoreItem> = listOf(),
    val rooms: List<Room> = listOf(),
    val userMessage: Int? = null,
    val favoriteSelected: Boolean = false,
    val userName: String = "",
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workflowId: String = ROUNDUP

    private val _myRooms = MutableStateFlow(savedStateHandle.get<List<Room>>(SELECTED_SAVED_STATE_KEY) ?: emptyList())
    private val myRooms: StateFlow<List<Room>> = _myRooms

    private val _showFavorite = MutableStateFlow(savedStateHandle.get<Boolean>(FAVORITE_SAVED_STATE_KEY) ?: false)
    private val showFavorite: StateFlow<Boolean> = _showFavorite

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val _choreItems = choreRepository.getChoreItemsStream()
        .catch { cause ->
            handleError(cause)
            emit(emptyList())
        }

    private val _parentChores = choreRepository.getChoresStream()
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

    private val _userName: MutableStateFlow<String> = MutableStateFlow("")


    private val _filteredChoreItems = combine(
        choreRepository.getChoreItemsStream(),
        choreRepository.getWorkflowStream(workflowId), _myRooms, _showFavorite,
        choreRepository.getChoresStream()
    ) { choreItems, workflow, selectedRooms, isFavorite, parentChores ->
        filterChoreItems(choreItems, workflow, selectedRooms, isFavorite, parentChores)
    }
        .catch { cause ->
            handleError(cause)
            emit(emptySet())
        }

    private fun filterChoreItems(
        allChoreItems: List<ChoreItem>,
        workflow: Workflow?,
        selectedRooms: List<Room>,
        isFavoriteSelected: Boolean,
        parentChores : List<Chore>): Set<ChoreItem>
    {
        val filteredChoreItems: MutableSet<ChoreItem> = mutableSetOf()
        if (workflow != null) {
            val choreItems = allChoreItems.filter { it.parentWorkflowId == workflow.id }.toList()
            for (choreItem in choreItems) {
                val chore = parentChores.find { chore -> chore.id == choreItem.parentChoreId }
                if (selectedRooms.isEmpty() && !isFavoriteSelected) {
                    // Case: No rooms selected and not filtering by favorites
                    filteredChoreItems.add(choreItem)
                } else if (chore != null) {
                    if (selectedRooms.any { selected ->
                            chore.rooms.any { room -> selected.id == room }
                        } || (isFavoriteSelected && chore.isFavorite)) {
                        // Case: Either rooms match the selected rooms or filtering by favorites and chore is a favorite
                        filteredChoreItems.add(choreItem)
                    }
                }
            }
            return filteredChoreItems
        }
        return emptySet()
    }


    val uiState: StateFlow<DashboardUiState> = combine(_choreItems, _userMessage, _parentChores, _rooms, _filteredChoreItems) {
            choreItems, userMessage, parentChores, rooms, filteredChoreItems ->
        DashboardUiState(
            choreItems = choreItems,
            userMessage = userMessage,
            parentChores = parentChores,
            rooms = rooms,
            filteredChoreItems = filteredChoreItems,
            userName = _userName.value,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = DashboardUiState()
        )

    init {
        viewModelScope.launch {
            val user = choreRepository.getUser("scodd_user")
            if(user != null){
                _userName.value = user.name
                val instant = Instant.ofEpochMilli(user.lastUpdated)
                val lastUpdatedDate = LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate()

                val todayDate =  LocalDateTime.now(ZoneOffset.UTC).toLocalDate()
                Timber.tag("time").d(lastUpdatedDate.toString())
                Timber.tag("time").d(todayDate.toString())

                if (lastUpdatedDate != todayDate){
                    getRoundUp()
                    choreRepository.updateUser("scodd_user",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                }
            }
        }
    }

    private fun getRoundUp(){
        val todayDate = LocalDateTime.now(ZoneOffset.UTC).toLocalDate()
//        Timber.tag("curious").d("getRoundUp(): $todayDate")
        val roundup: MutableSet<Chore> = mutableSetOf()

        val parentChores: MutableList<Chore> = mutableListOf()
        viewModelScope.launch {
            choreRepository.getChores().forEach {chore ->
                parentChores.add(chore)
            }
            parentChores.forEach { parentChore ->
                var addToRoundUp = false
                val routineInfo = parentChore.routineInfo
                val choreDate = routineInfo.date
                if(choreDate != null){
                    //Chores Whose Date/Start Date matches today's date
                    val instant = Instant.ofEpochMilli(choreDate)
                    val date = LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate()

                    if(todayDate == date){
                        addToRoundUp = true
//                       roundup.add(parentChore)
                    }

                    //Chores with Routines
                    if(!routineInfo.isOneTime){
                        when(routineInfo.scheduleType){
                            ScoddTime.DAILY -> {
                                addToRoundUp = true
//                               roundup.add(parentChore)
                            }
                            ScoddTime.WEEKLY -> {
                                if(routineInfo.weeklyDay == todayDate.dayOfWeek){
                                    addToRoundUp = true
//                                   roundup.add(parentChore)
                                }
                            }
                            ScoddTime.MONTHLY -> {
                                if(date.dayOfMonth == todayDate.dayOfMonth){
                                    addToRoundUp = true
//                                   roundup.add(parentChore)
                                }
                            }
                            ScoddTime.YEARLY -> {
                                if(date.dayOfYear == todayDate.dayOfYear){
                                    addToRoundUp = true
//                                   roundup.add(parentChore)
                                }
                            }
                            ScoddTime.CUSTOM -> {
                                when(routineInfo.frequencyOption){
                                    ScoddTime.DAY -> {
                                        if((todayDate.toEpochDay() - date.toEpochDay()).mod(routineInfo.frequencyValue) == 0){
                                           addToRoundUp = true
//                                          roundup.add(parentChore)
                                        }
                                    }
                                    ScoddTime.WEEK -> {
                                        if(routineInfo.weeklyDay == todayDate.dayOfWeek){
                                            if((todayDate.toEpochDay() - date.toEpochDay()).mod(routineInfo.frequencyValue * 7) == 0){
                                                addToRoundUp = true
//                                                roundup.add(parentChore)
                                            }
                                        }
                                    }
                                    ScoddTime.MONTH -> {
                                        if (todayDate.dayOfMonth == date.dayOfMonth) {
                                            val monthsBetween: Long = ChronoUnit.MONTHS.between(date, todayDate)
                                            if (monthsBetween % routineInfo.frequencyValue == 0L) {
                                                addToRoundUp = true
//                                                roundup.add(parentChore)
                                            }
                                        }
                                    }
                                    ScoddTime.YEAR -> {
                                        val yearsBetween: Long = ChronoUnit.YEARS.between(date,todayDate)
                                        val isLeapYearToday = todayDate.isLeapYear
                                        val isLeapYearChore = date.isLeapYear

                                        val daysOfYearToday = if (isLeapYearToday) todayDate.dayOfYear - 1 else todayDate.dayOfYear
                                        val daysOfYearChore = if (isLeapYearChore) date.dayOfYear - 1 else date.dayOfYear

                                        if (daysOfYearToday == daysOfYearChore) {
                                            if (yearsBetween % routineInfo.frequencyValue == 0L) {
                                                addToRoundUp = true
//                                                roundup.add(parentChore)
                                            }
                                        }
                                    }
                                    else -> {}
                                }
                            }
                            else -> {}
                        }
                    }
                }

                if(addToRoundUp){
                    roundup.add(parentChore)
                }
            }

            roundup.forEach {
                viewModelScope.launch {
                    choreRepository.createChoreItem(it.id, workflowId)
                }
            }
        }

    }

    fun getEarliestTime() : String{
        val roundup: MutableSet<Chore> = mutableSetOf()
        Timber.tag("try").d(uiState.value.parentChores.toString())

        uiState.value.filteredChoreItems.forEach { choreItem ->
            val roundupChore = uiState.value.parentChores.find { parentChore -> parentChore.id == choreItem.parentChoreId }
            if(roundupChore != null){
                roundup.add(roundupChore)
            }
        }

        if(roundup.isNotEmpty() && uiState.value.filteredChoreItems.isNotEmpty()){
            val earliestChore: Chore?

            val lowestHour = roundup.map { it.routineInfo.hour }.withIndex().minBy { it.value }

            earliestChore = if(roundup.map { it.routineInfo.hour }.count { it == lowestHour.value } > 1){
                val lowestMinutes = roundup.filter { it.routineInfo.hour == lowestHour.value }.withIndex().minBy { it.value.routineInfo.minute }
                lowestMinutes.value
            }else{
                roundup.elementAt(lowestHour.index)
            }
            Timber.tag("try").d(earliestChore.title)

            return LocalTime.of(earliestChore.routineInfo.hour, earliestChore.routineInfo.minute)
                .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)).toString()
        }

        return ""
    }

    fun toggleRoom(room : Room){
        val currentRooms = myRooms.value.toMutableList()
        if (myRooms.value.map{it.id}.contains(room.id)) {
            currentRooms.remove(currentRooms.find{ it.id == room.id })
        } else {
            currentRooms.add(room)
        }
        _myRooms.value = currentRooms
    }
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

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackbarMessage(message: Int) {
        _userMessage.value = message
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

    fun setCompleted(choreItemId: String, completed: Boolean) = viewModelScope.launch {
        val choreItem = uiState.value.filteredChoreItems.find { it.id == choreItemId }?: return@launch
        if (completed) {
            choreRepository.completeChoreItem(choreItem.id)
            showSnackbarMessage(R.string.chore_marked_complete)
        } else {
            choreRepository.activateChoreItem(choreItem.id)
        }
    }

    fun getRoomTitle(parentChoreId: String, index : Int): String {
        val rooms = uiState.value.parentChores.find { it.id == parentChoreId }?.rooms
        if(rooms != null){
            val roomText: String = if(rooms.isNotEmpty()){
                val allRooms = uiState.value.rooms
                val room = allRooms.find{ it.id == rooms[index]}
                room?.title ?: " "
            }else{
                ""
            }
            return roomText
        }
        return ""
    }

    fun getChoreTitle(parentChoreId: String): String {
        return uiState.value.parentChores.find { it.id == parentChoreId }?.title?: ""
    }

    fun getAdditionalAmount(parentChoreId: String): Int {
        return uiState.value.parentChores.find { it.id == parentChoreId }?.rooms?.count()?.minus(1) ?: 0
    }

    fun selectedItems(selectedItems: List<String>) = viewModelScope.launch {
        choreRepository.updateWorkflow(workflowId, selectedItems)
    }

    fun getTotalChoreItemAmount(): Int{
        return uiState.value.choreItems.count { it.parentWorkflowId == workflowId && !it.isComplete}
    }

    fun getDistinctRoomAmount(): Int{
        val choreItemsRoom: MutableSet<String> = mutableSetOf()
        val choreItems = uiState.value.choreItems.filter{ it.parentWorkflowId == workflowId }
        if (choreItems.isNotEmpty()) {
            for (choreItem in choreItems) {
                val chore = uiState.value.parentChores.find { chore -> chore.id == choreItem.parentChoreId }
                if (chore != null) {
                    choreItemsRoom.addAll(chore.rooms)
                }
            }
        }
        return choreItemsRoom.count()
    }

    fun clearCompleted() {
        val choreItems = uiState.value.filteredChoreItems.filter { it.isComplete }
        choreItems.forEach { choreItem ->
            viewModelScope.launch {
                choreRepository.deleteChoreItem(choreItem.id)
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            choreRepository.getChoreItems().let {
                it.forEach { choreItem ->
                    if(choreItem.parentWorkflowId == workflowId){
                        choreRepository.deleteChoreItem(choreItem.id)
                    }
                }
            }
        }
    }

    fun deleteItem(choreItemId: String) {
        viewModelScope.launch {
            choreRepository.deleteChoreItem(choreItemId)
        }
    }

}

// Used to save the current filtering in SavedStateHandle.
const val SELECTED_SAVED_STATE_KEY = "selected"
const val FAVORITE_SAVED_STATE_KEY = "favorites"


