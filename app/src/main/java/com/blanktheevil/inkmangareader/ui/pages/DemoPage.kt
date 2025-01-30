package com.blanktheevil.inkmangareader.ui.pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.BuildConfig
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.emptyDataList
import com.blanktheevil.inkmangareader.ui.components.FeatureCarousel
import com.blanktheevil.inkmangareader.ui.components.MangaFeed
import com.blanktheevil.inkmangareader.ui.components.MangaShelf
import com.blanktheevil.inkmangareader.viewmodels.DemoViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DemoPage() = BasePage<DemoViewModel, DemoViewModel.DemoState, DemoViewModel.DemoParams> { uiState, authenticated ->
    val sessionManager = koinInject<SessionManager>()
    val scope = rememberCoroutineScope()

    LazyColumn {
        item(key = "Seasonal") {
            FeatureCarousel(mangaList = uiState.seasonalList ?: emptyDataList())
        }
        item { Spacer(modifier = Modifier.size(16.dp)) }
        if (authenticated) {
            item {
                MangaFeed(feed = uiState.chapterFeed)
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
        if (!authenticated) {
            item {
                Button(onClick = {
                    scope.launch {
                        sessionManager.login(BuildConfig.MANGADEX_USERNAME, BuildConfig.MANGADEX_PASSWORD)
                    }
                }) {
                    Text("Login")
                }
            }
        }

        items(listOfNotNull(uiState.popularList, uiState.recentList)) {
            MangaShelf(mangaList = it)
            Spacer(modifier = Modifier.size(16.dp))
        }

        items(uiState.userLists) {
            MangaShelf(mangaList = it)
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}