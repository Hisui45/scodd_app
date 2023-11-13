package com.example.scodd.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.Chore
import com.example.scodd.utils.Async
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.scodd.utils.WhileUiSubscribed
import kotlinx.coroutines.flow.*

//data class DashboardUiState(
//    val items: List<Chore> = emptyList(),
//    val isLoading: Boolean = false,
//    val filteringUiInfo: FilteringUiInfo = FilteringUiInfo(),
//    val userMessage: Int? = null
//)
//
//@HiltViewModel
//class DashboardViewModel @Inject constructor(
//    private val taskRepository: ChoreRepository,
//    private val savedStateHandle: SavedStateHandle
//) : ViewModel() {
//
//    private val _savedFilterType =
//        savedStateHandle.getStateFlow(TASKS_FILTER_SAVED_STATE_KEY, ALL_TASKS)
//
//    private val _filterUiInfo = _savedFilterType.map { getFilterUiInfo(it) }.distinctUntilChanged()
//    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
//    private val _isLoading = MutableStateFlow(false)
//
//    private val _filteredTasksAsync =
//        combine(taskRepository.getChoresStream(), _savedFilterType) { chores, type ->
//            filterTasks(chores, type)
//        }
//            .map { Async.Success(it) }
//            .catch<Async<List<Chore>>> { emit(Async.Error(R.string.loading_tasks_error)) }
//
//    val uiState: StateFlow<DashboardUiState> = combine(
//        _filterUiInfo, _isLoading, _userMessage, _filteredTasksAsync
//    ) { filterUiInfo, isLoading, userMessage, choresAsync ->
//        when (choresAsync) {
//            Async.Loading -> {
//                DashboardUiState(isLoading = true)
//            }
//            is Async.Error -> {
//                DashboardUiState(userMessage = choresAsync.errorMessage)
//            }
//            is Async.Success -> {
//                DashboardUiState(
//                    items = choresAsync.data,
//                    filteringUiInfo = filterUiInfo,
//                    isLoading = isLoading,
//                    userMessage = userMessage
//                )
//            }
//        }
//    }
//        .stateIn(
//            scope = viewModelScope,
//            started = WhileUiSubscribed,
//            initialValue = DashboardUiState(isLoading = true)
//        )
//
////    fun setFiltering(requestType: TasksFilterType) {
////        savedStateHandle[TASKS_FILTER_SAVED_STATE_KEY] = requestType
////    }
//
////    fun clearCompletedTasks() {
////        viewModelScope.launch {
////            taskRepository.clearCompletedChores()
////            showSnackbarMessage(R.string.completed_chores_cleared)
////            refresh()
////        }
////    }
//
////    fun completeTask(task: Chore, completed: Boolean) = viewModelScope.launch {
////        if (completed) {
////            taskRepository.completeChore(task.id)
////            showSnackbarMessage(R.string.chore_marked_complete)
////        }
//////        else {
//////            taskRepository.activateTask(task.id)
//////            showSnackbarMessage(R.string.task_marked_active)
//////        }
////    }
//
////    fun showEditResultMessage(result: Int) {
////        when (result) {
////            EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_saved_task_message)
////            ADD_EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_added_task_message)
////            DELETE_RESULT_OK -> showSnackbarMessage(R.string.successfully_deleted_task_message)
////        }
////    }
//
//    fun snackbarMessageShown() {
//        _userMessage.value = null
//    }
//
//    private fun showSnackbarMessage(message: Int) {
//        _userMessage.value = message
//    }
//
//    fun refresh() {
//        _isLoading.value = true
//        viewModelScope.launch {
//            taskRepository.refresh()
//            _isLoading.value = false
//        }
//    }
//
//    private fun filterTasks(tasks: List<Chore>, filteringType: TasksFilterType): List<Chore> {
//        val tasksToShow = ArrayList<Chore>()
//        // We filter the tasks based on the requestType
//        for (task in tasks) {
//            when (filteringType) {
//                ALL_TASKS -> tasksToShow.add(task)
//                ACTIVE_TASKS -> if (task.isCompleted) {
//                    tasksToShow.add(task)
//                }
//                COMPLETED_TASKS -> if (task.isCompleted) {
//                    tasksToShow.add(task)
//                }
//            }
//        }
//        return tasksToShow
//    }
//
//    private fun getFilterUiInfo(requestType: TasksFilterType): FilteringUiInfo =
//        when (requestType) {
//            ALL_TASKS -> {
//                FilteringUiInfo(
//                    R.string.label_all, R.string.no_tasks_all,
//                    R.drawable.rooster
//                )
//            }
//            ACTIVE_TASKS -> {
//                FilteringUiInfo(
//                    R.string.label_active, R.string.no_tasks_active,
//                    R.drawable.rooster
//                )
//            }
//            COMPLETED_TASKS -> {
//                FilteringUiInfo(
//                    R.string.label_completed, R.string.no_tasks_completed,
//                    R.drawable.rooster
//                )
//            }
//        }
//}
//
//// Used to save the current filtering in SavedStateHandle.
//const val TASKS_FILTER_SAVED_STATE_KEY = "TASKS_FILTER_SAVED_STATE_KEY"
//
//data class FilteringUiInfo(
//    val currentFilteringLabel: Int = R.string.label_all,
//    val noTasksLabel: Int = R.string.no_tasks_all,
//    val noTaskIconRes: Int = R.drawable.rooster,
//)
