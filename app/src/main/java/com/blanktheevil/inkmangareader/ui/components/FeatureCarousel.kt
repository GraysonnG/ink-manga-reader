package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.Gradients
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.toAsyncPainterImage

@Composable
fun FeatureCarousel(
    mangaList: MangaList,
    onItemClicked: (mangaId: String) -> Unit = {}
) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp

        Box(
            modifier = Modifier
                .height(screenHeight.div(1.75f))
        ) {
            if (mangaList.items.isEmpty()) return@Box

            val pageCount = remember(mangaList) {
                mangaList.items.size * 10000
            }

            val pagerState = rememberPagerState(
                initialPage = pageCount / 2,
                pageCount = { pageCount }
            )

            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                beyondViewportPageCount = 1,
            ) {
                val index = remember {
                    it % mangaList.items.size
                }
                FeatureItem(
                    manga = mangaList.items[index],
                    onItemClicked = onItemClicked
                )
            }

            Row(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
            ) {
                val style = MaterialTheme.typography.labelMedium
                val color = Color.White
                val index = pagerState.currentPage % mangaList.items.size

                Text(
                    text = "${index + 1}",
                    style = style,
                    color = color,
                )
                Text(
                    text = "/${pagerState.pageCount / 10000}",
                    style = style,
                    color = color.copy(0.5f),
                )
            }
        }
}

@Composable
private inline fun FeatureItem(
    manga: Manga,
    crossinline onItemClicked: (mangaId: String) -> Unit,
) {
    val coverImage = manga.coverArt
        .toAsyncPainterImage(crossfade = true)

    CompositionLocalProvider(
        LocalContentColor provides Color.White,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Image(
                painter = coverImage,
                modifier = Modifier
                    .fillMaxSize(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
            )

            Box(
                modifier = Modifier
                    .heightIn(min = 10.dp)
                    .fillMaxSize()
                    .background(Gradients.transparentToBlack)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = manga.title,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 2,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    manga.tags.take(4).forEach {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text(text = it)
                        }
                    }
                }
                Text(
                    text = manga.description,
                    maxLines = 3,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 14.sp
                )
                Button(onClick = { onItemClicked(manga.id) }) {
                    InkIcon(resId = R.drawable.read_24)
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = "Read")
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview() = DefaultPreview {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FeatureCarousel(StubData.mangaList(length = 4))
    }
}