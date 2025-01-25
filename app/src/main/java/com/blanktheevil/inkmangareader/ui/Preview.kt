package com.blanktheevil.inkmangareader.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.blanktheevil.inkmangareader.stubs.stubModule
import com.blanktheevil.inkmangareader.ui.theme.InkMangaReaderTheme
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
        Surface {
            block()
        }
    }
}