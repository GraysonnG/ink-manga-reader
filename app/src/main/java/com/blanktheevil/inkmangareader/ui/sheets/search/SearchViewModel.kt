package com.blanktheevil.inkmangareader.ui.sheets.search

import androidx.lifecycle.viewModelScope
import com.blanktheevil.inkmangareader.data.models.Tag
import com.blanktheevil.inkmangareader.data.repositories.tags.TagsRepository
import com.blanktheevil.inkmangareader.viewmodels.BaseViewModel
import com.blanktheevil.inkmangareader.viewmodels.BaseViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tagsRepository: TagsRepository,
) : BaseViewModel<SearchState, Nothing>(SearchState()) {
    override fun initViewModel(
        hardRefresh: Boolean,
        params: Nothing?,
    ): Job = viewModelScope.launch {
        getTags()
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
) : BaseViewModelState()