package com.blanktheevil.inkmangareader.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

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

object Transitions {
    val slideIn: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? = {
        slideInHorizontally { it } + fadeIn()
    }

    val slideOut: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? = {
        slideOutHorizontally { it } + fadeOut()
    }
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController")
}