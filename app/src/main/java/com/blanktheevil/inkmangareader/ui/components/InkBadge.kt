package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.theme.ColorSwatch
import com.blanktheevil.inkmangareader.ui.theme.LocalPrimarySwatch
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private val hazeBorderColor = Color.White.copy(alpha = 0.2f)
private val buttonShape = RoundedCornerShape(50)
private val defaultContentPadding = Pair(4.dp, 0.dp)

@Composable
fun InkBadge(
    text: String,
    swatch: ColorSwatch = LocalPrimarySwatch.current,
    hazeState: HazeState? = null,
) = CompositionLocalProvider(
    LocalContentColor provides if (hazeState != null) Color.White else swatch.onColor,
    LocalTextStyle provides MaterialTheme.typography.labelMedium
) {
    Row(
        Modifier
            .clip(buttonShape)
            .then(
                if (hazeState != null) {
                    Modifier.hazeStyle(hazeState)
                        .background(swatch.color.copy(alpha = 0.2f))
                } else {
                    Modifier.flatStyle(swatch.color)
                }
            )
            .padding(
                horizontal = defaultContentPadding.first,
                vertical = defaultContentPadding.second,
            )
    ) {
        Text(text)
    }
}

private fun Modifier.flatStyle(
    backgroundColor: Color,
) = background(color = backgroundColor)

private fun Modifier.hazeStyle(
    hazeState: HazeState,
) = border(
    width = 1.dp,
    color = hazeBorderColor,
    shape = buttonShape,
)
    .hazeEffect(
        state = hazeState
    )

@PreviewLightDark
@Composable
private fun PreviewInkBadge() = DefaultPreview {
    val hazeState = rememberHazeState()

    Box(
        Modifier
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Max)
    ) {
        Image(
            modifier = Modifier.matchParentSize().hazeSource(hazeState),
            painter = painterResource(R.drawable.manga_placeholder),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InkBadge(text = "Hello")
            InkBadge(text = "Hello", hazeState = hazeState)
        }
    }
}
