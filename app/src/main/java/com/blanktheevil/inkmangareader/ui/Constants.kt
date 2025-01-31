package com.blanktheevil.inkmangareader.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Gradients {
    val transparentToBlack = Brush.linearGradient(
        colors = listOf(
            Color.Black.copy(alpha = 0f),
            Color.Black.copy(alpha = 0.9f),
        ),
        start = Offset.Zero,
        end = Offset.Infinite.copy(x = 0f),
    )
}