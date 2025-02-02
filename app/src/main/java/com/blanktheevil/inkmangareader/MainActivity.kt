package com.blanktheevil.inkmangareader

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.blanktheevil.inkmangareader.navigation.PrimaryNavGraph
import com.blanktheevil.inkmangareader.ui.LocalNavController
import com.blanktheevil.inkmangareader.ui.LocalWindow
import com.blanktheevil.inkmangareader.ui.reader.Reader
import com.blanktheevil.inkmangareader.ui.theme.InkMangaReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        setContent {
            InkMangaReaderTheme {
                CompositionLocalProvider(
                    LocalNavController provides rememberNavController(),
                    LocalWindow provides window
                ) {
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
}
