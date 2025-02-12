package com.blanktheevil.inkmangareader.ui.sheets.login

import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.viewmodels.BaseViewModel
import com.blanktheevil.inkmangareader.viewmodels.BaseViewModelState
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sessionManager: SessionManager,
) : BaseViewModel<LoginState, Nothing>(LoginState()) {
    override fun initViewModel(hardRefresh: Boolean, params: Nothing?) = viewModelScope.launch {
        updateState { copy(
            username = "",
            password = "",
            shouldDismiss = false,
        ) }
    }

    fun login() = viewModelScope.launch {
        sessionManager.login(
            username = _uiState.value.username,
            password = _uiState.value.password,
        ).onSuccess {
            updateState { copy(shouldDismiss = true) }
        }
    }

    fun updateUsername(username: String) {
        updateState { copy(
            username = username,
        ) }
    }

    fun updatePassword(password: String) {
        updateState { copy(
            password = password,
        ) }
    }
}

data class LoginState(
    override val loading: Boolean = false,
    override val errors: List<Any> = emptyList(),
    val username: String = "",
    val password: String = "",
    val shouldDismiss: Boolean = false,
) : BaseViewModelState()