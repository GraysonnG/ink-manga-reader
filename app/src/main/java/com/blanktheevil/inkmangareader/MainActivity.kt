package com.blanktheevil.inkmangareader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.emptyDataList
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mangaRepository: MangaRepository = koinInject()
            var list: MangaList by remember {
                mutableStateOf(emptyDataList())
            }

            LaunchedEffect(Unit) {
                list = mangaRepository.getList(
                    MangaListRequest.Seasonal,
                    hardRefresh = false,
                ).value.successOrNull() ?: emptyDataList()
            }

            Column {
                list.items.forEach {
                    Text(it.title)
                }
            }
        }
    }
}
