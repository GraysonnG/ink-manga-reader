package com.blanktheevil.inkmangareader.ui.pages

import android.content.ClipData
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.emptyDataList
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.ChapterList
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.mappers.LinkedChapter
import com.blanktheevil.inkmangareader.download.DownloadManager
import com.blanktheevil.inkmangareader.helpers.rememberFalseState
import com.blanktheevil.inkmangareader.reader.ReaderManager
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.LocalNavController
import com.blanktheevil.inkmangareader.ui.components.ExpandableContentFab
import com.blanktheevil.inkmangareader.ui.components.ImageHeader
import com.blanktheevil.inkmangareader.ui.components.InkIconButton
import com.blanktheevil.inkmangareader.ui.components.InkMenuItem
import com.blanktheevil.inkmangareader.ui.components.LabeledCheckbox
import com.blanktheevil.inkmangareader.ui.components.VolumesSkeleton
import com.blanktheevil.inkmangareader.ui.components.volumeItems
import com.blanktheevil.inkmangareader.ui.permanentStatusBarSize
import com.blanktheevil.inkmangareader.ui.theme.LocalContainerSwatch
import com.blanktheevil.inkmangareader.ui.theme.LocalPrimarySwatch
import com.blanktheevil.inkmangareader.ui.theme.LocalSurfaceSwatch
import com.blanktheevil.inkmangareader.ui.theme.containerSwatch
import com.blanktheevil.inkmangareader.ui.theme.primarySwatch
import com.blanktheevil.inkmangareader.ui.theme.springQuick
import com.blanktheevil.inkmangareader.ui.theme.surfaceSwatch
import com.blanktheevil.inkmangareader.ui.theme.toColorPalette
import com.blanktheevil.inkmangareader.viewmodels.MangaDetailViewModel
import com.blanktheevil.inkmangareader.viewmodels.MangaDetailViewModel.Params
import com.blanktheevil.inkmangareader.viewmodels.MangaDetailViewModel.State
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private const val MENU_LIST = 0
private const val MENU_SHARE = 1

@Composable
fun MangaDetailPage(mangaId: String) = BasePage<MangaDetailViewModel, State, Params>(
    viewModelParams = Params(mangaId)
) { viewModel, uiState, _ ->
    val nav = LocalNavController.current
    val readerManager = koinInject<ReaderManager>()
    val palette = uiState.manga?.toColorPalette()
    val clipManager = LocalClipboardManager.current

    if (!uiState.loading) {
        val manga: Manga = uiState.manga ?: return@BasePage

        CompositionLocalProvider(
            LocalPrimarySwatch provides palette.primarySwatch,
            LocalContainerSwatch provides palette.containerSwatch,
            LocalSurfaceSwatch provides palette.surfaceSwatch,
        ) {
            MangaDetailLayout(
                manga,
                uiState.chapterFeed,
                firstChapter = uiState.firstChapter,
                loadingChapters = uiState.loadingChapters,
                followed = uiState.followed,
                onBackButtonClicked = { nav.navigateUp() },
                onMenuItemClicked = {
                    when(it) {
                        MENU_LIST -> {}
                        MENU_SHARE -> {
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
                onFollowButtonClicked = viewModel::toggleFollowManga,
                getCustomLists = viewModel::getCurrentUserLists,
                onAddToList = viewModel::addToList,
                onRemoveFromList = viewModel::removeFromList,
            )
        }
    } else {
        SkeletonLoader()
    }
}

@Composable
private fun MangaDetailLayout(
    manga: Manga,
    chapters: ChapterList,
    loadingChapters: Boolean,
    followed: Boolean,
    firstChapter: LinkedChapter? = null,
    @DrawableRes headerPlaceholderImage: Int? = null,
    onStartReadingClicked: () -> Unit = {},
    onBackButtonClicked: () -> Unit = {},
    onMenuItemClicked: (Int) -> Unit = {},
    onFollowButtonClicked: () -> Unit = {},
    getCustomLists: suspend () -> Map<String, DataList<String>> = { emptyMap() },
    onAddToList: suspend (String, String) -> Either<Unit> = { _,_ -> Either.Null() },
    onRemoveFromList: suspend (String, String) -> Either<Unit> = { _,_ -> Either.Null() },
) = Surface(
    color = LocalSurfaceSwatch.current.color,
    contentColor = LocalSurfaceSwatch.current.onColor,
) {
    val headerHeight = LocalConfiguration.current.screenHeightDp.dp.times(0.5f)
    val volumes = remember(chapters) {
        chapters.items.groupBy { it.volume ?: "No Volume" }
    }
    val userScrollEnabled = remember(loadingChapters) {
        !loadingChapters
    }

    val hazeState = rememberHazeState()

    Box {
        ImageHeader(
            initialHeight = headerHeight,
            minHeight = 64.dp,
            url = manga.coverArt,
            headerBackgroundModifier = Modifier.hazeSource(state = hazeState),
            navArea = {
                NavigationArea(
                    manga = manga,
                    scrollFraction = it,
                    hazeState = hazeState,
                    onBackButtonClicked = onBackButtonClicked,
                    onMenuItemClicked = onMenuItemClicked,
                )
            },
            headerArea = {
                HeaderArea(
                    manga = manga,
                    firstChapter = firstChapter,
                    scrollFraction = it,
                    hazeState = hazeState,
                    onBackButtonClicked = onBackButtonClicked,
                    onStartReadingClicked = onStartReadingClicked,
                    onMenuItemClicked = onMenuItemClicked,
                )
            },
            content = { nestedScrollConnection ->
                BodyArea(
                    manga = manga,
                    volumes = volumes,
                    loadingChapters = loadingChapters,
                    scrollingEnabled = userScrollEnabled,
                    nestedScrollConnection = nestedScrollConnection,
                )
            },
            placeholder = headerPlaceholderImage,
        )

        FABMenu(
            manga = manga,
            followed = followed,
            onFollowButtonClicked = onFollowButtonClicked,
            getCustomLists = getCustomLists,
            onAddToList = onAddToList,
            onRemoveFromList = onRemoveFromList,
        )
    }
}

@Composable
private fun NavigationArea(
    manga: Manga,
    scrollFraction: Float,
    hazeState: HazeState,
    onBackButtonClicked: () -> Unit,
    onMenuItemClicked: (Int) -> Unit,
) = Row(
    Modifier
        .fillMaxSize()
        .clip(RectangleShape),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
) {
    val buttonBackgroundColor = remember(scrollFraction) {
        Color.Black.copy(alpha = (scrollFraction * 0.8f).coerceIn(0.1f, 1f))
    }
    var menuOpen by rememberFalseState()

    InkIconButton(
        modifier = Modifier.background(buttonBackgroundColor),
        iconRes = R.drawable.round_arrow_back_24,
        hazeState = hazeState,
        onClick = onBackButtonClicked,
    )

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

    Box {
        InkIconButton(
            modifier = Modifier.background(buttonBackgroundColor),
            iconRes = R.drawable.baseline_more_horiz_24,
            hazeState = hazeState,
            onClick = { menuOpen = true }
        )
        DetailMenu(
            menuOpen = menuOpen,
            onMenuItemClicked = onMenuItemClicked,
            onDismissRequest = { menuOpen = false }
        )
    }
}

@Composable
private fun BodyArea(
    manga: Manga,
    volumes: Map<String, List<Chapter>>,
    loadingChapters: Boolean,
    scrollingEnabled: Boolean,
    nestedScrollConnection: NestedScrollConnection,
) {
    val downloadManager: DownloadManager = koinInject()

    CompositionLocalProvider(
        LocalContentColor provides LocalSurfaceSwatch.current.onColor
    ) {
        val chapterListState = remember(volumes, loadingChapters) {
            when {
                loadingChapters -> ChapterLoadingState.Loading
                !loadingChapters && volumes.isNotEmpty() -> ChapterLoadingState.Chapters
                else -> ChapterLoadingState.Empty
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            userScrollEnabled = scrollingEnabled
        ) {
            item(key = "description") {
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

            when (chapterListState) {
                ChapterLoadingState.Loading -> item(key = "loading") { VolumesSkeleton() }
                ChapterLoadingState.Chapters -> volumeItems(volumes, downloadManager)
                ChapterLoadingState.Empty -> item(key = "empty") { NoChaptersDialog() }
            }
        }
    }
}

@Composable
private fun HeaderArea(
    manga: Manga,
    firstChapter: LinkedChapter?,
    scrollFraction: Float,
    hazeState: HazeState,
    onStartReadingClicked: () -> Unit,
    onBackButtonClicked: () -> Unit,
    onMenuItemClicked: (Int) -> Unit,
) = Column(
    modifier = Modifier.fillMaxSize()
) {
    Spacer(modifier = Modifier.size(permanentStatusBarSize))
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 4.dp)
                .clip(RectangleShape),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

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
fun BoxScope.FABMenu(
    manga: Manga,
    followed: Boolean,
    onFollowButtonClicked: () -> Unit,
    onAddToList: suspend (String, String) -> Either<Unit>,
    onRemoveFromList: suspend (String, String) -> Either<Unit>,
    getCustomLists: suspend () -> Map<String, DataList<String>>
) = Row(
    modifier = Modifier
        .padding(all = 8.dp)
        .align(Alignment.BottomEnd)
        .fillMaxWidth()
    ,
    horizontalArrangement = Arrangement.End,
) {
    var addToListFabExpanded by rememberFalseState()
    val followButtonIcon = remember(followed) {
        if (followed) {
            R.drawable.round_favorite_24
        } else {
            R.drawable.round_favorite_border_24
        }
    }
    var loadingCustomLists by rememberFalseState()
    var customLists: Map<String, DataList<String>> by remember {
        mutableStateOf(emptyMap())
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(addToListFabExpanded) {
        if (addToListFabExpanded && customLists.isEmpty()) {
            loadingCustomLists = true
            customLists = getCustomLists()
            loadingCustomLists = false
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End,
    ) {
        ExpandableContentFab(
            shouldExpand = addToListFabExpanded,
            collapsedContainerColor = LocalPrimarySwatch.current.rawColor,
            collapsedContentColor = LocalPrimarySwatch.current.rawOnColor,
            onClick = { addToListFabExpanded = !addToListFabExpanded },
            icon = R.drawable.round_library_add_24
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Add to a list")
                if (!loadingCustomLists) {
                    // TODO: Fix issue where close and reopen after adding doesnt persist the list state
                    customLists.entries.forEach { (listId, mangaIds) ->
                        var checked by remember {
                            mutableStateOf(manga.id in mangaIds.items)
                        }

                        LabeledCheckbox(
                            text = mangaIds.title ?: "Custom List",
                            checked = checked,
                            onCheckedChange = { c ->
                                coroutineScope.launch {
                                    if (c) {
                                        onAddToList(manga.id, listId)
                                    } else {
                                        onRemoveFromList(manga.id, listId)
                                    }.onSuccess {
                                        checked = c
                                        // TODO: Temporary fix this is super stupid
                                        customLists = getCustomLists()
                                    }
                                }
                            }
                        )
                    }
                } else {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

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

@Composable
fun DetailMenu(
    menuOpen: Boolean,
    onMenuItemClicked: (Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    DropdownMenu(expanded = menuOpen, onDismissRequest = onDismissRequest) {
        InkMenuItem(icon = R.drawable.round_add_24, text = "Add To List") {
            onMenuItemClicked(MENU_LIST)
        }
        InkMenuItem(icon = R.drawable.round_share_24, text = "Share") {
            onMenuItemClicked(MENU_SHARE)
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
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(LocalPrimarySwatch.current.color)
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
                    color = LocalPrimarySwatch.current.onColor
                )
                InkIcon(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.CenterVertically),
                    resId = R.drawable.round_arrow_forward_24,
                    tint = LocalPrimarySwatch.current.onColor
                )
            }
        }
    }
}

@Composable
private fun LazyItemScope.NoChaptersDialog(
    color: Color = LocalPrimarySwatch.current.color
) = Row (
    modifier = Modifier
        .animateItem(fadeInSpec = springQuick())
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(color.copy(alpha = 0.1f))
        .border(1.dp, color, RoundedCornerShape(8.dp))
        .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
) {
    val text = stringResource(R.string.manga_detail_no_chapters)

    InkIcon(
        tint = color,
        modifier = Modifier.size(24.dp),
        resId = R.drawable.round_warning_24,
    )

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
private fun SkeletonLoader() = Column(
    modifier = Modifier.fillMaxSize(),
) {
    val color = LocalContainerSwatch.current.color
    val onColor = LocalContainerSwatch.current.onColor
    val headerHeight = LocalConfiguration.current.screenHeightDp.dp.times(0.5f)
    Column(modifier = Modifier
        .background(color.copy(alpha = 0.1f))
        .fillMaxWidth()
        .height(headerHeight.plus(permanentStatusBarSize))
        .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(onColor.copy(alpha = 0.1f))
            .fillMaxWidth()
            .height(32.dp)
        )
        Box(modifier = Modifier
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(onColor.copy(alpha = 0.1f))
            .fillMaxWidth(0.5f)
            .height(32.dp)
        )
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .width(56.dp)
                        .height(14.dp)
                )
            }
        }
    }

    LazyColumn(
        userScrollEnabled = false
    ) { item { VolumesSkeleton() } }
}

private enum class ChapterLoadingState {
    Loading,
    Empty,
    Chapters,
}

@Composable
@PreviewLightDark
private fun Preview() = DefaultPreview {
    MangaDetailLayout(
        manga = StubData.manga(
            title = "A really really long title of a manga because the japanese need help",
            coverArt = "https://mangadex.org/covers/141609b6-cf86-4266-904c-6648f389cdc9/bd903567-ae7e-433a-8a8d-65ceee3fc123.jpg"
        ),
        chapters = StubData.chapterList("h", length = 11, vol = {
            it % 4
        }),
        followed = true,
        loadingChapters = false,
        headerPlaceholderImage = R.drawable.manga_placeholder
    )
}

@Composable
@PreviewLightDark
private fun PreviewSkeleton() = DefaultPreview {
    SkeletonLoader()
}