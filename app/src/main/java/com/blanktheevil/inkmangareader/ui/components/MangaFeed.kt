package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.toAsyncPainterImage

@Composable
fun MangaFeed(
    feed: Map<Manga, List<Chapter>>,
    modifier: Modifier = Modifier,
    onClick: (mangaId: String) -> Unit,
) = Column {
    RowLink(title = "My Updates")
    Spacer(modifier = Modifier.size(8.dp))
    LazyRow(
        modifier = modifier,
        userScrollEnabled = feed.isNotEmpty(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { Spacer(modifier = Modifier) }
        if (feed.isNotEmpty()) {
            items(feed.entries.toList()) { (manga, list) ->
                MangaItem(manga = manga, chapters = list, onClick = onClick)
            }
        } else {
            items(4) {
                MangaItemSkeleton()
            }
        }
        item { Spacer(modifier = Modifier) }
    }
}

@Composable
private fun MangaItem(
    manga: Manga,
    chapters: List<Chapter>,
    onClick: (mangaId: String) -> Unit,
) = Column(
    modifier = Modifier
        .width(110.dp)
        .clip(RoundedCornerShape(8.dp))
        .clickable(
            interactionSource = null,
            indication = ripple(),
            role = Role.Button,
            onClick = { onClick(manga.id) },
        )
) {
    val coverImage = manga.coverArt.toAsyncPainterImage(crossfade = true)
    val unreadChapters = remember(chapters) {
        chapters.filter { it.isRead != true }.size
    }
    Image(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .aspectRatio(1f),
        painter = coverImage,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
    )
    Spacer(modifier = Modifier.size(4.dp))
    Text(
        text = manga.title,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelLarge,
    )
    val text = when {
        unreadChapters > 1 -> "$unreadChapters unread chapters."
        unreadChapters == 1 -> "1 unread chapter."
        else -> "All caught up!"
    }

    val color = if (unreadChapters > 0) {
        MaterialTheme.colorScheme.primary
    } else {
        LocalContentColor.current.copy(alpha = 0.5f)
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
private fun MangaItemSkeleton() = Column(
    modifier = Modifier
        .width(110.dp)
        .clip(RoundedCornerShape(8.dp))
) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        .fillMaxWidth()
        .aspectRatio(1f))
    Spacer(modifier = Modifier.size(4.dp))
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
            .fillMaxWidth()
            .height(14.dp)
    )
}


@Composable
@PreviewLightDark
private fun Preview() = DefaultPreview {
    val mangaList = StubData.mangaList(length = 6)
    val chapterList = StubData.chapterList(length = 3)

    Column(modifier = Modifier.fillMaxSize()) {
        MangaFeed(mangaList.items.associateWith { chapterList.items }) {}
        MangaFeed(feed = emptyMap()) {}
    }
}