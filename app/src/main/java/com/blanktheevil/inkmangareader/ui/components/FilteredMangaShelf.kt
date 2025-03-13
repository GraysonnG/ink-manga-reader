package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.data.Tags
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.data.models.Tag
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview

@Composable
fun FilteredMangaShelf(
    mangaList: MangaList,
    filters: List<Tag>,
    onRowLinkClicked: () -> Unit,
    onItemClicked: (String) -> Unit,
    onFilterSelectionChanged: (Tag?) -> Unit,
) = Column {
    var selectedFilter by remember { mutableStateOf<Tag?>(null) }

    mangaList.title?.let { title ->
        RowLink(title = title, onClick = onRowLinkClicked)
        Spacer(modifier = Modifier.size(8.dp))
    }

    if (filters.isNotEmpty()) {
        LazyRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) {
            items(filters) {
                val selected = remember(selectedFilter) {
                    selectedFilter?.id == it.id
                }

                FilterChip(
                    selected = selected,
                    onClick = {
                        selectedFilter = if (!selected) it else null
                        onFilterSelectionChanged(selectedFilter)
                    },
                    label = {
                        Text(it.name)
                    },
                )
            }
        }

        Spacer(modifier = Modifier.size(8.dp))
    }

    LazyRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        item { Spacer(modifier = Modifier) }
        items(mangaList.items, key = { it.id }) {
            MangaCard(
                imageModifier = Modifier
                    .height(240.dp),
                manga = it
            ) {
                onItemClicked(it.id)
            }
        }
        item { Spacer(modifier = Modifier) }
    }
}

@PreviewLightDark
@Composable
private fun FilteredMangaShelfPreview() = DefaultPreview {
    FilteredMangaShelf(
        mangaList = StubData.mangaList(length = 15),
        filters = Tags.PopularFilters,
        onRowLinkClicked = {},
        onItemClicked = {},
        onFilterSelectionChanged = {}
    )
}