package com.blanktheevil.inkmangareader.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.BuildConfig
import com.blanktheevil.inkmangareader.data.Tags
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.emptyDataList
import com.blanktheevil.inkmangareader.helpers.rememberFalseState
import com.blanktheevil.inkmangareader.helpers.rememberTrueState
import com.blanktheevil.inkmangareader.navigation.navigateToMangaDetail
import com.blanktheevil.inkmangareader.navigation.navigateToMangaList
import com.blanktheevil.inkmangareader.ui.LocalNavController
import com.blanktheevil.inkmangareader.ui.components.FeatureCarousel
import com.blanktheevil.inkmangareader.ui.components.FilteredMangaShelf
import com.blanktheevil.inkmangareader.ui.components.HomeHeader
import com.blanktheevil.inkmangareader.ui.components.ImageHeader
import com.blanktheevil.inkmangareader.ui.components.MangaFeed
import com.blanktheevil.inkmangareader.ui.components.MangaShelf
import com.blanktheevil.inkmangareader.ui.sheets.login.LoginSheet
import com.blanktheevil.inkmangareader.ui.sheets.search.SearchSheet
import com.blanktheevil.inkmangareader.viewmodels.DemoViewModel
import com.blanktheevil.inkmangareader.viewmodels.MangaListType
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DemoPage() = BasePage<DemoViewModel, DemoViewModel.DemoState, DemoViewModel.DemoParams> {viewModel, uiState, authenticated ->
    val sessionManager = koinInject<SessionManager>()
    val scope = rememberCoroutineScope()
    val nav = LocalNavController.current
    val columnState = rememberLazyListState()
    var searchSheetOpen by rememberFalseState()
    var loginSheetOpen by rememberFalseState()
    val headerHeight = LocalConfiguration.current.screenHeightDp.dp.times(0.5f)
    var featureEnabled by rememberTrueState()

    ImageHeader(
        initialHeight = headerHeight,
        minHeight = 56.dp,
        url = "",
        onCollapsed = { featureEnabled = false },
        onExpanded = { featureEnabled = true },
        headerArea = { scrollFraction ->
            val alpha = remember(scrollFraction) {
                1 - (scrollFraction * 1.5f).coerceIn(0f, 1f)
            }

            FeatureCarousel(
                contentModifier = Modifier
                    .alpha(alpha),
                mangaList = uiState.seasonalList ?: emptyDataList(),
                enabled = featureEnabled
            ) {
                nav.navigateToMangaDetail(mangaId = it)
            }

            Box(modifier = Modifier
                .background(
                    Color.Black.copy(
                        alpha = scrollFraction.times(0.8f)
                    )
                )
                .fillMaxSize())

            HomeHeader(
                scrollFraction = scrollFraction,
                authenticated = authenticated,
                onSearchClicked = { searchSheetOpen = true },
                onAccountClicked = {
                    if (authenticated) {
                        // open the user menu
                    } else {
                        loginSheetOpen = true
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.nestedScroll(it),
            state = columnState,
            contentPadding = PaddingValues(
                top = 16.dp
            )
        ) {
            if (authenticated) {
                item(key = "updates-feed") {
                    MangaFeed(feed = uiState.chapterFeed) {
                        nav.navigateToMangaDetail(mangaId = it)
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
            if (!authenticated) {
                item {
                    Button(onClick = {
                        scope.launch {
                            sessionManager.login(
                                BuildConfig.MANGADEX_USERNAME,
                                BuildConfig.MANGADEX_PASSWORD,
                            )
                        }
                    }) {
                        Text("Login")
                    }
                }
            }

            uiState.popularList?.let {
                item(key = "popular-feed") {
                    FilteredMangaShelf(
                        mangaList = it,
                        filters = Tags.PopularFilters,
                        onRowLinkClicked = {
                            nav.navigateToMangaList(MangaListType.POPULAR)
                        },
                        onItemClicked = { mangaId ->
                            nav.navigateToMangaDetail(mangaId = mangaId)
                        }
                    ) { tag ->
                        viewModel.filterPopularFeed(tag)
                    }
                    Spacer(Modifier.size(16.dp))
                }
            }

            uiState.recentList?.let {
                item(key = "recent-feed") {
                    MangaShelf(
                        mangaList = it,
                        onRowLinkClicked = {
                            nav.navigateToMangaList(MangaListType.RECENT)
                        }
                    ) { mangaId ->
                        nav.navigateToMangaDetail(mangaId = mangaId)
                    }
                    Spacer(Modifier.size(16.dp))
                }
            }

            if (uiState.userLists.isNotEmpty()) {
                item("user-list-title") {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { }
                        ,
                    ) {
                        Text(
                            text = "Your Manga Lists",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Organize your favorites",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                            )

                            Text(
                                text = "See all",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                            )

                        }
                    }
                }
            }

            items(uiState.userLists, key = { list ->
                list.items.joinToString { item -> item.id.takeLast(4) }
            }) { list ->
                MangaShelf(
                    mangaList = list,
                    onRowLinkClicked = {
                        val listId = list.extras?.get("listId")
                        if (listId != null) {
                            nav.navigateToMangaList(listId)
                        }
                    }
                ) { mangaId ->
                    nav.navigateToMangaDetail(mangaId = mangaId)
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }

    if (searchSheetOpen) {
        SearchSheet {
            searchSheetOpen = false
        }
    }

    if (loginSheetOpen) {
        LoginSheet {
            loginSheetOpen = false
        }
    }
}