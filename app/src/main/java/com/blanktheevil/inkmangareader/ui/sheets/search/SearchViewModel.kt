package com.blanktheevil.inkmangareader.ui.sheets.search

import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.data.ContentFilter
import com.blanktheevil.inkmangareader.data.ContentRatings
import com.blanktheevil.inkmangareader.data.Demographic
import com.blanktheevil.inkmangareader.data.Order
import com.blanktheevil.inkmangareader.data.Status
import com.blanktheevil.inkmangareader.data.Tags
import com.blanktheevil.inkmangareader.data.models.Tag
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.repositories.SearchParams
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.data.repositories.tags.TagsRepository
import com.blanktheevil.inkmangareader.settings.SettingsManager
import com.blanktheevil.inkmangareader.viewmodels.BaseViewModel
import com.blanktheevil.inkmangareader.viewmodels.BaseViewModelState
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tagsRepository: TagsRepository,
    private val mangaRepository: MangaRepository,
    private val moshi: Moshi,
    private val settingsManager: SettingsManager,
) : BaseViewModel<SearchState, Nothing>(SearchState()) {
    override fun initViewModel(
        hardRefresh: Boolean,
        params: Nothing?,
    ): Job = viewModelScope.launch {
        getTags()

        val initialContentFilters = settingsManager.settingsState.firstOrNull()?.contentFilter?.mapNotNull {
            ContentFilter.getContentFilterByValue(it)
        } ?: ContentFilter.default_ratings

        updateState {
            SearchState(
                initialContentFilters = initialContentFilters
            )
        }
    }

    fun onDismiss() {
        updateState { SearchState() }
    }

    fun onTextChanged(newText: String) {
        updateState { copy(
            searchText = newText
        ) }
    }

    fun onOrderChanged(order: List<Order>) {
        order.firstOrNull()?.let {
            updateState { copy(
                order = it
            ) }
        }
    }

    fun onStatusChanged(status: List<Status>) {
        updateState { copy(
            status = status
        ) }
    }

    fun onDemographicsChanged(demographics: List<Demographic>) {
        updateState { copy(
            demographics = demographics
        ) }
    }

    fun onContentFiltersChanged(contentFilters: List<ContentFilter>) {
        updateState { copy(
            contentFilters = contentFilters.map { it.value }
        ) }
    }

    fun onTagsChanged(includedTags: List<Tag>, excludedTags: List<Tag>) {
        updateState { copy(
            includedTags = includedTags,
            excludedTags = excludedTags,
        ) }
    }

    fun onTagModeChanged(includedMode: Tags.Mode, excludedMode: Tags.Mode) {
        updateState { copy(
            includedTagMode = includedMode,
            excludedTagMode = excludedMode,
        ) }
    }

    fun getSearchParamsString(): String = with (uiState.value) {
        val params = SearchParams(
            search = this.searchText,
            contentRating = this.contentFilters,
            order = order,
            publicationDemographic = demographics.map { it.value },
            status = status.map { it.value },
            includedTags = includedTags,
            excludedTags = excludedTags,
            includedTagsMode = includedTagMode,
            excludedTagsMode = excludedTagMode,
        )
        // prefetch the search results before navigating to the list page
        viewModelScope.launch {
            mangaRepository.getList(MangaListRequest.Search(params), hardRefresh = true)
        }

        moshi.adapter(SearchParams::class.java).toJson(params)
    }


    private fun getTags() = viewModelScope.launch(
        Dispatchers.IO
    ) {
        tagsRepository.getAllTags().onSuccess {
            updateState { copy(
                tags = it,
                loading = false,
            ) }
        }
    }
}

data class SearchState(
    override val loading: Boolean = true,
    override val errors: List<Any> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val initialContentFilters: List<ContentFilter> = ContentFilter.default_ratings,
    val searchText: String = "",
    val order: Order = Order.Relevant,
    val status: List<Status> = emptyList(),
    val demographics: List<Demographic> = emptyList(),
    val contentFilters: ContentRatings = ContentFilter.DEFAULT_RATINGS,
    val includedTags: List<Tag> = emptyList(),
    val excludedTags: List<Tag> = emptyList(),
    val includedTagMode: Tags.Mode = Tags.Mode.AND,
    val excludedTagMode: Tags.Mode = Tags.Mode.OR,
) : BaseViewModelState()