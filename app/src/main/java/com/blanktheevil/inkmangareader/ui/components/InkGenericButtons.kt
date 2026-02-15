package com.blanktheevil.inkmangareader.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.theme.ColorSwatch
import com.blanktheevil.inkmangareader.ui.theme.LocalPrimarySwatch
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private val hazeBorderColor = Color.White.copy(alpha = 0.2f)
private val buttonShape = RoundedCornerShape(50)
private val defaultContentPadding = Pair(16.dp, 8.dp)

val LocalInkButtonMargin = compositionLocalOf {
    PaddingValues(4.dp)
}

@Composable
fun InkButton(
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconRes: Int? = null,
    @DrawableRes trailingIconRes: Int? = null,
    margin: PaddingValues = LocalInkButtonMargin.current,
    contentPadding: PaddingValues = generatePaddingValues(
        iconStart = leadingIconRes != null,
        iconEnd = trailingIconRes != null,
    ),
    swatch: ColorSwatch = LocalPrimarySwatch.current,
    hazeState: HazeState? = null,
    onClick: () -> Unit,
    text: @Composable RowScope.() -> Unit,
) = CompositionLocalProvider(
    LocalContentColor provides if (hazeState != null) Color.White else swatch.onColor,
    LocalTextStyle provides MaterialTheme.typography.bodySmall,
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Row(
        Modifier.padding(margin)
            .clip(buttonShape)
            .then(
                if (hazeState != null) {
                    Modifier.hazeStyle(hazeState)
                } else {
                    Modifier.flatStyle(swatch.color)
                }
            )
            .clickable(
                role = Role.Button,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = ripple()
            )
            .then(modifier)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        leadingIconRes?.let { InkIcon(resId = it) }
        text()
        trailingIconRes?.let { InkIcon(resId = it) }
    }
}

@Composable
fun InkIconButton(
    modifier: Modifier = Modifier,
    iconRes: Int,
    margin: PaddingValues = LocalInkButtonMargin.current,
    swatch: ColorSwatch = LocalPrimarySwatch.current,
    hazeState: HazeState? = null,
    onClick: () -> Unit,
) = CompositionLocalProvider(
    LocalContentColor provides if (hazeState != null) Color.White else swatch.onColor,
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Box(
        Modifier.padding(margin)
            .clip(CircleShape)
            .then(
                if (hazeState != null) {
                    Modifier.hazeStyle(hazeState)
                } else {
                    Modifier.flatStyle(swatch.color)
                }
            )
            .clickable(
                role = Role.Button,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = ripple()
            )
            .then(modifier)
            .padding(8.dp)
    ) {
        InkIcon(
            modifier = Modifier.size(24.dp),
            resId = iconRes,
        )
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

private fun generatePaddingValues(
    iconStart: Boolean,
    iconEnd: Boolean,
) = PaddingValues(
    start = if (iconStart) defaultContentPadding.second else defaultContentPadding.first,
    end = if (iconEnd) defaultContentPadding.second else defaultContentPadding.first,
    top = defaultContentPadding.second,
    bottom = defaultContentPadding.second
)

@Composable
@PreviewLightDark
fun PreviewInkButton() = DefaultPreview {
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
            Row {
                InkButton(
                    trailingIconRes = R.drawable.round_arrow_forward_24,
                    onClick = {}
                ) {
                    Text("Hello World")
                }

                InkIconButton(
                    iconRes = R.drawable.round_search_24,
                ) {  }
            }
            Row {
                InkButton(
                    leadingIconRes = R.drawable.round_settings_24,
                    hazeState = hazeState,
                    onClick = {}
                ) {
                    Text("Hello World")
                }

                InkIconButton(
                    iconRes = R.drawable.round_search_24,
                    hazeState = hazeState,
                ) {  }
            }
        }
    }
}