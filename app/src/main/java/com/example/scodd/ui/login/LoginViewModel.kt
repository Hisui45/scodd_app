package com.example.scodd.ui.login

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.R
import com.example.scodd.data.ChoreRepository
import com.example.scodd.model.*
import com.example.scodd.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

data class LoginUiState(
    val isLoggedIn: Boolean = false,
    val workflows: List<Workflow> = listOf(),

    )



@HiltViewModel
class LoginViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
//    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val _workflows = choreRepository.getWorkflowsStream()
        .catch { cause ->
            handleError(cause)
            emit(emptyList())
        }



    val uiState: StateFlow<LoginUiState> = combine( _userMessage, _workflows) { userMessage, workflows ->
        LoginUiState(
            workflows = workflows
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = LoginUiState()
        )

    private fun handleError(cause: Throwable) {
        // Handle the error, for example, log it or emit a default value
        Timber.e(cause, "Error loading data")
        _userMessage.value = R.string.error_chores
        // Additional error handling logic if needed
    }

}