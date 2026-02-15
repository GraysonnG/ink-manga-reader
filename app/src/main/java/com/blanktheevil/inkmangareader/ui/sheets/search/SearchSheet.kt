package com.blanktheevil.inkmangareader.ui.sheets.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.ContentFilter
import com.blanktheevil.inkmangareader.data.Demographic
import com.blanktheevil.inkmangareader.data.Order
import com.blanktheevil.inkmangareader.data.Status
import com.blanktheevil.inkmangareader.data.Tags
import com.blanktheevil.inkmangareader.data.models.Tag
import com.blanktheevil.inkmangareader.navigation.navigateToMangaList
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.LocalNavController
import com.blanktheevil.inkmangareader.ui.components.HorizontalChipGroup
import com.blanktheevil.inkmangareader.ui.components.TextInputField
import com.blanktheevil.inkmangareader.ui.statusBarSize
import com.blanktheevil.inkmangareader.viewmodels.MangaListType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSheet(
    onDismissRequest: () -> Unit = {},
) = Column {
    val nav = LocalNavController.current
    val viewModel = koinViewModel<SearchViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val state = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(Unit) {
        viewModel.initViewModel(false)
    }

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = {
            onDismissRequest()
            viewModel.onDismiss()
        },
        sheetState = state,
    ) {
        SearchSheetContent(
            loading = uiState.loading,
            tags = uiState.tags,
            initialContentFilters = uiState.initialContentFilters,
            searchText = uiState.searchText,
            onTextChanged = viewModel::onTextChanged,
            onOrderChanged = viewModel::onOrderChanged,
            onStatusChanged = viewModel::onStatusChanged,
            onDemographicsChanged = viewModel::onDemographicsChanged,
            onContentFiltersChanged = viewModel::onContentFiltersChanged,
            onTagChanged = viewModel::onTagsChanged,
            onTagModeChanged = viewModel::onTagModeChanged,
            onSearch = {
                nav.navigateToMangaList(
                    MangaListType.SEARCH,
                    mapOf(
                        "searchParams" to viewModel.getSearchParamsString()
                    )
                )
            }
        )
    }
}

@Composable
private fun SearchSheetContent(
    loading: Boolean = false,
    tags: List<Tag>,
    initialContentFilters: List<ContentFilter>,
    searchText: String,
    initialIncludedTags: List<Tag> = emptyList(),
    initialExcludedTags: List<Tag> = emptyList(),
    onTextChanged: (newText: String) -> Unit,
    onOrderChanged: (List<Order>) -> Unit,
    onStatusChanged: (List<Status>) -> Unit,
    onDemographicsChanged: (List<Demographic>) -> Unit,
    onContentFiltersChanged: (List<ContentFilter>) -> Unit,
    onTagChanged: (included: List<Tag>, excluded: List<Tag>) -> Unit,
    onTagModeChanged: (included: Tags.Mode, excluded: Tags.Mode) -> Unit,
    onSearch: () -> Unit,
) = Box(
    Modifier
        .fillMaxSize()
) {
    val searchPlaceholder = stringResource(R.string.search_placeholder)
    val searchButtonText = stringResource(R.string.search_button)
    val filterOrderTitle = stringResource(R.string.filter_order)
    val filterStatusTitle = stringResource(R.string.filter_status)
    val filterDemographicTitle = stringResource(R.string.filter_demographic)
    val filterContentTitle = stringResource(R.string.filter_content)

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = statusBarSize)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.End,
    ) {
        TextInputField(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            value = searchText,
            placeholder = searchPlaceholder,
            onValueChange = onTextChanged,
            shape = RoundedCornerShape(64.dp),
            trailingIcon = {
                InkIcon(resId = R.drawable.round_search_24)
            },
            keyboardActions = KeyboardActions(
                onDone = { onSearch() }
            )
        )

        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 64.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HorizontalChipGroup(
                modifier = Modifier.padding(top = 16.dp),
                title = filterOrderTitle,
                items = Order.list,
                selectedItems = listOf(Order.Relevant),
                onItemSelected = onOrderChanged,
                itemToString = { stringResource(it.nameRes) },
                selectionRequired = true,
            )

            HorizontalChipGroup(
                title = filterStatusTitle,
                items = Status.list,
                selectedItems = emptyList(),
                onItemSelected = onStatusChanged,
                itemToString = { stringResource(it.nameRes) },
                singleSelection = false,
            )

            HorizontalChipGroup(
                title = filterDemographicTitle,
                items = Demographic.list,
                selectedItems = emptyList(),
                onItemSelected = onDemographicsChanged,
                itemToString = { stringResource(it.nameRes) },
                singleSelection = false,
            )

            HorizontalChipGroup(
                title = filterContentTitle,
                items = ContentFilter.list,
                selectedItems = initialContentFilters,
                onItemSelected = onContentFiltersChanged,
                itemToString = { stringResource(it.nameRes) },
                singleSelection = false,
                selectionRequired = false,
            )

            if (tags.isNotEmpty()) {
                TagSelector(
                    initialIncludedTags = initialIncludedTags,
                    initialExcludedTags = initialExcludedTags,
                    tags = tags,
                    onTagChanged = onTagChanged,
                    onTagModeChanged = onTagModeChanged,
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    Button(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp)
        ,
        onClick = onSearch
    ) {
        Text(searchButtonText)
    }
}

@PreviewLightDark
@Composable
private fun Preview() = DefaultPreview {
    val tags = StubData.tagList(32)

    Box(modifier = Modifier.fillMaxSize()) {
        SearchSheetContent(
            initialContentFilters = ContentFilter.default_ratings,
            initialIncludedTags = listOf(tags[2]),
            initialExcludedTags = listOf(tags[18]),
            tags = tags,
            searchText = "",
            onTextChanged = {},
            onTagChanged = {_,_->},
            onTagModeChanged = {_,_->},
            onOrderChanged = {},
            onStatusChanged = {},
            onDemographicsChanged = {},
            onContentFiltersChanged = { _ -> },
            onSearch = {}
        )
    }
}
