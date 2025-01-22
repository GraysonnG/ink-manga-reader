package com.blanktheevil.inkmangareader.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _counter = MutableStateFlow(0)
    val counter = _counter.asStateFlow()

    init {
        viewModelScope.launch {
            flow {
                while (true) {
                    delay(1000)
                    emit(Unit)
                }
            }.collect {
                _counter.value += 1
            }
        }
    }
}