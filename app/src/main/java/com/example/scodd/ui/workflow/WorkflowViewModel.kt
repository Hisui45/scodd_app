package com.example.scodd.ui.workflow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.*
import com.example.scodd.utils.WhileUiSubscribed
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*

data class WorkflowUiState(
    val title: String = "",
    val workflow: Workflow? = null,
    val choreItems: List<ChoreItem> = emptyList(),
    val parentChores: List<Chore> = emptyList(),
    val rooms: List<Room> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isWorkflowDeleted: Boolean = false,
    val isChoreItemDeleted: Boolean = false
    
)
/**
 * ViewModel for the Details screen.
 */
@HiltViewModel
class WorkflowViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workflowId: String = savedStateHandle["workflowId"]!!

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isWorkflowDeleted = MutableStateFlow(false)

    private val _workflow = choreRepository.getWorkflowStream(workflowId)
        .catch {
            handleChoreStreamError()
            emit(null)
        }
    private val _rooms = choreRepository.getRoomsStream()
        .catch {
            handleChoreStreamError()
            emit(emptyList())
        }
    private val _parentChores = choreRepository.getChoresStream()
        .catch {
            handleChoreStreamError()
            emit(emptyList())
        }
    private val _choreItems = combine(
        choreRepository.getChoreItemsStream(),
        choreRepository.getWorkflowStream(workflowId)
    ) { choreItems, workflow ->
        handleChoreItems(choreItems, workflow)
    }
        .catch {
            handleChoreStreamError()
            emit(emptyList())
        }
    private fun handleChoreStreamError() {
        _userMessage.value = R.string.loading_chores_error
    }
    private fun handleChoreItems(allChoreItems: List<ChoreItem>, workflow: Workflow?): List<ChoreItem> {
        if (workflow != null) {
            return allChoreItems.filter { it.parentWorkflowId == workflow.id }.toList()
        }
        return emptyList()
    }


    val uiState: StateFlow<WorkflowUiState> = combine(
        _userMessage, _workflow, _choreItems, _rooms, _parentChores
    ) { userMessage, workflow, choreItems, rooms, parentChores ->
        combine(_isWorkflowDeleted) { isWorkflowDeleted ->
            WorkflowUiState(
                userMessage = userMessage,
                workflow = workflow,
                choreItems = choreItems,
                rooms = rooms,
                parentChores = parentChores,
                isWorkflowDeleted = isWorkflowDeleted[0]
            )
        }.first()
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = WorkflowUiState(isLoading = true)
    )


    fun deleteWorkflow() = viewModelScope.launch {
        choreRepository.deleteWorkflow(workflowId)
        _isWorkflowDeleted.value = true
    }
    fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
            choreRepository.refreshWorkflow(workflowId)
            _isLoading.value = false
        }
    }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackbarMessage(message: Int) {
        _userMessage.value = message
    }

    fun setCompleted(choreItemId: String, completed: Boolean) = viewModelScope.launch {
        val choreItem = uiState.value.choreItems.find { it.id == choreItemId }?: return@launch
        if (completed) {
            choreRepository.completeChoreItem(choreItem.id)
            showSnackbarMessage(R.string.chore_marked_complete)
        } else {
            choreRepository.activateChoreItem(choreItem.id)
        }
    }

    fun resetWorkflow() = viewModelScope.launch {
        uiState.value.choreItems.forEach { choreItem ->
            if (choreItem.isComplete) {
                choreRepository.activateChoreItem(choreItem.id)
            }
        }
    }


        fun toggleListType() = viewModelScope.launch {
            val workflow = uiState.value.workflow
            if (workflow != null) {
                if (workflow.isCheckList) {
                    choreRepository.switchChoreList(workflow.id)
                } else {
                    choreRepository.switchCheckList(workflow.id)
                }
            }
        }

        fun getRoomTitle(parentChoreId: String, index: Int): String {
            val rooms = uiState.value.parentChores.find { it.id == parentChoreId }?.rooms
            if (rooms != null) {
                var roomText: String = if (rooms.isNotEmpty()) {
                    val allRooms = uiState.value.rooms
                    val room = allRooms.find { it.id == rooms[index] }
                    room?.title ?: " "
                } else {
                    ""
                }
                return roomText
            }
            return ""
        }

        fun getChoreTitle(parentChoreId: String): String {
            return uiState.value.parentChores.find { it.id == parentChoreId }?.title ?: ""
        }

        fun getAdditionalAmount(parentChoreId: String): Int {
            return uiState.value.parentChores.find { it.id == parentChoreId }?.rooms?.count()?.minus(1) ?: 0
        }

        fun selectedItems(selectedItems: List<String>) = viewModelScope.launch {
            choreRepository.updateWorkflow(workflowId, selectedItems)
        }

        fun updateTitle(title: String) {
            viewModelScope.launch {
                choreRepository.updateTitle(workflowId, title)
            }
        }

        fun addToRoundUp() {
            uiState.value.choreItems.forEach { choreItem ->
                viewModelScope.launch {
                    choreRepository.createChoreItem(choreItem.parentChoreId, ROUNDUP)
                    _userMessage.value = R.string.added_roundup_workflow
                }
            }
        }

    fun deleteItem(choreItemId: String) {
        viewModelScope.launch {
            choreRepository.deleteChoreItem(choreItemId)
        }
    }

}
