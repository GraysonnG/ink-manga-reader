package com.blanktheevil.inkmangareader.viewmodels

import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.emptyDataList
import com.blanktheevil.inkmangareader.data.models.ChapterList
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.ChapterListRequest
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepository
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.data.repositories.mappers.LinkedChapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MangaDetailViewModel(
    private val mangaRepository: MangaRepository,
    private val chapterRepository: ChapterRepository,
) : BaseViewModel<MangaDetailViewModel.State, MangaDetailViewModel.Params>(
    State()
) {
    override fun initViewModel(hardRefresh: Boolean, params: Params?): Job = viewModelScope.launch {
        updateState {
            if (hardRefresh) {
                State()
            } else {
                copy(
                    loading = true,
                )
            }
        }
        params?.mangaId?.let { mangaId ->
            getMangaData(mangaId = mangaId, hardRefresh = hardRefresh)
            getMangaChapterFeed(mangaId = mangaId, hardRefresh = hardRefresh)
            getFirstChapter(mangaId = mangaId)
        }
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

            it.onNull {
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

    data class State(
        override val loading: Boolean = false,
        override val errors: List<Any> = emptyList(),
        val manga: Manga? = null,
        val chapterFeed: ChapterList = emptyDataList(),
        val firstChapter: LinkedChapter? = null,
    ): BaseViewModelState()

    data class Params(
        val mangaId: String,
    )
}