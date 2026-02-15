package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.data.Tags
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.data.models.Tag
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.skeletonBackground
import com.valentinilk.shimmer.shimmer

@Composable
fun FilteredMangaShelf(
    mangaList: MangaList,
    filters: List<Tag>,
    loading: Boolean,
    onRowLinkClicked: () -> Unit,
    onItemClicked: (String) -> Unit,
    onFilterSelectionChanged: (Tag?) -> Unit,
) {
    if (loading) {
        FilteredMangaShelfSkeleton()
    } else {
        FilteredMangaShelfContent(
            mangaList = mangaList,
            filters = filters,
            onRowLinkClicked = onRowLinkClicked,
            onItemClicked = onItemClicked,
            onFilterSelectionChanged = onFilterSelectionChanged,
        )
    }
}

@Composable
private fun FilteredMangaShelfContent(
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
            InkMangaCard(
                manga = it,
                mangaCardType = MangaCardType.TALL,
            ) {
                onItemClicked(it.id)
            }
        }
        item { Spacer(modifier = Modifier) }
    }
}

@Composable
private fun FilteredMangaShelfSkeleton() = Column(
    modifier = Modifier
        .shimmer()
        .fillMaxWidth()
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .skeletonBackground()
            .fillMaxWidth()
            .height(24.dp)
    )

    Spacer(Modifier.size(8.dp))

    LazyRow(
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(10) { Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .skeletonBackground()
                .width(75.dp)
                .height(28.dp)
        ) }
    }

    Spacer(Modifier.size(8.dp))
    
    LazyRow(
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(8) {
            InkMangaCardSkeleton(MangaCardType.TALL, modifier = Modifier)
        }
    }
}

@PreviewLightDark
@Composable
private fun FilteredMangaShelfPreview() = DefaultPreview {
    FilteredMangaShelf(
        mangaList = StubData.mangaList(length = 15),
        filters = Tags.PopularFilters,
        loading = false,
        onRowLinkClicked = {},
        onItemClicked = {},
        onFilterSelectionChanged = {}
    )
}

@PreviewLightDark
@Composable
private fun FilteredMangaShelfSkeletonPreview() = DefaultPreview {
    FilteredMangaShelfSkeleton()
}
