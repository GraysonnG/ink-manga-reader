package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.toAsyncPainterImage

@Composable
fun MangaCard(manga: Manga) {
    Column (
        Modifier.clip(RoundedCornerShape(8.dp))
            .width(IntrinsicSize.Min)
    ) {
        val coverImage = manga.coverArt.toAsyncPainterImage(
            crossfade = true
        )
        Image(
            modifier = Modifier.height(240.dp).aspectRatio(9 / 13f).fillMaxWidth(),
            painter = coverImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        Text(
            text = manga.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
fun MangaCardSkeleton() {

}

@Composable
@PreviewLightDark
private fun Preview() = DefaultPreview {
    MangaCard(manga = StubData.manga())
}