package com.blanktheevil.inkmangareader.viewmodels

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.combine
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.Order
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.error
import com.blanktheevil.inkmangareader.data.filterEitherSuccess
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.data.models.Tag
import com.blanktheevil.inkmangareader.data.onEitherError
import com.blanktheevil.inkmangareader.data.onUniqueSession
import com.blanktheevil.inkmangareader.data.repositories.ChapterListRequest
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.repositories.SearchParams
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepository
import com.blanktheevil.inkmangareader.data.repositories.list.UserListRepository
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.data.success
import com.blanktheevil.inkmangareader.data.withSession
import com.blanktheevil.inkmangareader.helpers.getCreatedAtSinceString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
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

    fun filterPopularFeed(filter: Tag?) = viewModelScope.launch(
        Dispatchers.IO
    ) {
        if (filter != null) {
            mangaRepository.getList(
                MangaListRequest.Search(
                    SearchParams(
                        search = null,
                        order = Order.FollowsHigh,
                        includedTags = listOf(filter),
                        createdAtSince = getCreatedAtSinceString()
                    )
                ),
                hardRefresh = true
            )
        } else {
            mangaRepository.getList(
                MangaListRequest.Popular,
                hardRefresh = false
            )
        }.collect {
            it.onSuccess { list ->
                updateState { copy(
                    popularList = list.copy(title = popularList?.title)
                ) }
            }
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

        sessionManager
            .withSession(
                chapterRepository.getList(
                    request = ChapterListRequest.Follows,
                    limit = 30,
                    hardRefresh = hardRefresh
                )
            )
            .filterEitherSuccess()
            .flatMapLatest { chapters ->
                val ids = chapters.items.mapNotNull { ch -> ch.relatedMangaId }.distinct()
                if (ids.isEmpty()) return@flatMapLatest flowOf(
                    error(Exception("No chapters found"))
                )

                mangaRepository.getList(
                    MangaListRequest.Generic(ids),
                    hardRefresh = hardRefresh
                )
                    .map { either ->
                        when (either) {
                            is Either.Success -> success(
                                either.data.items.associateWith { manga ->
                                    chapters.items.filter {
                                        it.relatedMangaId == manga.id
                                    }
                                }
                            )
                            is Either.Null -> Either.Null()
                            is Either.Error -> error(either.error)
                        }
                    }
            }
            .distinctUntilChanged()
            .onEitherError {
                updateState { copy(chapterFeedLoading = false) }
            }
            .filterEitherSuccess()
            .debounce(200)
            .collect { data ->
                updateState {
                    copy(
                        chapterFeed = data,
                        chapterFeedLoading = false,
                    )
                }
            }
//            .distinctUntilChanged()
//            .collect { (chapters, mangaEither) ->
//                mangaEither.onSuccess { mangaList ->
//                    val data = mangaList.items.map { manga ->
//                        Pair(manga, chapters.items.filter { ch -> ch.relatedMangaId == manga.id })
//                    }.sortedBy { (_, sortedChapters) ->
//                        sortedChapters.all { ch -> ch.isRead == true }
//                    }.toMap()
//
//                    updateState {
//                        copy(
//                            chapterFeed = data,
//                            chapterFeedLoading = false
//                        )
//                    }
//                }
//            }
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

    @Immutable
    @Stable
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