package com.example.myapplication2525


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class FactUiState {
    object Loading : FactUiState()
    data class Success(val factText: String) : FactUiState()

}

class FactsViewModel(
    private val repository: FactRepository = FactRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<FactUiState>(FactUiState.Success("Нажмите кнопку, чтобы узнать факт!"))
    val uiState: StateFlow<FactUiState> = _uiState.asStateFlow()

    fun loadNewFact() {
        viewModelScope.launch {
            repository.getRandomFact()
                .onStart { _uiState.value = FactUiState.Loading }
                .catch { e ->
                    // Обработка ошибки: показать сообщение
                    _uiState.value = FactUiState.Success("Ошибка загрузки: ${e.message}")
                }
                .collect { factText ->
                    _uiState.value = FactUiState.Success(factText)
                }
        }
    }
}