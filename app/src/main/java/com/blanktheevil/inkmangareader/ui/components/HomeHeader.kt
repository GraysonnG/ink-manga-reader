package com.blanktheevil.inkmangareader.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.blanktheevil.inkmangareader.ui.LocalHazeState
import com.blanktheevil.inkmangareader.ui.statusBarSize
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.delay

private val buttonBorderColor = Color.White.copy(alpha = 0.2f)

@Composable
fun HomeHeader(
    scrollFraction: Float,
    authenticated: Boolean,
    hazeState: HazeState,
    authenticatedInitialState: Boolean = false,
    onSearchClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onAccountClicked: () -> Unit = {},
) {
    var auth by remember {
        mutableStateOf(authenticatedInitialState)
    }

    LaunchedEffect(authenticated) {
        delay(500)
        auth = authenticated
    }

    val buttonBackgroundColor = remember(scrollFraction) {
        Color.Black.copy(alpha = (scrollFraction * .8f).coerceIn(0.1f, 1f))
    }

    CompositionLocalProvider(
        LocalContentColor provides Color.White
    ) {
        Row(
            modifier = Modifier
                .zIndex(1000f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                Modifier.weight(1f)
            ) {
                InkButton(
                    modifier = Modifier.background(buttonBackgroundColor),
                    leadingIconRes = R.drawable.round_person_24,
                    hazeState = hazeState,
                    contentPadding = PaddingValues(8.dp),
                    onClick = onAccountClicked,
                ) {
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

            InkIconButton(
                modifier = Modifier.background(buttonBackgroundColor),
                iconRes = R.drawable.round_settings_24,
                hazeState = hazeState,
                onClick = onSettingsClicked,
            )

            InkIconButton(
                modifier = Modifier.background(buttonBackgroundColor),
                iconRes = R.drawable.round_search_24,
                hazeState = hazeState,
                onClick = onSearchClicked,
            )
        }
    }
}

@Composable
private fun HeaderIconButton(
    @DrawableRes iconRes: Int,
    hazeState: HazeState,
    buttonBackgroundColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(50.dp))
            .border(
                width = 1.dp,
                color = buttonBorderColor,
                shape = RoundedCornerShape(50.dp)
            )
            .hazeEffect(
                state = hazeState,
                style = HazeStyle.Unspecified.copy(
                    backgroundColor = Color.Black.copy(alpha = 0.8f)
                )
            )
            .background(buttonBackgroundColor)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .padding(8.dp)

    ) {
        InkIcon(resId = iconRes)
    }
}

@PreviewLightDark
@Composable
private fun Preview() = DefaultPreview {
    val hazeState = LocalHazeState.current

    CompositionLocalProvider(
        LocalInkButtonMargin provides PaddingValues(0.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeHeader(scrollFraction = 0f, authenticated = true, authenticatedInitialState = true, hazeState = hazeState)
            HomeHeader(scrollFraction = 1f, authenticated = true, hazeState = hazeState)
        }
    }
}