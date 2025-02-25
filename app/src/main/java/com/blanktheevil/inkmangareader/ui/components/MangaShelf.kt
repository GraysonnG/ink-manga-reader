package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview

@Composable
fun MangaShelf(
    mangaList: MangaList,
    onRowLinkClicked: () -> Unit = {},
    onItemClicked: (mangaId: String) -> Unit = {}
) = Column {
    mangaList.title?.let { title ->
        RowLink(title = title, onClick = onRowLinkClicked)
        Spacer(modifier = Modifier.size(8.dp))
    }
    LazyRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        item { Spacer(modifier = Modifier) }
        items(mangaList.items) {
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

@Composable
@PreviewLightDark
private fun Preview() = DefaultPreview {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MangaShelf(mangaList = StubData.mangaList(length = 4))
        MangaShelf(mangaList = StubData.mangaList(length = 4))
        MangaShelf(mangaList = StubData.mangaList(title = null, length = 4))
    }
}