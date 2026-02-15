package com.blanktheevil.inkmangareader.ui

import android.view.Window
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
import com.blanktheevil.inkmangareader.ui.theme.springQuick
import dev.chrisbanes.haze.HazeState

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
        slideInHorizontally { it }
    }

    val slideOut: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? = {
        slideOutHorizontally { -it }
    }

    val slideInRev: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? = {
        slideInHorizontally { -it }
    }

    val slideOutRev: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? = {
        slideOutHorizontally { it }
    }

    val fadeIn: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? = {
        fadeIn(animationSpec = springQuick())
    }

    val fadeOut: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? = {
        fadeOut(animationSpec = springQuick())
    }
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController")
}

val LocalHazeState = compositionLocalOf<HazeState> {
    error("No haze state!")
}

val LocalWindow = compositionLocalOf<Window> { error("No Window") }
