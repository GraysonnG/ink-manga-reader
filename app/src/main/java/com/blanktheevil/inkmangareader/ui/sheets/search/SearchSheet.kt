package com.blanktheevil.inkmangareader.ui.sheets.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.RectangleShape
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
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.components.HorizontalChipGroup
import com.blanktheevil.inkmangareader.ui.components.TextInputField
import com.blanktheevil.inkmangareader.ui.statusBarSize
import okhttp3.internal.toImmutableList
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSheet(
    onDismissRequest: () -> Unit = {},
) = Column {
    val viewModel = koinViewModel<SearchViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val state = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(Unit) {
        viewModel.initViewModel(false)
    }

    if (!uiState.loading) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            onDismissRequest = onDismissRequest,
            sheetState = state,
            shape = RectangleShape,
            dragHandle = null,
        ) {
            SearchSheetContent(
                tags = uiState.tags,
                searchText = uiState.searchText,
                onTextChanged = viewModel::onTextChanged,
                onOrderChanged = viewModel::onOrderChanged,
                onStatusChanged = viewModel::onStatusChanged,
                onDemographicsChanged = viewModel::onDemographicsChanged,
                onContentFiltersChanged = viewModel::onContentFiltersChanged,
                onTagChanged = viewModel::onTagsChanged,
                onTagModeChanged = viewModel::onTagModeChanged,
            )
        }
    }
}

@Composable
private fun SearchSheetContent(
    initialIncludedTags: List<Tag> = emptyList(),
    initialExcludedTags: List<Tag> = emptyList(),
    tags: List<Tag>,
    searchText: String,
    onTextChanged: (newText: String) -> Unit,
    onOrderChanged: (List<Order>) -> Unit,
    onStatusChanged: (List<Status>) -> Unit,
    onDemographicsChanged: (List<Demographic>) -> Unit,
    onContentFiltersChanged: (List<ContentFilter>) -> Unit,
    onTagChanged: (included: List<Tag>, excluded: List<Tag>) -> Unit,
    onTagModeChanged: (included: Tags.Mode, excluded: Tags.Mode) -> Unit,
) = Box(
    Modifier
        .fillMaxSize()
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = statusBarSize),
        horizontalAlignment = Alignment.End,
    ) {
        TextInputField(
            modifier = Modifier.fillMaxWidth(),
            value = searchText,
            placeholder = "Search...",
            onValueChange = onTextChanged,
            trailingIcon = {
                InkIcon(resId = R.drawable.round_search_24)
            },
            keyboardActions = KeyboardActions(
                onDone = {

                }
            )
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (tags.isNotEmpty()) {
                HorizontalChipGroup(
                    modifier = Modifier.padding(top = 16.dp),
                    title = "Order by:",
                    items = Order.list,
                    selectedItems = listOf(Order.Relevant),
                    onItemSelected = onOrderChanged,
                    itemToString = { stringResource(it.nameRes) },
                    selectionRequired = true,
                )

                HorizontalChipGroup(
                    title = "Status:",
                    items = Status.list,
                    selectedItems = emptyList(),
                    onItemSelected = onStatusChanged,
                    itemToString = { stringResource(it.nameRes) },
                    singleSelection = false,
                )

                HorizontalChipGroup(
                    title = "Demographic:",
                    items = Demographic.list,
                    selectedItems = emptyList(),
                    onItemSelected = onDemographicsChanged,
                    itemToString = { stringResource(it.nameRes) },
                    singleSelection = false,
                )

                HorizontalChipGroup(
                    title = "Content Filter:",
                    items = ContentFilter.list,
                    selectedItems = ContentFilter.default_ratings,
                    onItemSelected = onContentFiltersChanged,
                    itemToString = { stringResource(it.nameRes) },
                    singleSelection = false,
                    selectionRequired = false,
                )

                Box {
                    TagSelector(
                        initialIncludedTags = initialIncludedTags,
                        initialExcludedTags = initialExcludedTags,
                        tags = tags,
                        onTagChanged = onTagChanged,
                        onTagModeChanged = onTagModeChanged,
                    )
                }
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
        onClick = { /*TODO*/ }
    ) {
        Text("Search")
    }
}

@PreviewLightDark
@Composable
private fun Preview() = DefaultPreview {
    val tags = StubData.tagList(32)

    Box(modifier = Modifier.fillMaxSize()) {
        SearchSheetContent(
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
            onContentFiltersChanged = { _ -> }
        )
    }
}
