package com.blanktheevil.inkmangareader.ui.pages

import android.content.ClipData
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.models.ChapterList
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.mappers.LinkedChapter
import com.blanktheevil.inkmangareader.reader.ReaderManager
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.LocalNavController
import com.blanktheevil.inkmangareader.ui.components.ImageHeader
import com.blanktheevil.inkmangareader.ui.components.InkMenuItem
import com.blanktheevil.inkmangareader.ui.components.VolumesSkeleton
import com.blanktheevil.inkmangareader.ui.components.volumeItems
import com.blanktheevil.inkmangareader.ui.permanentStatusBarSize
import com.blanktheevil.inkmangareader.ui.theme.LocalContainerSwatch
import com.blanktheevil.inkmangareader.ui.theme.LocalPrimarySwatch
import com.blanktheevil.inkmangareader.ui.theme.LocalSurfaceSwatch
import com.blanktheevil.inkmangareader.ui.theme.containerSwatch
import com.blanktheevil.inkmangareader.ui.theme.primarySwatch
import com.blanktheevil.inkmangareader.ui.theme.surfaceSwatch
import com.blanktheevil.inkmangareader.ui.theme.toColorPalette
import com.blanktheevil.inkmangareader.viewmodels.MangaDetailViewModel
import com.blanktheevil.inkmangareader.viewmodels.MangaDetailViewModel.Params
import com.blanktheevil.inkmangareader.viewmodels.MangaDetailViewModel.State
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.koin.compose.koinInject

@Composable
fun MangaDetailPage(mangaId: String) = BasePage<MangaDetailViewModel, State, Params>(
    viewModelParams = Params(mangaId)
) {viewModel, uiState, _ ->
    val nav = LocalNavController.current
    val readerManager = koinInject<ReaderManager>()
    val palette = uiState.manga?.toColorPalette()
    val clipManager = LocalClipboardManager.current

    uiState.manga?.let { manga ->
        CompositionLocalProvider(
            LocalPrimarySwatch provides palette.primarySwatch,
            LocalContainerSwatch provides palette.containerSwatch,
            LocalSurfaceSwatch provides palette.surfaceSwatch,
        ) {
            MangaDetailLayout(
                manga,
                uiState.chapterFeed,
                firstChapter = uiState.firstChapter,
                loading = uiState.loading,
                followed = uiState.followed,
                onBackButtonClicked = { nav.navigateUp() },
                onMenuItemClicked = {
                    when(it) {
                        0,
                        1 -> {}
                        2 -> {
                            val clipData = ClipData.newPlainText(manga.title, "https://mangadex.org/title/${manga.id}")
                            clipManager.setClip(ClipEntry(clipData))
                        }
                    }
                },
                onStartReadingClicked = {
                    uiState.firstChapter?.let {
                        readerManager.setChapter(it.id)
                    }
                },
                onFollowButtonClicked = {
                    viewModel.toggleFollowManga()
                }
            )
        }
    }
}

@Composable
private fun MangaDetailLayout(
    manga: Manga,
    chapters: ChapterList,
    loading: Boolean,
    followed: Boolean,
    firstChapter: LinkedChapter? = null,
    @DrawableRes headerPlaceholderImage: Int? = null,
    onStartReadingClicked: () -> Unit = {},
    onBackButtonClicked: () -> Unit = {},
    onMenuItemClicked: (Int) -> Unit = {},
    onFollowButtonClicked: () -> Unit = {},
) = Surface(
    color = LocalSurfaceSwatch.current.color,
    contentColor = LocalSurfaceSwatch.current.onColor,
) {
    val headerHeight = LocalConfiguration.current.screenHeightDp.dp.times(0.5f)
    val volumes = remember(chapters) {
        chapters.items.groupBy { it.volume ?: "No Volume" }
    }
    val followButtonIcon = if (followed) {
        R.drawable.round_favorite_24
    } else {
        R.drawable.round_favorite_border_24
    }

    Box {
        ImageHeader(
            initialHeight = headerHeight,
            minHeight = 64.dp,
            url = manga.coverArt,
            headerArea = {
                HeaderArea(
                    manga = manga,
                    firstChapter = firstChapter,
                    scrollFraction = it,
                    onBackButtonClicked = onBackButtonClicked,
                    onStartReadingClicked = onStartReadingClicked,
                    onMenuItemClicked = onMenuItemClicked,
                )
            },
            placeholder = headerPlaceholderImage,
        ) { nestedScrollConnection ->
            CompositionLocalProvider(
                LocalContentColor provides LocalSurfaceSwatch.current.onColor
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(nestedScrollConnection),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    userScrollEnabled = chapters.items.isNotEmpty()
                ) {
                    item {
                        MarkdownText(
                            manga.description,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .padding(horizontal = 8.dp)
                            ,
                            linkColor = LocalPrimarySwatch.current.color,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 99,
                        )
                    }

                    if (loading) {
                        item { VolumesSkeleton() }
                    } else {
                        volumeItems(volumes)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(all = 8.dp)
                .align(Alignment.BottomEnd)
                .fillMaxWidth()
            ,
            horizontalArrangement = Arrangement.End,
        ) {
            FloatingActionButton(
                onClick = onFollowButtonClicked,
                containerColor = LocalPrimarySwatch.current.color,
                contentColor = LocalPrimarySwatch.current.onColor,
            ) {
                InkIcon(
                    modifier = Modifier.offset(y = 1.dp),
                    resId = followButtonIcon
                )
            }
        }
    }
}

@Composable
private fun HeaderArea(
    manga: Manga,
    firstChapter: LinkedChapter?,
    scrollFraction: Float,
    onStartReadingClicked: () -> Unit,
    onBackButtonClicked: () -> Unit,
    onMenuItemClicked: (Int) -> Unit,
) = Column(
    modifier = Modifier.fillMaxSize()
) {
    val buttonBackgroundColor = remember(scrollFraction) {
        Color.Black.copy(alpha = (1 - scrollFraction) * 0.55f)
    }
    val iconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = buttonBackgroundColor,
        disabledContainerColor = buttonBackgroundColor,
    )
    var menuOpen by remember {
        mutableStateOf(false)
    }
    Spacer(modifier = Modifier.size(permanentStatusBarSize))
    Box(modifier = Modifier.fillMaxSize()) {
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
                onClick = onBackButtonClicked,
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
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            IconButton(
                colors = iconButtonColors,
                onClick = { menuOpen = true }
            ) {
                InkIcon(resId = R.drawable.baseline_more_horiz_24)
                DetailMenu(menuOpen = menuOpen, onMenuItemClicked = onMenuItemClicked) {
                    menuOpen = false
                }
            }
        }

        TitleDetailContent(
            modifier = Modifier.alpha(
                1 - scrollFraction
                    .times(2)
                    .coerceIn(0f, 1f)
            ),
            onStartReadingClicked = onStartReadingClicked,
            manga = manga,
            firstChapter = firstChapter,
        )
    }
}

@Composable
fun DetailMenu(
    menuOpen: Boolean,
    onMenuItemClicked: (Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    DropdownMenu(expanded = menuOpen, onDismissRequest = onDismissRequest) {
        InkMenuItem(icon = R.drawable.round_add_24, text = "Add To List") {
            onMenuItemClicked(0)
        }
        InkMenuItem(icon = R.drawable.round_favorite_border_24, text = "Follow") {
            onMenuItemClicked(1)
        }
        InkMenuItem(icon = R.drawable.round_share_24, text = "Share") {
            onMenuItemClicked(2)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BoxScope.TitleDetailContent(
    modifier: Modifier = Modifier,
    firstChapter: LinkedChapter?,
    manga: Manga,
    onStartReadingClicked: () -> Unit,
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
        FlowRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            manga.tags.take(4).forEach {
                Badge(
                    containerColor = LocalContainerSwatch.current.color,
                    contentColor = LocalContainerSwatch.current.onColor,
                ) { Text(text = it) }
            }
        }

        if (firstChapter == null) return@Row
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer,
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .heightIn(min = 40.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            role = Role.Button,
                            indication = ripple(),
                            onClick = onStartReadingClicked,
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    val text = "Ch. ${firstChapter.chapter}"

                    Text(
                        "Start Reading $text",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    InkIcon(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterVertically),
                        resId = R.drawable.round_arrow_forward_24
                    )
                }
            }
        }
    }


}

@Composable
@PreviewLightDark
private fun Preview() = DefaultPreview {
    MangaDetailLayout(
        manga = StubData.manga(
            title = "A really really long title of a manga because the japanese need help",
            coverArt = "https://mangadex.org/covers/141609b6-cf86-4266-904c-6648f389cdc9/bd903567-ae7e-433a-8a8d-65ceee3fc123.jpg"
        ),
        chapters = StubData.chapterList(
            vol = { i -> if (i % 4 != 0) i % 4 else null },
            length = 16
        ),
        followed = true,
        loading = false,
        headerPlaceholderImage = R.drawable.manga_placeholder
    )
}