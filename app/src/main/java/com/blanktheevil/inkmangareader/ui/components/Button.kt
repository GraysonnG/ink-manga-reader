package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
private fun buttonColor(): Color = MaterialTheme.colorScheme.primaryContainer
@Composable
private fun onButtonColor(): Color = MaterialTheme.colorScheme.onPrimaryContainer

@Composable
fun SimpleInkButton(
    onClick: () -> Unit,
    title: @Composable RowScope.() -> Unit,
    color: Color = buttonColor(),
    background: (@Composable BoxScope.() -> Unit)? = null,
    trailingIcon: (@Composable RowScope.() -> Unit)? = null,
) = CompositionLocalProvider(LocalContentColor provides onButtonColor()){
    Box(modifier = Modifier
        .height(IntrinsicSize.Min)
        .fillMaxWidth()
        .clip(RoundedCornerShape(50))
        .background(color)
        .heightIn(min = 40.dp)
    ) {
        background?.invoke(this)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        role = Role.Button,
                        indication = ripple(),
                        onClick = onClick,
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                title()
            }
            trailingIcon?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    trailingIcon(this)
                }
            }
        }
    }
}