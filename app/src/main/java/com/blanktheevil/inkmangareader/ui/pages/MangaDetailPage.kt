package com.blanktheevil.inkmangareader.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Badge
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.components.ImageHeader
import com.blanktheevil.inkmangareader.viewmodels.MangaDetailViewModel
import com.blanktheevil.inkmangareader.viewmodels.MangaDetailViewModel.State
import com.blanktheevil.inkmangareader.viewmodels.MangaDetailViewModel.Params
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun MangaDetailPage(mangaId: String) = BasePage<MangaDetailViewModel, State, Params>(
    viewModelParams = Params(mangaId)
) {uiState, _ ->
    val manga = uiState.manga ?: return@BasePage

    MangaDetailLayout(manga)
}

@Composable
private fun MangaDetailLayout(manga: Manga) {
    val headerHeight = LocalConfiguration.current.screenHeightDp.dp.times(0.5f)

    ImageHeader(
        initialHeight = headerHeight,
        minHeight = 64.dp,
        url = manga.coverArt,
        headerArea = {
            HeaderArea(manga = manga, scrollFraction = it)
        },
        placeholder = R.drawable.manga_placeholder,
    ) { nestedScrollConnection ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            item {
                MarkdownText(
                    manga.description,
                    linkColor = MaterialTheme.colorScheme.primary
                )
            }
            
            items(100) { index ->
                Text(text = "Test $index")
            }
        }
    }
}

@Composable
private fun HeaderArea(manga: Manga, scrollFraction: Float) = Column(
    modifier = Modifier.fillMaxSize()
) {
    val statusBarSize = with (LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }
    val buttonBackgroundColor = remember(scrollFraction) {
        Color.Black.copy(alpha = (1 - scrollFraction) * 0.8f)
    }
    val iconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = buttonBackgroundColor,
        disabledContainerColor = buttonBackgroundColor,
    )
    Spacer(modifier = Modifier.size(statusBarSize))
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp)
                .clip(RectangleShape),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                colors = iconButtonColors,
                onClick = { /*TODO*/ }
            ) {
                InkIcon(resId = R.drawable.round_arrow_back_24)
            }

            Text(
                modifier = Modifier
                    .offset(y = 128.dp.times(1 - scrollFraction))
                    .padding(horizontal = 8.dp)
                    .weight(1f)
                    .alpha(scrollFraction),
                text = manga.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            IconButton(
                colors = iconButtonColors,
                onClick = { /*TODO*/ }
            ) {
                InkIcon(resId = R.drawable.baseline_more_horiz_24)
            }
        }

        TitleDetailContent(
            modifier = Modifier.alpha(
                1 - scrollFraction
                    .times(2)
                    .coerceIn(0f, 1f)
            ),
            manga = manga
        )
    }
}

@Composable
private fun BoxScope.TitleDetailContent(
    modifier: Modifier = Modifier,
    manga: Manga,
) = Column(
    modifier = modifier
        .padding(8.dp)
        .padding(bottom = 8.dp)
        .align(Alignment.BottomStart),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    Text(
        text = manga.title,
        style = MaterialTheme.typography.headlineMedium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        manga.tags.take(3).forEach {
            Badge(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) { Text(text = it) }
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview() = DefaultPreview {
    MangaDetailLayout(manga = StubData.manga(
        title = "A really really long title of a manga because the japanese need help",
        coverArt = "https://mangadex.org/covers/141609b6-cf86-4266-904c-6648f389cdc9/bd903567-ae7e-433a-8a8d-65ceee3fc123.jpg"
    ))
}