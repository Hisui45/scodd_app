package com.example.scodd.ui.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

data class LoginUiState(
    val isLoggedIn: Boolean = false,
    val user: User? = null
)


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        runBlocking {
            val user = choreRepository.getUser("scodd_user")
            if(user != null){
                _uiState.update {
                    it.copy(user = user, isLoggedIn = true)
                }
            }
        }
    }


    fun saveName(name: String) {
        viewModelScope.launch {
            choreRepository.createUser(name, 0, lastUpdated = 0L)

        }
        _uiState.update {
            it.copy(isLoggedIn = true)
        }
    }

}