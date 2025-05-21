package com.ozan.kotlinaiwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Tüm ViewModel'ler için temel sınıf
 */
abstract class BaseViewModel<State, Event> : ViewModel() {
    
    protected val _uiState = MutableStateFlow<State?>(null)
    val uiState: StateFlow<State?> = _uiState
    
    protected val _eventFlow = MutableStateFlow<Event?>(null)
    val eventFlow: StateFlow<Event?> = _eventFlow
    
    protected fun <T> execute(
        operation: suspend () -> T,
        onSuccess: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val result = operation()
                onSuccess(result)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
