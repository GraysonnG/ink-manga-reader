package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview

@Composable
fun MangaFeed(
    feed: Map<Manga, List<Chapter>>,
    loading: Boolean,
    modifier: Modifier = Modifier,
    onClick: (mangaId: String) -> Unit,
) = Column {
    val list = remember(feed) { feed.entries.toList() }
    val yourUpdatesTitleString = stringResource(id = R.string.manga_feed_section_title)
    RowLink(title = yourUpdatesTitleString)
    Spacer(modifier = Modifier.size(8.dp))
    LazyRow(
        modifier = modifier,
        userScrollEnabled = feed.isNotEmpty(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { Spacer(modifier = Modifier) }
        when {
            !loading -> items(list) { (manga, list) ->
                InkMangaCard(
                    manga = manga,
                    mangaCardType = MangaCardType.SQUARE,
                    subtitle = {
                        MangaCardSubtitle(chapters = list)
                    },
                    onClick = { onClick(manga.id) },
                )
            }
            loading -> items(4) {
                InkMangaCardSkeleton(
                    MangaCardType.SQUARE
                )
            }
            !loading && feed.isEmpty() -> item {
                Text("Oof!")
            }
        }
        item { Spacer(modifier = Modifier) }
    }
}

@Composable
private fun MangaCardSubtitle(
    chapters: List<Chapter>
) {
    val unreadChapters = remember(chapters) {
        chapters.filter { it.isRead != true }.size
    }

    val color = if (unreadChapters > 0) {
        MaterialTheme.colorScheme.primary
    } else {
        LocalContentColor.current.copy(alpha = 0.5f)
    }

    val text = if (unreadChapters == 0) {
        stringResource(id = R.string.manga_feed_card_subtitle_zero)
    } else {
        pluralStringResource(
            id = R.plurals.manga_feed_card_subtitle,
            count = unreadChapters,
            unreadChapters
        )
    }

    Text(
        text = text,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall,
        color = color,
    )
}

@Composable
@PreviewLightDark
private fun Preview() = DefaultPreview {
    val mangaList = StubData.mangaList(length = 6)
    val chapterList = StubData.chapterList(length = 3)

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(64.dp)) {
        MangaFeed(mangaList.items.associateWith { chapterList.items }, loading = false) {}
        MangaFeed(feed = emptyMap(), loading = true) {}
    }
}