package com.blanktheevil.inkmangareader.ui.pages

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.isValid
import com.blanktheevil.inkmangareader.viewmodels.BaseViewModel
import com.blanktheevil.inkmangareader.viewmodels.BaseViewModelState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun <reified VM: BaseViewModel<UIState, Params>, UIState: BaseViewModelState, Params> BasePage(
    viewModelParams: Params? = null,
    noinline content: @Composable BasePageScope.(viewModel: VM, uiState: UIState, authenticated: Boolean) -> Unit,
) {
    val sessionManager = koinInject<SessionManager>()
    val session by sessionManager.session.collectAsState()
    val isSessionValid by remember { derivedStateOf { session.isValid() } }
    val viewModel = koinViewModel<VM>()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initViewModel(false, viewModelParams)
    }

    LaunchedEffect(isSessionValid) {
        if (!isSessionValid) sessionManager.refresh()
    }

    PullToRefreshBox(
        isRefreshing = state.loading,
        onRefresh = {
            viewModel.initViewModel(true, viewModelParams)
        }
    ) {
        BasePageScopeInstance.content(viewModel, state, isSessionValid)
    }
}

interface BasePageScope

internal object BasePageScopeInstance : BasePageScope
