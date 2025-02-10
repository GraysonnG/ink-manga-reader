package com.blanktheevil.inkmangareader.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<VMState : BaseViewModelState, VMParams>(
    state: VMState,
) : ViewModel() {
    protected val _uiState = MutableStateFlow(state)
    val uiState = _uiState.asStateFlow()

    abstract fun initViewModel(hardRefresh: Boolean, params: VMParams? = null): Job

    protected suspend fun doAsyncJobs(vararg jobs: suspend () -> Any) = coroutineScope {
        val deferred = jobs.map { this.async { it() } }
        deferred.awaitAll()
        Unit
    }

    fun updateState(update: VMState.() -> VMState) {
        val updatedValue = update(_uiState.value)
        _uiState.value = updatedValue
    }
}

abstract class BaseViewModelState {
    abstract val errors: List<Any>
    abstract val loading: Boolean
}