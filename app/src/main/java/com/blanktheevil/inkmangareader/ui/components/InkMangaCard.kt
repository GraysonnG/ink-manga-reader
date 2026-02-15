package com.blanktheevil.inkmangareader.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.Crossfade
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkImage
import com.blanktheevil.inkmangareader.ui.skeletonBackground
import com.blanktheevil.inkmangareader.ui.theme.LocalSurfaceSwatch
import com.blanktheevil.inkmangareader.ui.toAsyncPainterImage
import com.valentinilk.shimmer.shimmer

enum class MangaCardType {
    TALL,
    SQUARE,
}

private data class InkMangaCardParams(
    val manga: Manga,
    val modifier: Modifier = Modifier,
    val clampHeight: Boolean = true,
    @DrawableRes val placeholderRes: Int? = null,
    val subtitle: (@Composable () -> Unit)? = null,
    val onClick: () -> Unit,
)

private val cornerShape = RoundedCornerShape(8.dp)
private val borderColor @Composable get() = LocalSurfaceSwatch.current.rawOnColor.copy(alpha = 0.25f)
private val pillShape = RoundedCornerShape(20.dp)

@Composable
fun InkMangaCard(
    manga: Manga,
    mangaCardType: MangaCardType,
    modifier: Modifier = Modifier,
    clampHeight: Boolean = true,
    subtitle: (@Composable () -> Unit)? = null,
    @DrawableRes placeholderRes: Int? = null,
    onClick: () -> Unit,
) {
    val params = InkMangaCardParams(
        manga = manga,
        modifier = modifier,
        clampHeight = clampHeight,
        placeholderRes = placeholderRes,
        subtitle = subtitle,
        onClick = onClick,
    )

    when (mangaCardType) {
        MangaCardType.TALL -> TallMangaCard(params)
        MangaCardType.SQUARE -> SquareMangaCard(params)
    }
}

@Composable
fun InkMangaCardSkeleton(
    mangaCardType: MangaCardType,
    modifier: Modifier = Modifier.shimmer(),
) {
    when (mangaCardType) {
        MangaCardType.TALL -> TallMangaCardSkeleton(
            modifier = modifier,
        )
        MangaCardType.SQUARE -> SquareMangaCardSkeleton(
            modifier = modifier,
        )
    }
}

@Composable
private fun TallMangaCard(
    params: InkMangaCardParams,
) = with(params) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .clip(cornerShape)
            .cardClickable(onClick = onClick)
    ) {
        CoverImage(modifier = Modifier
            .border(1.dp, borderColor, cornerShape)
            .then(if (clampHeight) Modifier.height(240.dp) else Modifier)
            .aspectRatio(9 / 13f)
            .fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = manga.title,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge
        )
        subtitle?.invoke()
    }
}

@Composable
private fun SquareMangaCard(
    params: InkMangaCardParams,
) = with(params) {
    Column(
        modifier = modifier
            .width(110.dp)
            .clip(cornerShape)
            .cardClickable(onClick = onClick)
    ) {
        CoverImage(modifier = Modifier
            .border(1.dp, borderColor, cornerShape)
            .then(if (clampHeight) Modifier.height(110.dp) else Modifier)
            .fillMaxWidth()
            .aspectRatio(1f)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = manga.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge
        )
        subtitle?.invoke()
    }
}

@Composable
private fun TallMangaCardSkeleton(
    modifier: Modifier,
) = Column(
    modifier.width(IntrinsicSize.Min)
) {
    Box(
        modifier = Modifier
            .clip(cornerShape)
            .skeletonBackground()
            .height(240.dp)
            .aspectRatio(9 / 13f)
            .fillMaxWidth()
    )
    Spacer(Modifier.size(6.dp))
    Box(
        modifier = Modifier
            .clip(pillShape)
            .skeletonBackground()
            .fillMaxWidth()
            .height(16.dp)
    )
    Spacer(Modifier.size(4.dp))
    Box(
        modifier = Modifier
            .clip(pillShape)
            .skeletonBackground()
            .fillMaxWidth(0.5f)
            .height(16.dp)
    )
    Spacer(Modifier.size(2.dp))
}

@Composable
private fun SquareMangaCardSkeleton(
    modifier: Modifier,
) = Column(
    modifier.width(110.dp)
) {
    Box(modifier = Modifier
        .clip(cornerShape)
        .skeletonBackground()
        .fillMaxWidth()
        .aspectRatio(1f)
    )
    Spacer(Modifier.size(8.dp))
    Box(
        modifier = Modifier
            .clip(pillShape)
            .skeletonBackground()
            .fillMaxWidth()
            .height(14.dp)
    )
    Spacer(Modifier.size(4.dp))
    Box(
        modifier = Modifier
            .clip(pillShape)
            .skeletonBackground()
            .fillMaxWidth(0.9f)
            .height(10.dp)
    )
    Spacer(Modifier.size(3.dp))
}

@Composable
private fun Modifier.cardClickable(
    onClick: () -> Unit,
) = this.clickable(
    interactionSource = remember {
        MutableInteractionSource()
    },
    indication = ripple(),
    role = Role.Button,
    onClick = onClick,
)

@Composable
private fun InkMangaCardParams.CoverImage(
    modifier: Modifier,
) {
    val coverImage = manga.coverArt.toAsyncPainterImage(
        placeholder = placeholderRes,
        crossfade = Crossfade.SHORT
    )

    InkImage(
        modifier = modifier,
        painter = coverImage,
        shape = cornerShape
    )
}

@Composable
@PreviewLightDark
private fun PreviewTallCard() = DefaultPreview {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InkMangaCard(
            manga = StubData.manga(),
            mangaCardType = MangaCardType.TALL,
            placeholderRes = R.drawable.manga_placeholder,
        ) { }

        InkMangaCardSkeleton(MangaCardType.TALL)
    }
}

@Composable
@PreviewLightDark
private fun PreviewSquareCard() = DefaultPreview {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InkMangaCard(
            manga = StubData.manga(),
            mangaCardType = MangaCardType.SQUARE,
            placeholderRes = R.drawable.manga_placeholder,
            subtitle = {
                Text(
                    text = "hello",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) { }

        InkMangaCardSkeleton(
            mangaCardType = MangaCardType.SQUARE
        )
    }
}
