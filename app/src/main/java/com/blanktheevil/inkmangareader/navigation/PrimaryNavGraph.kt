package com.blanktheevil.inkmangareader.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.blanktheevil.inkmangareader.ui.LocalNavController
import com.blanktheevil.inkmangareader.ui.Transitions
import com.blanktheevil.inkmangareader.ui.pages.DemoPage
import com.blanktheevil.inkmangareader.ui.pages.MangaDetailPage
import com.blanktheevil.inkmangareader.ui.pages.MangaListPage

@Composable
fun PrimaryNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = InkDestination.Home.route
    ) {
        simpleComposable(InkDestination.Home) {
            DemoPage()
        }

        simpleComposable(
            route = InkDestination.MangaDetail.declareArguments("mangaId"),
            arguments = listOf(
                navArgument("mangaId") { nullable = false }
            )
        ) {
            val mangaId = it.arguments?.getString("mangaId") ?: return@simpleComposable
            MangaDetailPage(mangaId = mangaId)
        }

        simpleComposable(
            route = InkDestination.MangaList.declareArguments("typeOrId"),
            arguments = listOf(
                navArgument("typeOrId") { nullable = false }
            )
        ) {
            val typeOrId = it.arguments?.getString("typeOrId") ?: return@simpleComposable
            MangaListPage(typeOrId = typeOrId)
        }
    }
}

sealed class InkDestination(val route: String) {
    data object Home : InkDestination("home")
    data object MangaDetail : InkDestination("manga")
    data object MangaList : InkDestination("mangalist")

    fun withArguments(
        vararg arguments: Pair<String, String>,
    ): String = "${route}${
        arguments.joinToString(prefix = "?", separator = "&") {
            "${it.first}=${it.second}"
        }
    }"

    fun declareArguments(
        vararg arguments: String,
    ): String = "${route}${
        arguments.joinToString(prefix = "?", separator = "&") {
            "$it={${it}}"
        }
    }"
}

private fun NavGraphBuilder.simpleComposable(
    route: InkDestination,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(navBackStackEntry: NavBackStackEntry) -> Unit,
) {
    composable(
        route = route.route,
        arguments = arguments,
        enterTransition = Transitions.slideIn,
        exitTransition = Transitions.slideOut,
        content = content,
    )
}

private fun NavGraphBuilder.simpleComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(navBackStackEntry: NavBackStackEntry) -> Unit,
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = Transitions.slideIn,
        exitTransition = Transitions.slideOut,
        popEnterTransition = Transitions.slideInRev,
        popExitTransition = Transitions.slideOutRev,
        content = content,
    )
}

fun NavController.navigateToHome() {
    navigate(route = InkDestination.Home.route) {
        popUpTo(InkDestination.Home.route)
    }
}

fun NavController.navigateToMangaDetail(mangaId: String) {
    navigate(
        route = InkDestination.MangaDetail.withArguments(
            "mangaId" to mangaId
        )
    )
}

fun NavController.navigateToMangaList(
    typeOrId: String
) {
    navigate(
        route = InkDestination.MangaList.withArguments(
            "typeOrId" to typeOrId,
        )
    )
}