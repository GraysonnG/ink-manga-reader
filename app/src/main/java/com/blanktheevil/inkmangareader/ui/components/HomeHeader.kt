package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.statusBarSize
import kotlinx.coroutines.delay

private const val BREAKPOINT = 500
private const val ANIMATION_DURATION = 500

@Composable
fun HomeHeader(
    scrollOffset: Int,
    authenticated: Boolean,
    authenticatedInitialState: Boolean = false,
    onSearchClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
) {
    var auth by remember {
        mutableStateOf(authenticatedInitialState)
    }

    LaunchedEffect(authenticated) {
        delay(500)
        auth = authenticated
    }

    val buttonColor by animateColorAsState(
        targetValue = if (scrollOffset <= BREAKPOINT) Color.Black.copy(alpha = 0.6f) else Color.Transparent,
        label = "buttonColor",
        animationSpec = tween(ANIMATION_DURATION)
    )
    val headerColor by animateColorAsState(
        targetValue = if (scrollOffset <= BREAKPOINT) Color.Transparent else Color.Black.copy(alpha = 0.8f),
        label = "headerColor",
        animationSpec = tween(ANIMATION_DURATION)
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
            Row(
                Modifier
                    .weight(1f)
                    .padding(start = 8.dp),

            ) {
                Row(
                    Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable(
                            role = Role.Button
                        ) { }
                        .background(buttonColor)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    InkIcon(resId = R.drawable.round_person_24)
                    AnimatedVisibility(
                        visible = auth,
                        enter = expandHorizontally(),
                        exit = shrinkHorizontally(),
                    ) {
                        Text(
                            modifier = Modifier.padding(end = 8.dp),
                            text = "Blank The Evil",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

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
        HomeHeader(scrollOffset = 0, authenticated = true, authenticatedInitialState = true)
        HomeHeader(scrollOffset = 500, authenticated = true)
    }
}