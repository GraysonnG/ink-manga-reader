package com.blanktheevil.inkmangareader.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.blanktheevil.inkmangareader.stubs.stubModule
import com.blanktheevil.inkmangareader.ui.theme.InkMangaReaderTheme
import com.blanktheevil.inkmangareader.ui.theme.LocalContainerSwatch
import com.blanktheevil.inkmangareader.ui.theme.LocalPrimarySwatch
import com.blanktheevil.inkmangareader.ui.theme.LocalSurfaceSwatch
import com.blanktheevil.inkmangareader.ui.theme.containerSwatch
import com.blanktheevil.inkmangareader.ui.theme.primarySwatch
import com.blanktheevil.inkmangareader.ui.theme.surfaceSwatch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

@Composable
fun DefaultPreview(block: @Composable () -> Unit) {
    val context = LocalContext.current
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            androidContext(context)
            modules(
                stubModule
            )
        }
    }

    InkMangaReaderTheme {
        CompositionLocalProvider(
            LocalPrimarySwatch provides MaterialTheme.colorScheme.primarySwatch,
            LocalContainerSwatch provides MaterialTheme.colorScheme.containerSwatch,
            LocalSurfaceSwatch provides MaterialTheme.colorScheme.surfaceSwatch,
        ) {
            Surface {
                block()
            }
        }
    }
}