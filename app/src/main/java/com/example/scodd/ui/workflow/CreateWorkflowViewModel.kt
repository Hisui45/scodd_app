package com.example.scodd.ui.workflow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.Chore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class CreateWorkflowUiState(
    val title: String = "",
    val selectedChores: List<String> = emptyList(),
    val parentChores: List<Chore> = emptyList(),
    val allChores: List<Chore> = emptyList(),
    val isWorkflowSaved: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
)
@HiltViewModel
class CreateWorkflowViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // A MutableStateFlow needs to be created in this ViewModel. The source of truth of the current
    // editable Chore is the ViewModel, we need to mutate the UI state directly in methods such as
    // `updateTitle` or `updateDescription`
    private val _uiState = MutableStateFlow(CreateWorkflowUiState())
    val uiState: StateFlow<CreateWorkflowUiState> = _uiState.asStateFlow()

    init {
        loadChores()
    }
    private fun createNewWorkflow() = viewModelScope.launch {
        choreRepository.createWorkflow(
            uiState.value.title,
            uiState.value.parentChores.map { chore -> chore.id }
        )
        _uiState.update {
            it.copy(isWorkflowSaved = true)
        }
    }
    fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(title = newTitle)
        }
    }
    fun saveWorkflow() {
        if (uiState.value.title.isEmpty()) {
            _uiState.update {
                it.copy(userMessage = R.string.workflow_no_title_message)
            }
            return
        }

        createNewWorkflow()
    }

    fun snackbarMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }
    fun selectedItems(selectedItems: List<String>){
        val choresToShow = ArrayList<Chore>()
        selectedItems.forEach { selectedItemId ->
            val chore = _uiState.value.allChores.find{ it.id == selectedItemId}
            if (chore != null) {
                choresToShow.add(chore)
            }
        }
        choresToShow.forEach{chore ->
            if(!selectedItems.any { selectedItemId -> selectedItemId == chore.id }){
                choresToShow.remove(chore)
            }
        }
        _uiState.update {
            it.copy(selectedChores = selectedItems, parentChores = choresToShow)
        }
    }

    fun deleteChore(chore: Chore) {
        val chores = uiState.value.parentChores.toMutableSet()
        if (chore in chores) {
            chores.remove(chore)
        }
        _uiState.update { it.copy(parentChores = chores.toList(), selectedChores = chores.map { chore -> chore.id })}
    }

    private fun loadChores() {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            choreRepository.getChores().let { chores ->
                _uiState.update {
                    it.copy(
                        allChores = chores,
                        isLoading = false
                    )
                }
            }
        }
    }

}
