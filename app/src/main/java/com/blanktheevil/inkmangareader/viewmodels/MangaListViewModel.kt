package com.blanktheevil.inkmangareader.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.emptyDataList
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.repositories.SearchParams
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.helpers.isUUID
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MangaListViewModel(
    private val mangaRepository: MangaRepository,
    private val moshi: Moshi,
) : BaseViewModel<MangaListViewModel.State, MangaListViewModel.Params>(State()) {
    companion object {
        private const val CHUNK_SIZE = 32
    }

    private var mangaListRequest: MangaListRequest? = null

    override fun initViewModel(hardRefresh: Boolean, params: Params?) = viewModelScope.launch(Dispatchers.IO) {
        params?.let {
            updateState { copy(loading = true) }
            mangaListRequest = getListRequestFromTypeOrId(it.typeOrId, it.extras)

            launch {
                mangaRepository.getList(
                    mangaListRequest!!,
                    hardRefresh = hardRefresh,
                    limit = CHUNK_SIZE,
                    offset = 0,
                ).collect { either ->
                    either.onSuccess { list ->
                        updateState { copy(
                            loading = false,
                            list = list
                        ) }
                    }
                    either.onError {
                        updateState { copy(
                            loading = false,
                        ) }
                    }
                }
            }
        }
    }

    fun loadMore() = viewModelScope.launch(Dispatchers.IO) {
        if (_uiState.value.offset + CHUNK_SIZE > _uiState.value.list.total) return@launch // don't load beyond the list bounds
        Log.d(this@MangaListViewModel::class.java.simpleName, "Loading More...")
        updateState { copy(loadingMore = true) }
        mangaListRequest?.let { request ->
            mangaRepository.getList(
                request,
                hardRefresh = true,
                limit = CHUNK_SIZE,
                offset = _uiState.value.offset + CHUNK_SIZE,
            ).collect { either ->
                either.onSuccess {
                    updateState { copy(
                        list = list + it,
                        offset = offset + CHUNK_SIZE,
                        loadingMore = false,
                    ) }
                }
            }
        }
    }

    fun removeItemFromList() {
        // make call to remove item from user list
    }

    operator fun DataList<Manga>.plus(other: DataList<Manga>) = DataList(
        items = (this.items + other.items).distinctBy { it.id }, // remove duplicate items
        title = this.title,
        offset = this.offset,
        limit = this.limit,
        total = this.total,
        extras = this.extras?.plus(other.extras ?: emptyMap()),
    )

    private fun getListRequestFromTypeOrId(typeOrId: String, extras: Map<String, String>) = when (typeOrId) {
        MangaListType.POPULAR -> MangaListRequest.Popular
        MangaListType.RECENT -> MangaListRequest.Recent
        MangaListType.SEARCH -> {
            val searchParams = extras["searchParams"]?.let { data ->
                moshi.adapter(SearchParams::class.java).fromJson(
                    data
                )
            } ?: throw IllegalArgumentException("Search parameters are required for SEARCH type")

            updateState { copy(searchParams = searchParams) }

            MangaListRequest.Search(params = searchParams)
        }
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
        val searchParams: SearchParams? = null,
        val loadingMore: Boolean = false,
        val offset: Int = 0,
    ) : BaseViewModelState()

    data class Params(
        val typeOrId: String,
        val extras: Map<String, String>,
    )
}

object MangaListType {
    const val POPULAR = "POPULAR"
    const val RECENT = "RECENT"
    const val SEARCH = "SEARCH"
}