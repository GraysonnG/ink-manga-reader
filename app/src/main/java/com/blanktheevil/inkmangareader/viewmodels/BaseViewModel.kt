package com.blanktheevil.inkmangareader.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T>(
    state: T,
) : ViewModel() {
    protected val _uiState = MutableStateFlow(state)
    val uiState = _uiState.asStateFlow()

    fun updateState(update: T.() -> T) {
        _uiState.value = update(_uiState.value)
    }
}