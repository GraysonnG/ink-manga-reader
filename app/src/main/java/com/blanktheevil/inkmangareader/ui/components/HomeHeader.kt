package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.statusBarSize

@Composable
fun HomeHeader(
    scrollOffset: Int,
    onSearchClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
) {
    val buttonColor by animateColorAsState(
        targetValue = if (scrollOffset == 0) Color.Black.copy(alpha = 0.6f) else Color.Transparent,
        label = "buttonColor",
        animationSpec = tween(1000)
    )
    val headerColor by animateColorAsState(
        targetValue = if (scrollOffset == 0) Color.Transparent else Color.Black.copy(alpha = 0.8f),
        label = "headerColor",
        animationSpec = tween(1000)
    )


    val iconButtonColors = IconButtonDefaults.iconButtonColors(
        contentColor = Color.White,
        containerColor = buttonColor,
        disabledContainerColor = buttonColor,
    )

    CompositionLocalProvider(
        LocalContentColor provides Color.White
    ) {
        Row(
            modifier = Modifier
                .zIndex(1000f)
                .fillMaxWidth()
                .background(headerColor)
                .padding(top = statusBarSize),
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(
                onClick = { },
                colors = iconButtonColors,
            ) {
                InkIcon(resId = R.drawable.round_settings_24)
            }
            IconButton(
                onClick = onSearchClicked,
                colors = iconButtonColors,
            ) {
                InkIcon(resId = R.drawable.round_search_24)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() = DefaultPreview {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HomeHeader(scrollOffset = 0)
        HomeHeader(scrollOffset = 1)
    }
}