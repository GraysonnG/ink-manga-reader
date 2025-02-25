package com.blanktheevil.inkmangareader.ui.pages

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.navigation.navigateToMangaDetail
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.LocalNavController
import com.blanktheevil.inkmangareader.ui.components.ImageHeader
import com.blanktheevil.inkmangareader.ui.components.MangaCard
import com.blanktheevil.inkmangareader.ui.permanentStatusBarSize
import com.blanktheevil.inkmangareader.viewmodels.MangaListViewModel
import com.blanktheevil.inkmangareader.viewmodels.MangaListViewModel.State
import com.blanktheevil.inkmangareader.viewmodels.MangaListViewModel.Params


private const val HEADER_HEIGHT = 0.4f

@Composable
fun MangaListPage(typeOrId: String) = BasePage<MangaListViewModel, State, Params>(
    viewModelParams = Params(typeOrId = typeOrId)
) { vm, uiState, _ ->
    val nav = LocalNavController.current

    MangaListPageLayout(
        uiState = uiState,
        onEditClicked = {},
        onBackButtonClicked = {
            nav.popBackStack()
        },
        onMangaCardClicked = {
            nav.navigateToMangaDetail(it)
        },
        onLoadMore = vm::loadMore,
    )
}

@Composable
private fun MangaListPageLayout(
    uiState: State,
    @DrawableRes mangaPlaceholderRes: Int? = null,
    onEditClicked: () -> Unit,
    onBackButtonClicked: () -> Unit,
    onMangaCardClicked: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val state = rememberLazyGridState()
    val headerHeight = LocalConfiguration.current.screenHeightDp.dp.times(HEADER_HEIGHT)

    val reachedBottom by remember {
        derivedStateOf {
            val lastVisibleItem = state.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != 0 && lastVisibleItem?.index == (state.layoutInfo.totalItemsCount - 9)
        }
    }

    LaunchedEffect(reachedBottom) {
        if (reachedBottom && !uiState.loading && !uiState.loadingMore) {
            onLoadMore()
        }
    }

    ImageHeader(
        initialHeight = headerHeight,
        minHeight = 64.dp,
        url = uiState.list.items.getOrNull(0)?.coverArt,
        placeholder = mangaPlaceholderRes,
        headerArea = { HeaderArea(
            uiState = uiState,
            scrollFraction = it,
            onEditClicked = onEditClicked,
            onBackButtonClicked = onBackButtonClicked,
        ) }
    ) { nsc ->
        LazyVerticalGrid(
            state = state,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .nestedScroll(nsc)
            ,
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            contentPadding = PaddingValues(
                vertical = 8.dp
            )
        ) {
            itemsIndexed(
                uiState.list.items,
                key = { index, item -> item.id + index }
            ) { index, item ->
                MangaCard(
                    modifier = Modifier.fillMaxSize()
                        .animateItem(
                            fadeInSpec = tween(300, index.div(2) * 50)
                        ),
                    manga = item,
                    placeholderRes = mangaPlaceholderRes,
                ) {
                    onMangaCardClicked(item.id)
                }
            }
        }
    }
}

@Composable
private fun HeaderArea(
    uiState: State,
    scrollFraction: Float,
    onEditClicked: () -> Unit,
    onBackButtonClicked: () -> Unit,
) = Column(
    modifier = Modifier.fillMaxSize()
) {
    Spacer(modifier = Modifier.size(permanentStatusBarSize))
    Box(modifier = Modifier.fillMaxSize()) {
        val title = if (uiState.loading) {
            ""
        } else {
            uiState.list.title ?: "Custom List"
        }

        TitleSimpleContent(
            scrollFraction = scrollFraction,
            title = title,
            onBackButtonClicked = onBackButtonClicked,
            onEditClicked = onEditClicked,
        )

        if (uiState.loading) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .padding(bottom = 8.dp)
                    .align(Alignment.BottomStart)
                    .clip(RoundedCornerShape(4.dp))
                    .background(LocalContentColor.current.copy(alpha = 0.3f))
                    .size(
                        width = 200.dp,
                        height = 40.dp,
                    )
            )
        } else {
            TitleDetailContent(
                modifier = Modifier.alpha(
                    1 - scrollFraction
                        .times(2)
                        .coerceIn(0f, 1f)
                ),
                title = title,
                username = uiState.list.extras?.get("username")
            )
        }
    }
}

@Composable
private fun BoxScope.TitleDetailContent(
    title: String,
    username: String?,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier
        .padding(8.dp)
        .padding(bottom = 8.dp)
        .align(Alignment.BottomStart),
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineLarge
    )

    if (!username.isNullOrEmpty()) {
        Text(
            text = "By: $username",
            color = LocalContentColor.current.copy(alpha = 0.70f),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun TitleSimpleContent(
    scrollFraction: Float,
    title: String,
    onBackButtonClicked: () -> Unit,
    onEditClicked: () -> Unit,
) = Row (
    modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .padding(horizontal = 8.dp)
        .clip(RectangleShape),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Absolute.SpaceBetween
) {
    val buttonBackgroundColor = remember(scrollFraction) {
        Color.Black.copy(alpha = (1 - scrollFraction) * 0.55f)
    }
    val iconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = buttonBackgroundColor,
        disabledContainerColor = buttonBackgroundColor,
    )

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
        text = title,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )

    IconButton(
        colors = iconButtonColors,
        onClick = onEditClicked,
    ) {
        InkIcon(resId = R.drawable.rounded_edit_24)
    }
}


@PreviewLightDark
@Composable
private fun Preview() = DefaultPreview {

    MangaListPageLayout(
        uiState = State(
            list = StubData.mangaList(
                length = 12,
                extras = mapOf("username" to "Test User")
            ),
            loading = false,
        ),
        mangaPlaceholderRes = R.drawable.manga_placeholder,
        onBackButtonClicked = {},
        onEditClicked = {},
        onMangaCardClicked = {},
        onLoadMore = {},
    )
}