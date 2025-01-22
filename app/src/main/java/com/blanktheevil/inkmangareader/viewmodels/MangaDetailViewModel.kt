package com.blanktheevil.inkmangareader.viewmodels

import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.data.state.MangaStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MangaDetailViewModel(
    private val mangaRepository: MangaRepository
) : BaseViewModel<MangaDetailViewModel.State>(State()) {
    private lateinit var mangaStateFlow: MangaStateFlow
    private var mangaId: String? = null

    fun initDetails(mangaId: String) = viewModelScope.launch {
        this@MangaDetailViewModel.mangaId = mangaId
        mangaStateFlow = mangaRepository.get(mangaId = mangaId)
        mangaStateFlow.collect {
            updateState {
                when (it) {
                    is Either.Success -> {
                        copy(manga = it.data)
                    }

                    else -> {
                        copy(manga = null)
                    }
                }
            }
        }
    }

    data class State(
        val manga: Manga? = null,
    )
}