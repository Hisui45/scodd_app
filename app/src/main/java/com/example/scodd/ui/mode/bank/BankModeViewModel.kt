package com.example.scodd.ui.mode.bank

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

data class BankUiState(
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
class BankModeViewModel @Inject constructor(
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

    //    private val _uiState = MutableStateFlow(BankUiState())
    val uiState: StateFlow<BankUiState> = combine(
        _parentChores, _choresFromWorkflow, _selectedChores, _choreItems, _userMessage
    ){parentChores, choresFromWorkflow,selectedChores, choreItems, userMessage  ->
        BankUiState(
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
        initialValue = BankUiState()
    )


//    private val _isLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            loadMode()
            loadWorkflows()
        }
    }

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

    private fun handleChoreStreamError() {
        _userMessage.value = R.string.loading_chores_error
    }

    fun isWorkflowSelected(workflowId: String): Boolean {
        return _selectedWorkflows.value.any{it == workflowId}
    }

    private fun updateBankModeWorkflow() {
        viewModelScope.launch {
            choreRepository.updateModeWorkflows(
                modeId = ScoddModes.BANK_MODE,
                selectedWorkflows = _selectedWorkflows.value,
                workflowChores = _choresFromWorkflow.value
            )
        }
    }

    private fun updateBankModeWorkflowChores() {
        viewModelScope.launch {
            choreRepository.updateModeWorkflowChores(
                modeId = ScoddModes.BANK_MODE,
                workflowChores = _choresFromWorkflow.value
            )
        }
    }
    private fun updateBankModeChores() {
        viewModelScope.launch {
            choreRepository.updateModeChores(
                modeId = ScoddModes.BANK_MODE,
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
        updateBankModeWorkflow()
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
        updateBankModeChores()
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
        updateBankModeWorkflowChores()
    }

    private suspend fun loadWorkflows(){
        choreRepository.getWorkflows().let { workflows ->
            _parentWorkflows.value = workflows

        }
    }

    private suspend fun loadMode() {
        choreRepository.getMode(ScoddModes.BANK_MODE).let {
            mode ->
            if (mode != null) {
                _selectedWorkflows.value = mode.selectedWorkflows
                _choresFromWorkflow.value = mode.workflowChores
                _selectedChores.value = mode.chores
            }
        }
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
                convertToMinutes(value,unit)
            }else{
                convertToMinutes(0, ScoddTime.MINUTE)
            }
        }

        val potentialPayoutFromSelectedChores = selectedChores.sumOf { parentChoreId ->
            if(parentChores.find { it.id == parentChoreId }?.isTimeModeActive == true){
                val value = parentChores.find { it.id == parentChoreId }?.timerModeValue ?: 0
                val unit = parentChores.find { it.id == parentChoreId }?.timerOption ?: ScoddTime.MINUTE
                convertToMinutes(value,unit)
            }else{
                convertToMinutes(0, ScoddTime.MINUTE)
            }
        }

        val totalTimeInMinutes = potentialPayoutFromWorkflow + potentialPayoutFromSelectedChores

        if (totalTimeInMinutes < 0) {
            return "?"  // Handle negative input as needed
        }

        val hours = totalTimeInMinutes / 60
        val remainingMinutes = totalTimeInMinutes % 60

        return when {
            hours > 0 && remainingMinutes > 0 -> "$hours hours and $remainingMinutes minutes"
            hours > 0 -> "$hours hours"
            remainingMinutes > 0 -> "$remainingMinutes minutes"
            else -> "?"
        }

    }

    private fun convertToMinutes(value: Int, unit: ScoddTime): Int{
        return when(unit){
            ScoddTime.SECOND->{
                value / 60
            }

            ScoddTime.MINUTE->{
                value
            }

            ScoddTime.HOUR->{
                value * 60
            }

            else -> {
                0
            }
        }
    }
}

