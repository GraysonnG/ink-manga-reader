package com.blanktheevil.inkmangareader.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.theme.LocalContainerSwatch

@Composable
fun ExpandableContentFab(
    shouldExpand: Boolean,
    onClick: () -> Unit,
    collapsedContainerColor: Color = LocalContainerSwatch.current.rawColor,
    collapsedContentColor: Color = LocalContainerSwatch.current.rawOnColor,
    @DrawableRes icon: Int,
    content: @Composable () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    var showContent by remember { mutableStateOf(false) }

    val boxWidth by animateFloatAsState(
        targetValue = if (shouldExpand) screenWidthDp.toFloat().minus(16f) else 56f,
        animationSpec = tween(150),
        finishedListener = {
            showContent = it == screenWidthDp.toFloat().minus(16f)
        },
        label = "boxWidth"
    )

    val containerColor by animateColorAsState(
        targetValue = if (shouldExpand) MaterialTheme.colorScheme.surfaceVariant
        else collapsedContainerColor,
        animationSpec = tween(500),
        label = "containerColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (shouldExpand) MaterialTheme.colorScheme.onSurfaceVariant
        else collapsedContentColor,
        label = "contentColor",
    )

    val contentEnter = remember {
        expandVertically(
            tween(115)
        ) + fadeIn()
    }

    val contentExit = remember {
        ExitTransition.None
    }

    val buttonIcon = @Composable {
        InkIcon(
            resId = if (showContent) R.drawable.round_close_24 else icon,
            tint = contentColor,
        )
    }


    LaunchedEffect(shouldExpand) {
        if (!shouldExpand) showContent = false
    }

    Box(
        Modifier.height(IntrinsicSize.Min),
        contentAlignment = Alignment.BottomEnd
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            contentColor = contentColor,
            color = containerColor,
            shadowElevation = 4.dp,
            modifier = Modifier
                .width(boxWidth.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 56.dp)
            ) {
                AnimatedVisibility(
                    label = "content",
                    visible = showContent,
                    enter = contentEnter,
                    exit = contentExit
                ) {
                    content()
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(
                0.dp, 0.dp, 0.dp, 0.dp
            ),
            content = buttonIcon,
            onClick = {
                onClick()
                if (showContent) showContent = false
            }
        )
    }
}