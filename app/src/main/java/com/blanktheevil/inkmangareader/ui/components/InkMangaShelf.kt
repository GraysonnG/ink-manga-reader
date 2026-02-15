package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.helpers.mutableStateOfFalse
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.Crossfade
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.InkImage
import com.blanktheevil.inkmangareader.ui.theme.LocalContainerSwatch
import com.blanktheevil.inkmangareader.ui.toAsyncPainterImage
import kotlinx.coroutines.delay

@Composable
fun InkMangaShelf(
    list: MangaList,
    placeholderResId: Int? = null,
) = Column(
    Modifier
        .padding(horizontal = 8.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(LocalContainerSwatch.current.color)
        .padding(8.dp)
) {
    var toggled by remember { mutableStateOfFalse() }

    LaunchedEffect(toggled) {
        if (toggled) {
            delay(10000)
            toggled = false
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Hello"
        )

        IconButton(
            onClick = {}
        ) {
            InkIcon(resId = R.drawable.round_arrow_forward_24)
        }
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = toggled
        ) {
            if (it) {
                LazyRow(
                    state = rememberLazyListState(

                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = list.items) { manga ->
                        val state = rememberSharedContentState(manga.id)
                        InkMangaCard(
                            manga,
                            modifier = Modifier.sharedElement(
                                sharedContentState = state,
                                animatedVisibilityScope = this@AnimatedContent,
                            ),
                            mangaCardType = MangaCardType.SQUARE,
                            onClick = {},
                            placeholderRes = placeholderResId,
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .horizontalScroll(rememberScrollState())
                        .clickable() {
                            toggled = true
                        }

                    ,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    list.items.forEach { manga ->
                        val state = rememberSharedContentState(manga.id)
                        val coverImage = manga.coverArt.toAsyncPainterImage(
                            placeholder = placeholderResId,
                            crossfade = Crossfade.SHORT
                        )

                        InkImage(
                            modifier = Modifier
                                .height(75.dp)
                                .aspectRatio(1f)
                                .sharedElement(
                                    sharedContentState = state,
                                    this@AnimatedContent
                                ),
                            painter = coverImage,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview() = DefaultPreview {
    Box(Modifier.fillMaxSize()) {
        InkMangaShelf(
            StubData.mangaList(title = "blah", length = 10),
            placeholderResId = R.drawable.manga_placeholder
        )
    }
}