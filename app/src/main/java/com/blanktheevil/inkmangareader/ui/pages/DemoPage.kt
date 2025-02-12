package com.blanktheevil.inkmangareader.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.BuildConfig
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.emptyDataList
import com.blanktheevil.inkmangareader.navigation.navigateToMangaDetail
import com.blanktheevil.inkmangareader.ui.LocalNavController
import com.blanktheevil.inkmangareader.ui.components.FeatureCarousel
import com.blanktheevil.inkmangareader.ui.components.HomeHeader
import com.blanktheevil.inkmangareader.ui.components.MangaFeed
import com.blanktheevil.inkmangareader.ui.components.MangaShelf
import com.blanktheevil.inkmangareader.ui.sheets.login.LoginSheet
import com.blanktheevil.inkmangareader.ui.sheets.search.SearchSheet
import com.blanktheevil.inkmangareader.viewmodels.DemoViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DemoPage() = BasePage<DemoViewModel, DemoViewModel.DemoState, DemoViewModel.DemoParams> {_, uiState, authenticated ->
    val sessionManager = koinInject<SessionManager>()
    val scope = rememberCoroutineScope()
    val nav = LocalNavController.current
    val columnState = rememberLazyListState()
    val scrollOffset by remember { derivedStateOf { columnState.firstVisibleItemScrollOffset + columnState.firstVisibleItemIndex * 1000 } }
    var searchSheetOpen by remember { mutableStateOf(false) }
    var loginSheetOpen by remember { mutableStateOf(false) }

    HomeHeader(
        scrollOffset = scrollOffset,
        authenticated = authenticated,
        onSearchClicked = { searchSheetOpen = true },
        onAccountClicked = { loginSheetOpen = true }
    )

    LazyColumn(
        state = columnState
    ) {
        item(key = "Seasonal") {
            FeatureCarousel(mangaList = uiState.seasonalList ?: emptyDataList()) {
                nav.navigateToMangaDetail(mangaId = it)
            }
        }
        item { Spacer(modifier = Modifier.size(16.dp)) }
        if (authenticated) {
            item {
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

        items(listOfNotNull(uiState.popularList, uiState.recentList)) {
            MangaShelf(mangaList = it) { mangaId ->
                nav.navigateToMangaDetail(mangaId = mangaId)
            }
            Spacer(modifier = Modifier.size(16.dp))
        }

        if (uiState.userLists.isNotEmpty()) {
            item {
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

        items(uiState.userLists) {
            MangaShelf(mangaList = it) { mangaId ->
                nav.navigateToMangaDetail(mangaId = mangaId)
            }
            Spacer(modifier = Modifier.size(16.dp))
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