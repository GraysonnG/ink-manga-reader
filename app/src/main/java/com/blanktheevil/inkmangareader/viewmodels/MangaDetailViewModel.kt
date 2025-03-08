package com.blanktheevil.inkmangareader.viewmodels

import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.emptyDataList
import com.blanktheevil.inkmangareader.data.models.ChapterList
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.ChapterListRequest
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepository
import com.blanktheevil.inkmangareader.data.repositories.list.UserListRepository
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.data.repositories.mappers.LinkedChapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MangaDetailViewModel(
    private val mangaRepository: MangaRepository,
    private val chapterRepository: ChapterRepository,
    private val userListRepository: UserListRepository,
) : BaseViewModel<MangaDetailViewModel.State, MangaDetailViewModel.Params>(
    State()
) {
    override fun initViewModel(hardRefresh: Boolean, params: Params?): Job = viewModelScope.launch(
        Dispatchers.IO
    ) {
        if (hardRefresh) {
            updateState { State() }
        }

        params?.mangaId?.let { mangaId ->
            getMangaData(mangaId = mangaId, hardRefresh = hardRefresh)
            getMangaFollowed(mangaId = mangaId)
            getMangaChapterFeed(mangaId = mangaId, hardRefresh = hardRefresh)
            getFirstChapter(mangaId = mangaId)
        }
    }

    fun toggleFollowManga() = viewModelScope.launch(Dispatchers.IO) {
        with (_uiState.value) {
            if (!loading && manga != null) {
                if (followed) {
                    mangaRepository.unfollow(manga.id)
                } else {
                    mangaRepository.follow(manga.id)
                }.onSuccess {
                    updateState { copy(followed = !followed) }
                }
            }
        }
    }

    suspend fun getCurrentUserLists(): Map<String, DataList<String>> {
        return userListRepository.getCurrentLists().successOrNull() ?: emptyMap()
    }

    suspend fun addToList(mangaId: String, listId: String): Either<Unit> {
        return userListRepository.addMangaToList(mangaId, listId)
    }

    suspend fun removeFromList(mangaId: String, listId: String): Either<Unit> {
        return userListRepository.removeMangaFromList(mangaId, listId)
    }

    private suspend fun getMangaData(mangaId: String, hardRefresh: Boolean) = viewModelScope.launch(
        Dispatchers.IO
    ) {
        updateState { copy(loading = true) }
        mangaRepository.get(mangaId, hardRefresh = hardRefresh).collect {
            it.onSuccess { manga ->
                updateState { copy(
                    loading = false,
                    manga = manga,
                ) }
            }

            it.onError {
                updateState { copy(
                    loading = false,
                ) }
            }
        }
    }

    private suspend fun getMangaChapterFeed(mangaId: String, hardRefresh: Boolean) = viewModelScope.launch(
        Dispatchers.IO
    ) {
        chapterRepository.getList(ChapterListRequest.Feed(mangaId), limit = 90, hardRefresh = hardRefresh).collect {
            when (it) {
                is Either.Success -> {
                    updateState { copy(
                        chapterFeed = it.data
                    ) }
                }

                else -> {

                }
            }
        }
    }

    private suspend fun getFirstChapter(mangaId: String) = viewModelScope.launch(
        Dispatchers.IO
    ) {
        mangaRepository.getAggregate(mangaId = mangaId).onSuccess {
            val firstChapter = it.firstOrNull { ch -> ch.prevId == null }
            updateState { copy(
                firstChapter = firstChapter
            ) }
        }
    }

    private suspend fun getMangaFollowed(mangaId: String) = viewModelScope.launch(Dispatchers.IO) {
        mangaRepository.getFollowing(mangaId = mangaId)
            .onSuccess {
                updateState { copy(
                    followed = true
                ) }
            }
            .onError {
                updateState { copy(
                    followed = false
                ) }
            }
    }

    data class State(
        override val loading: Boolean = true,
        override val errors: List<Any> = emptyList(),
        val manga: Manga? = null,
        val followed: Boolean = false,
        val chapterFeed: ChapterList = emptyDataList(),
        val firstChapter: LinkedChapter? = null,
    ): BaseViewModelState()

    data class Params(
        val mangaId: String,
    )
}