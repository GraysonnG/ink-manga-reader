package com.blanktheevil.inkmangareader.viewmodels

import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MangaDetailViewModel(
    private val mangaRepository: MangaRepository
) : BaseViewModel<MangaDetailViewModel.State, MangaDetailViewModel.Params>(
    State()
) {
    override fun initViewModel(hardRefresh: Boolean, params: Params?): Job = viewModelScope.launch {
        params?.mangaId?.let { mangaId ->
            getMangaData(mangaId = mangaId, hardRefresh = hardRefresh)
        }
    }

    private fun getMangaData(mangaId: String, hardRefresh: Boolean) = viewModelScope.launch {
        mangaRepository.get(mangaId, hardRefresh = hardRefresh).collect {
            it.onSuccess {
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

    data class State(
        override val loading: Boolean = false,
        override val errors: List<Any> = emptyList(),
        val manga: Manga? = null,
    ): BaseViewModelState()

    data class Params(
        val mangaId: String,
    )
}