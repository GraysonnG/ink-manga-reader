package com.blanktheevil.inkmangareader.viewmodels

import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.isValid
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.ChapterList
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.data.repositories.ChapterListRequest
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepository
import com.blanktheevil.inkmangareader.data.repositories.list.UserListRepository
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
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

    private fun getSeasonal(hardRefresh: Boolean) = viewModelScope.launch {
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

    private fun getFollowedMangaUpdatesFeed(hardRefresh: Boolean) = viewModelScope.launch {
        updateState { copy(chapterFeedLoading = true) }
        sessionManager.session
            .onEach { if (it == null) updateState { copy(chapterFeedLoading = false) } }
            .filterNotNull()
            .collect {
                if (it.isValid()) {
                    chapterRepository.getList(
                        ChapterListRequest.Follows,
                        limit = 60,
                        offset = 0,
                        hardRefresh = hardRefresh,
                    )
                        .onEach { either ->
                            either.onError {
                                updateState { copy(chapterFeedLoading = false) }
                            }
                        }
                        .filterIsInstance<Either.Success<ChapterList>>()
                        .collect { list ->
                            val ids = list.data.items.mapNotNull { ch -> ch.relatedMangaId }.distinct()

                            mangaRepository.getList(MangaListRequest.Generic(ids), hardRefresh = hardRefresh).collect { either ->
                                either.onSuccess { mangaList ->
                                    val data = mangaList.items.map { manga ->
                                        Pair(manga, list.data.items.filter { ch -> ch.relatedMangaId == manga.id })
                                    }.sortedBy { (_, chapters) ->
                                        chapters.all { ch -> ch.isRead == true }
                                    }.toMap()

                                    updateState {
                                        copy(
                                            chapterFeed = data,
                                            chapterFeedLoading = false
                                        )
                                    }
                                }

                                either.onError {
                                    updateState { copy(chapterFeedLoading = false) }
                                }
                            }
                        }
                }
            }
    }

    private fun getOtherLists(hardRefresh: Boolean) = viewModelScope.launch {
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

    private fun getUserLists(hardRefresh: Boolean) = viewModelScope.launch {
        updateState { copy(userListsLoading = true) }
        sessionManager.session
            .filterNotNull()
            .collect {
                userListRepository.getCurrentLists().onSuccess { userLists ->
                    val data = userLists.entries.map {
                        mangaRepository.getList(
                            MangaListRequest.Generic(it.value.items.take(15), it.value.title),
                            hardRefresh = hardRefresh,
                        )
                            .onEach { either ->
                                either.onError {
                                    updateState { copy(userListsLoading = false) }
                                }
                            }
                            .filterIsInstance<Either.Success<MangaList>>()
                            .map { e -> e.data }
                    }
                    combine(flows = data) { it.toList() }
                        .collect { updateState { copy(
                            userLists = it,
                            userListsLoading = false,
                        ) } }
                }
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