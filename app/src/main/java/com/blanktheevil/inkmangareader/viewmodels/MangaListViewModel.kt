package com.blanktheevil.inkmangareader.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.data.emptyDataList
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.data.plus
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.helpers.isUUID
import kotlinx.coroutines.launch
import java.util.UUID

class MangaListViewModel(
    private val mangaRepository: MangaRepository,
) : BaseViewModel<MangaListViewModel.State, MangaListViewModel.Params>(State()) {
    companion object {
        private const val CHUNK_SIZE = 32
    }

    private var mangaListRequest: MangaListRequest? = null

    override fun initViewModel(hardRefresh: Boolean, params: Params?) = viewModelScope.launch {
        params?.let {
            updateState { copy(loading = true) }
            mangaListRequest = getListRequestFromTypeOrId(it.typeOrId)

            launch {
                mangaRepository.getList(
                    mangaListRequest!!,
                    hardRefresh = true,
                    limit = CHUNK_SIZE,
                    offset = 0,
                ).collect { either ->
                    either.onSuccess { list ->
                        updateState { copy(
                            loading = false,
                            list = list
                        ) }
                    }
                }
            }
        }
    }

    fun loadMore() = viewModelScope.launch {
        mangaListRequest?.let { request ->
            mangaRepository.getList(
                request,
                hardRefresh = true,
                limit = CHUNK_SIZE,
                offset = _uiState.value.offset + CHUNK_SIZE,
            ).collect { either ->
                either.onSuccess {
                    updateState { copy(
                        list = list.plus(it),
                        offset = offset + CHUNK_SIZE,
                    ) }
                }
            }
        }
    }

    private fun getListRequestFromTypeOrId(typeOrId: String) = when (typeOrId) {
        MangaListType.POPULAR -> MangaListRequest.Popular
        MangaListType.RECENT -> MangaListRequest.Recent
        else -> {
            if (typeOrId.isUUID()) {
                MangaListRequest.UserList(typeOrId)
            } else {
                MangaListRequest.Popular
            }
        }
    }

    data class State(
        override val loading: Boolean = true,
        override val errors: List<Any> = emptyList(),
        val list: MangaList = emptyDataList(),
        val offset: Int = 0,
    ) : BaseViewModelState()

    data class Params(
        val typeOrId: String
    )
}

object MangaListType {
    const val POPULAR = "POPULAR"
    const val RECENT = "RECENT"
}