package com.blanktheevil.inkmangareader

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.blanktheevil.inkmangareader.helpers.isUUID
import com.blanktheevil.inkmangareader.navigation.PrimaryNavGraph
import com.blanktheevil.inkmangareader.navigation.navigateToMangaDetail
import com.blanktheevil.inkmangareader.reader.ReaderManager
import com.blanktheevil.inkmangareader.ui.LocalNavController
import com.blanktheevil.inkmangareader.ui.LocalWindow
import com.blanktheevil.inkmangareader.ui.reader.Reader
import com.blanktheevil.inkmangareader.ui.theme.InkMangaReaderTheme
import com.blanktheevil.inkmangareader.ui.theme.LocalContainerSwatch
import com.blanktheevil.inkmangareader.ui.theme.LocalPrimarySwatch
import com.blanktheevil.inkmangareader.ui.theme.LocalSurfaceSwatch
import com.blanktheevil.inkmangareader.ui.theme.containerSwatch
import com.blanktheevil.inkmangareader.ui.theme.primarySwatch
import com.blanktheevil.inkmangareader.ui.theme.surfaceSwatch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val chapterId: String? = handleChapterDeeplink()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val readerManager = koinInject<ReaderManager>()

            InkMangaReaderTheme {
                CompositionLocalProvider(
                    LocalNavController provides navController,
                    LocalWindow provides window,
                    LocalPrimarySwatch provides MaterialTheme.colorScheme.primarySwatch,
                    LocalContainerSwatch provides MaterialTheme.colorScheme.containerSwatch,
                    LocalSurfaceSwatch provides MaterialTheme.colorScheme.surfaceSwatch,
                ) {
                    LaunchedEffect(Unit) {
                        if (chapterId != null && chapterId.isUUID()) {
                            readerManager.setChapter(chapterId)
                            val mangaId = readerManager.state.mapNotNull { it.mangaId }.first()
                            Log.d("MainActivity","mangaId: $mangaId")
                            navController.navigateToMangaDetail(mangaId)
                        }
                    }

                    Box {
                        Scaffold {
                            Surface(
                                Modifier
                                    .padding(bottom = it.calculateBottomPadding())
                                    .fillMaxSize()) {
                                PrimaryNavGraph()
                            }
                        }

                        Reader()
                    }
                }
            }
        }
    }

    private fun handleChapterDeeplink() = intent.data?.let {
        val chapterIndex = it.pathSegments.indexOf("chapter")
        if (chapterIndex < 0) return@let null

        val cId = it.pathSegments[chapterIndex + 1]
        if (!cId.isUUID()) return@let null

        cId.also { Log.d("MainActivity", "chapterId: $cId") }
    }
}
