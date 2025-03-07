package com.blanktheevil.inkmangareader.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.error
import com.blanktheevil.inkmangareader.data.filterEitherSuccess
import com.blanktheevil.inkmangareader.data.id
import com.blanktheevil.inkmangareader.data.isInvalid
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.ChapterList
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.data.onEitherError
import com.blanktheevil.inkmangareader.data.onUniqueSession
import com.blanktheevil.inkmangareader.data.repositories.ChapterListRequest
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepository
import com.blanktheevil.inkmangareader.data.repositories.list.UserListRepository
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DemoViewModel(
    private val mangaRepository: MangaRepository,
    private val chapterRepository: ChapterRepository,
    private val userListRepository: UserListRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel<DemoViewModel.DemoState, DemoViewModel.DemoParams>(DemoState()) {

    override fun initViewModel(hardRefresh: Boolean, params: DemoParams?) = viewModelScope.launch {
        updateState {
            if (hardRefresh) {
                DemoState()
            } else {
                copy(
                    loading = true,
                )
            }
        }
        getSeasonal(hardRefresh = hardRefresh)
        getFollowedMangaUpdatesFeed(hardRefresh = hardRefresh)
        getOtherLists(hardRefresh = hardRefresh)
        getUserLists(hardRefresh = hardRefresh)
        _uiState.collect {
            val shouldBeLoading =
                it.seasonalLoading ||
                        it.popularLoading ||
                        it.recentLoading ||
                        it.chapterFeedLoading ||
                        it.userListsLoading

            updateState { copy(loading = shouldBeLoading) }
        }
    }

    private fun getSeasonal(hardRefresh: Boolean) = viewModelScope.launch(
        Dispatchers.IO
    ) {
        updateState { copy(seasonalLoading = true) }
        mangaRepository.getList(
            MangaListRequest.Seasonal,
            limit = 100,
            offset = 0,
            hardRefresh = hardRefresh,
        ).collect {
            it.onSuccess { list ->
                updateState { copy(
                    seasonalList = list,
                    seasonalLoading = false,
                ) }
            }
            it.onError {
                updateState { copy(seasonalLoading = false) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun getFollowedMangaUpdatesFeed(hardRefresh: Boolean) = viewModelScope.launch(
        Dispatchers.IO
    ) {
        updateState { copy(chapterFeedLoading = true) }
        sessionManager.session
            .onEach {
                if (it.isInvalid()) {
                    sessionManager.refresh()
                    if (sessionManager.session.value.isInvalid()) {
                        updateState { copy(chapterFeedLoading = false) }
                    }
                }
            }
            .onUniqueSession()
            .onEach { Log.d(this@DemoViewModel::class.java.simpleName, "Followed Feed Session Valid: $it") }
            .flatMapLatest { chapterRepository.getList(ChapterListRequest.Follows, limit = 30, hardRefresh = hardRefresh) }
            .distinctUntilChangedBy { it.successOrNull()?.id }
            .onEitherError { updateState { copy(chapterFeedLoading = false) } }
            .filterEitherSuccess()
            .flatMapLatest {
                val ids = it.items.mapNotNull { ch -> ch.relatedMangaId }.distinct()
                if (ids.isEmpty()) return@flatMapLatest flow<Pair<ChapterList, Either.Error<MangaList>>> {
                    emit(Pair(it, error(Exception("No Ids"))))
                }
                combine(
                    flow { emit(it) },
                    mangaRepository.getList(MangaListRequest.Generic(ids), hardRefresh = hardRefresh),
                    ::Pair
                )
            }
            .collect { (list, either) ->
                either.onSuccess { mangaList ->
                    val data = mangaList.items.map { manga ->
                        Pair(manga, list.items.filter { ch -> ch.relatedMangaId == manga.id })
                    }.sortedBy { (_, chapters) ->
                        chapters.all { ch -> ch.isRead == true }
                    }.toMap()

                    updateState {
                        copy(
                            chapterFeed = data,
                            chapterFeedLoading = false
                        )
                    }
                }.onError {

                }
            }
    }

    private fun getOtherLists(hardRefresh: Boolean) = viewModelScope.launch(
        Dispatchers.IO
    ) {
        updateState { copy(
            popularLoading = true,
            recentLoading = true,
        ) }
        combine(
            mangaRepository.getList(MangaListRequest.Popular, hardRefresh = hardRefresh),
            mangaRepository.getList(MangaListRequest.Recent, hardRefresh = hardRefresh),
            ::Pair
        )
            .onEach {
                it.first.onSuccess {
                    updateState { copy(popularLoading = false) }
                }
                it.first.onError {
                    updateState { copy(popularLoading = false) }
                }
                it.second.onSuccess {
                    updateState { copy(recentLoading = false) }
                }
                it.second.onError {
                    updateState { copy(recentLoading = false) }
                }
            }
            .map {
                Pair(it.first.successOrNull(), it.second.successOrNull())
            }.collect {
                updateState {
                    copy(
                        popularList = it.first ?: this.popularList,
                        recentList = it.second ?: this.recentList,
                    )
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getUserLists(hardRefresh: Boolean) = viewModelScope.launch(
        Dispatchers.IO
    ) {
        if (!hardRefresh && _uiState.value.userLists.isNotEmpty()) {
            updateState { copy(userListsLoading = false) }
            return@launch
        }

        updateState { copy(userListsLoading = true) }
        sessionManager.session
            .onUniqueSession()
            .onEach { Log.d(this@DemoViewModel::class.java.simpleName, "Lists Valid Session") }
            .map { userListRepository.getCurrentLists().successOrNull() }
            .filterNotNull()
            .flatMapMerge { userLists ->
                combine(
                    userLists.entries
                        .take(5)
                        .map { entry -> entry.key }
                        .map { listId ->
                            mangaRepository.getList(
                                MangaListRequest.UserList(listId = listId),
                                hardRefresh = hardRefresh,
                                limit = 15,
                            )
                        }
                ) { comb ->
                    comb.mapNotNull { list -> list.successOrNull() }
                }
            }
            .collect { lists ->
                updateState { copy(
                    userLists = lists,
                    userListsLoading = false
                ) }
            }
    }

    data class DemoState(
        override val errors: List<Any> = emptyList(),
        override val loading: Boolean = true,
        val seasonalList: MangaList? = null,
        val popularList: MangaList? = null,
        val recentList: MangaList? = null,
        val chapterFeed: Map<Manga, List<Chapter>> = emptyMap(),
        val userLists: List<MangaList> = emptyList(),
        val seasonalLoading: Boolean = false,
        val popularLoading: Boolean = false,
        val recentLoading: Boolean = false,
        val chapterFeedLoading: Boolean = false,
        val userListsLoading: Boolean = false,
    ) : BaseViewModelState()

    data class DemoParams(
        val userLoggedIn: Boolean = false
    )
}