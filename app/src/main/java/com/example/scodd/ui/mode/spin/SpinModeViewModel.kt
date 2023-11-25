package com.example.scodd.ui.mode.spin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.Chore
import com.example.scodd.model.ChoreItem
import com.example.scodd.model.ScoddModes
import com.example.scodd.model.Workflow
import com.example.scodd.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SpinUiState(
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
    var userMessage: Int? = null,
    val choreItemError: String? = null

    )
/**
 * ViewModel for the Details screen.
 */
@HiltViewModel
class SpinModeViewModel @Inject constructor(
    private val choreRepository: ChoreRepository
) : ViewModel() {

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

    private fun removeEmptyWorkflows(workflows: List<Workflow>): List<Workflow>{
        /**
         * TODO: REMOVE  empty workflows
         */
        return workflows
    }

    val uiState: StateFlow<SpinUiState> = combine(
         _parentChores, _choresFromWorkflow, _selectedChores, _choreItems, _userMessage
    ){parentChores, choresFromWorkflow,selectedChores, choreItems, userMessage  ->
            SpinUiState(
                parentWorkflows = _parentWorkflows.value,
                choreItems = choreItems,
                parentChores = parentChores,
                choresFromWorkflow = choresFromWorkflow,
                selectedChores = selectedChores,
                userMessage = userMessage,
                choreItemError = _choreItemError.value
            )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = SpinUiState()
    )


//    private val _isLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            loadWorkflows()
            loadMode()
        }
    }

    private fun handleChoreStreamError() {
        _userMessage.value = R.string.loading_chores_error
    }

    fun isWorkflowSelected(workflowId: String): Boolean {
        return _selectedWorkflows.value.any{it == workflowId}
    }

    private fun updateSpinModeWorkflow() {
        viewModelScope.launch {
            choreRepository.updateModeWorkflows(
                modeId = ScoddModes.SPIN_MODE,
                selectedWorkflows = _selectedWorkflows.value,
                workflowChores = _choresFromWorkflow.value
            )
        }
    }

    private fun updateSpinModeWorkflowChores() {
        viewModelScope.launch {
            choreRepository.updateModeWorkflowChores(
                modeId = ScoddModes.SPIN_MODE,
                workflowChores = _choresFromWorkflow.value
            )
        }
    }
    private fun updateSpinModeChores() {
        viewModelScope.launch {
            choreRepository.updateModeChores(
                modeId = ScoddModes.SPIN_MODE,
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
        updateSpinModeWorkflow()
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
        _userMessage.value = null
        _choreItemError.value = null
    }

    fun showItemErrorMessage(message: Int, choreId: String){
        _choreItemError.value = choreId
        _userMessage.value = message
    }

    fun removeDuplicateItem(choreItem: String) {
        val choresFromWorkflow = _choresFromWorkflow.value.toMutableList()
        var found = false
        choresFromWorkflow.removeIf {
            if (!found && it == choreItem) {
                found = true
                true
            } else {
                false
            }
        }
        _choresFromWorkflow.value = choresFromWorkflow
        updateSpinModeWorkflowChores()
    }

    fun selectedItems(selectedItems: List<String>) {
        val selectedChores = uiState.value.selectedChores.toMutableSet()
        selectedChores.clear()
        selectedChores.addAll(selectedItems)
        _selectedChores.value = selectedChores.toList()
        updateSpinModeChores()
    }
    private suspend fun loadWorkflows(){
        choreRepository.getWorkflows().let { workflows ->
            _parentWorkflows.value = workflows

        }
    }

    private suspend fun loadMode() {
        choreRepository.getMode(ScoddModes.SPIN_MODE).let {
                mode ->
            if (mode != null) {
                _selectedWorkflows.value = mode.selectedWorkflows
                _choresFromWorkflow.value = mode.workflowChores
                _selectedChores.value = mode.chores
            }
        }
    }

}

