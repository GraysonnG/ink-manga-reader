package com.blanktheevil.inkmangareader.ui.pages

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import com.blanktheevil.inkmangareader.ui.components.MangaCard
import com.blanktheevil.inkmangareader.viewmodels.MangaListViewModel
import com.blanktheevil.inkmangareader.viewmodels.MangaListViewModel.State
import com.blanktheevil.inkmangareader.viewmodels.MangaListViewModel.Params

@Composable
fun MangaListPage(typeOrId: String) = BasePage<MangaListViewModel, State, Params>(
    viewModelParams = Params(typeOrId = typeOrId)
) { _, uiState, _ ->
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(uiState.list.items) {
            MangaCard(manga = it) {
                // go to manga detail page
            }
        }
    }
}