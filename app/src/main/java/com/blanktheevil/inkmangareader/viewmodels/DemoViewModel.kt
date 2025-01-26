package com.blanktheevil.inkmangareader.viewmodels

import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.data.Session
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaListEither
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DemoViewModel(
    private val mangaRepository: MangaRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel<DemoViewModel.DemoState, DemoViewModel.DemoParams>(DemoState()) {
    override fun initViewModel(hardRefresh: Boolean, params: DemoParams?) = viewModelScope.launch {
        updateState { copy(loading = true) }
        combine(
            sessionManager.session,
            mangaRepository.getList(MangaListRequest.Seasonal, hardRefresh = hardRefresh),
            mangaRepository.getList(MangaListRequest.Popular, hardRefresh = hardRefresh),
            mangaRepository.getList(MangaListRequest.Recent, hardRefresh = hardRefresh),
            ::HomeFlowData
        ).collect {
            it.onError { sE, pE, rE ->
                updateState { copy(errors = listOfNotNull(sE, pE, rE)) }
            }

            updateState {
                copy(
                    loading = false,
                    seasonalList = it.seasonal.successOrNull(),
                    popularList = it.popular.successOrNull(),
                    recentList = it.recent.successOrNull(),
                )
            }
        }
    }

    private data class HomeFlowData(
        val session: Session?,
        val seasonal: MangaListEither,
        val popular: MangaListEither,
        val recent: MangaListEither,
    ) {
        fun onError(
            callback: (seasonalError: Throwable?, popularError: Throwable?, recentError: Throwable?) -> Unit
        ) {
            callback(
                seasonal.errorOrNull(),
                popular.errorOrNull(),
                recent.errorOrNull(),
            )
        }
    }

    data class DemoState(
        override val errors: List<Any> = emptyList(),
        override val loading: Boolean = false,
        val seasonalList: MangaList? = null,
        val popularList: MangaList? = null,
        val recentList: MangaList? = null,

        ) : BaseViewModelState()

    data class DemoParams(
        val userLoggedIn: Boolean = false
    )
}