package com.blanktheevil.inkmangareader.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.ui.Crossfade
import com.blanktheevil.inkmangareader.ui.Gradients
import com.blanktheevil.inkmangareader.ui.navigationBarSize
import com.blanktheevil.inkmangareader.ui.permanentStatusBarSize
import com.blanktheevil.inkmangareader.ui.toAsyncPainterImage
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
inline fun ImageHeader(
    modifier: Modifier = Modifier,
    initialHeight: Dp,
    minHeight: Dp,
    url: String?,
    @DrawableRes placeholder: Int? = null,
    headerBackgroundModifier: Modifier = Modifier,
    noinline headerArea: @Composable BoxScope.(scrollFraction: Float) -> Unit,
    noinline navArea: @Composable RowScope.(scrollFraction: Float) -> Unit = {},
    crossinline onCollapsed: () -> Unit = {},
    crossinline onExpanded: () -> Unit = {},
    noinline content: @Composable (NestedScrollConnection) -> Unit,
) {
    val statusBarSize = permanentStatusBarSize
    val density = LocalDensity.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )

    val expandedHeight = remember {
        initialHeight.plus(statusBarSize)
    }
    val collapsedHeight = remember {
        minHeight.plus(statusBarSize)
    }
    val expandedHeightPx = remember(expandedHeight) {
        density.run { expandedHeight.toPx() }
    }
    val collapsedHeightPx = remember(collapsedHeight) {
        density.run { collapsedHeight.toPx() }
    }
    val boxAlphaMax = remember { 1f }
    val boxAlphaMin = remember { 0f }

    SideEffect {
        if (scrollBehavior.state.heightOffsetLimit != collapsedHeightPx - expandedHeightPx) {
            scrollBehavior.state.heightOffsetLimit = collapsedHeightPx - expandedHeightPx
        }
    }

    LaunchedEffect(scrollBehavior.state.collapsedFraction) {
        when (scrollBehavior.state.collapsedFraction) {
            1f -> onCollapsed()
            0f -> onExpanded()
        }
    }

    val coverImage = url.toAsyncPainterImage(
        crossfade = Crossfade.SHORT,
        placeholder = placeholder,
    )

    val height = remember(scrollBehavior.state.collapsedFraction) {
        collapsedHeight + (expandedHeight - collapsedHeight)
            .times(1 - scrollBehavior.state.collapsedFraction)
    }

    val boxAlpha = remember(scrollBehavior.state.collapsedFraction) {
        boxAlphaMin + (boxAlphaMax - boxAlphaMin)
            .times(scrollBehavior.state.collapsedFraction)
    }

    val scrollFraction by rememberUpdatedState(scrollBehavior.state.collapsedFraction)
    val nestedScrollConnection = remember { scrollBehavior.nestedScrollConnection }

    Column(
        modifier = modifier,
    ) {
        Box(
            Modifier
                .height(height)
                .heightIn(min = collapsedHeight, max = expandedHeight)
        ) {
            val headerAreaOverlayAlpha = remember(boxAlpha) {
                1 - boxAlpha
            }
            val headerAreaAlphaColor = remember(boxAlpha) {
                Color.Black.copy(alpha = boxAlpha * 0.8f)
            }

            Box(
                modifier = headerBackgroundModifier
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
                        .alpha(headerAreaOverlayAlpha)
                        .fillMaxSize()
                        .background(Gradients.transparentToBlack)
                )
            }

            CompositionLocalProvider(LocalContentColor provides Color.White) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(headerAreaAlphaColor)
                ) {
                    headerArea(scrollFraction)
                }
            }


            CompositionLocalProvider(
                LocalInkButtonMargin provides PaddingValues(0.dp)
            ) {
                Row(
                    Modifier
                        .padding(top = statusBarSize)
                        .fillMaxWidth()
                        .height(minHeight)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    navArea(scrollFraction)
                }
            }
        }
        content(nestedScrollConnection)
    }
}