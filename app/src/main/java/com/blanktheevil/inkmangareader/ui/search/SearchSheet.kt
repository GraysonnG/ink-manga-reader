package com.blanktheevil.inkmangareader.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.Tags
import com.blanktheevil.inkmangareader.data.models.Tag
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.statusBarSize
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
                onTagChanged = {_,_->},
                onTagModeChanged = {_,_->},
            )
        }
    }
}

@Composable
private fun SearchSheetContent(
    initialIncludedTags: List<Tag> = emptyList(),
    initialExcludedTags: List<Tag> = emptyList(),
    tags: List<Tag>,
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
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            trailingIcon = {
                InkIcon(
                    modifier = Modifier,
                    resId = R.drawable.round_search_24
                )
            },
            placeholder = { Text("Search...") },
            singleLine = true,
            shape = RectangleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceDim,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceDim,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceDim,
            )
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
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
            onTagChanged = {_,_->},
            onTagModeChanged = {_,_->},
        )
    }
}