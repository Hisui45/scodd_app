package com.example.scodd.ui.mode.time

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.Chore
import com.example.scodd.model.ChoreItem
import com.example.scodd.model.ScoddTime
import com.example.scodd.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TimeUiState(
    val parentWorkflows: List<Workflow> = emptyList(),
    val parentChores: List<Chore> = emptyList(),
    val choreItems: List<ChoreItem> = emptyList(),

    //Pre-load
    val selectedWorkflows: List<String> = emptyList(),
    //Individual Chores from Workflow
    val choresFromWorkflow: List<String> = emptyList(),
    //Pre-load
    val selectedChores: List<String> = emptyList(),

    val isLoading: Boolean = false,
    val userMessage: Int? = null,

    )
/**
 * ViewModel for the Details screen.
 */
@HiltViewModel
class TimeModeViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _uiState = MutableStateFlow(TimeUiState())
    val uiState: StateFlow<TimeUiState> = _uiState.asStateFlow()

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)

//    private val _isLoading = MutableStateFlow(false)


    init {
        viewModelScope.launch {
            loadWorkflows()
            loadChores()
            loadChoreItems()
        }
    }

    private fun handleChoreStreamError() {
        _userMessage.value = R.string.error_chores
    }

    fun isWorkflowSelected(workflowId: String): Boolean {
        return _uiState.value.selectedWorkflows.any{it == workflowId}
    }

    fun selectWorkflow(workflow: Workflow) {
        val selectedWorkflows = uiState.value.selectedWorkflows.toMutableSet()
        val workflowChores = uiState.value.choresFromWorkflow.toMutableSet()
        if (workflow.id in selectedWorkflows) {
            selectedWorkflows.remove(workflow.id)
            workflowChores.removeAll(workflowChores(workflow.id))
        } else {
            selectedWorkflows.add(workflow.id)
            workflowChores.addAll(workflowChores(workflow.id))
        }
        _uiState.update { it.copy(selectedWorkflows = selectedWorkflows.toList()) }
        _uiState.update { it.copy(choresFromWorkflow = workflowChores.toList()) }
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

    fun getChoreTitle(parentChoreId: String): String {
        return uiState.value.parentChores.find { it.id == parentChoreId }?.title?: ""
    }

    fun getChoreTimeModeValue(parentChoreId: String): Int {
        return uiState.value.parentChores.find { it.id == parentChoreId }?.timerModeValue?: 0
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

    fun calculateEstimatedTime(): Int {
        val parentChores = uiState.value.parentChores
        val selectedChores = uiState.value.selectedChores

        val potentialPayoutFromWorkflow = uiState.value.choresFromWorkflow.sumOf { parentChoreId ->
            parentChores.find { it.id == parentChoreId }?.timerModeValue ?: 0
        }

        val potentialPayoutFromSelectedChores = selectedChores.sumOf { parentChoreId ->
            parentChores.find { it.id == parentChoreId }?.timerModeValue ?: 0
        }

        return potentialPayoutFromWorkflow + potentialPayoutFromSelectedChores
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

    private fun showSnackbarMessage(message: Int) {
        _userMessage.value = message
    }

    fun selectedItems(selectedItems: List<String>) {
        val selectedChores = uiState.value.selectedChores.toMutableSet()
        selectedChores.addAll(selectedItems)
        _uiState.update { it.copy(selectedChores = selectedChores.toList()) }

    }
    private suspend fun loadWorkflows(){
        choreRepository.getWorkflows().let { workflows ->
            _uiState.update {
                it.copy(
                    parentWorkflows = workflows
                )
            }
        }
    }

    private suspend fun loadChoreItems(){
        choreRepository.getChoreItems().let { choreItems ->
            _uiState.update {
                it.copy(
                    choreItems = choreItems
                )
            }
        }
    }

    private suspend fun loadChores(){
        choreRepository.getChores().let { chores ->
            _uiState.update {
                it.copy(
                    parentChores = chores
                )
            }
        }
    }



}

