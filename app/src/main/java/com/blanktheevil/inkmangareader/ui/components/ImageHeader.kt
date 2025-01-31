package com.blanktheevil.inkmangareader.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.blanktheevil.inkmangareader.ui.Gradients
import com.blanktheevil.inkmangareader.ui.toAsyncPainterImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageHeader(
    modifier: Modifier = Modifier,
    initialHeight: Dp,
    minHeight: Dp,
    url: String?,
    @DrawableRes placeholder: Int? = null,
    headerArea: @Composable BoxScope.(scrollFraction: Float) -> Unit,
    content: @Composable (NestedScrollConnection) -> Unit
) {
    val statusBarSize = with (LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val expandedHeight = initialHeight.plus(statusBarSize)
    val collapsedHeight = minHeight.plus(statusBarSize)
    val expandedHeightPx: Float
    val collapsedHeightPx: Float
    val boxAlphaMax = 1f
    val boxAlphaMin = 0f

    LocalDensity.current.run {
        expandedHeightPx = expandedHeight.toPx()
        collapsedHeightPx = collapsedHeight.toPx()
    }

    SideEffect {
        if (scrollBehavior.state.heightOffsetLimit != collapsedHeightPx - expandedHeightPx) {
            scrollBehavior.state.heightOffsetLimit = collapsedHeightPx - expandedHeightPx
        }
    }

    val coverImage = url.toAsyncPainterImage(
        crossfade = true,
        placeholder = placeholder,
    )

    val height = remember(scrollBehavior.state.collapsedFraction) {
        collapsedHeight + (expandedHeight - collapsedHeight).times(1 - scrollBehavior.state.collapsedFraction)
    }

    val boxAlpha = remember(scrollBehavior.state.collapsedFraction) {
        boxAlphaMin + (boxAlphaMax - boxAlphaMin).times(scrollBehavior.state.collapsedFraction)
    }

    Column(
        modifier = modifier,
    ) {
        Box(
            Modifier
                .height(height)
                .heightIn(min = collapsedHeight, max = expandedHeight)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = coverImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Box(
                Modifier
                    .alpha(1 - boxAlpha)
                    .fillMaxSize()
                    .background(Gradients.transparentToBlack)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides Color.White
                ) {
                    headerArea(scrollBehavior.state.collapsedFraction)
                }
            }

            Box(
                Modifier
                    .alpha(boxAlpha * 0.8f)
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides Color.White
                ) {
                    headerArea(scrollBehavior.state.collapsedFraction)
                }
            }
        }

        content(scrollBehavior.nestedScrollConnection)
    }
}