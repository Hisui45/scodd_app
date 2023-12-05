package com.example.scodd.ui.mode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scodd.data.ChoreRepository
import com.example.scodd.utils.WhileUiSubscribed
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*

data class ModeUiState(
    val bankAmount: Int = 0
)
@HiltViewModel
class ModeViewModel @Inject constructor(
    private val choreRepository: ChoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModeUiState())

    val user = choreRepository.getUserStream("scodd_user")

    val uiState: StateFlow<ModeUiState> = user.map { user ->
        if(user != null){
            ModeUiState(bankAmount = user.bankModeValue)
        }else{
            ModeUiState()
        }
    }
        .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = ModeUiState()
    )

    init {
        viewModelScope.launch {
            val user = choreRepository.getUser("scodd_user")
            if(user != null){
                _uiState.update {
                    it.copy(bankAmount = user.bankModeValue)
                }
            }
        }
    }
}

