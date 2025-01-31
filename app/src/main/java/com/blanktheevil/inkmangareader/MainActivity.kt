package com.blanktheevil.inkmangareader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.blanktheevil.inkmangareader.navigation.PrimaryNavGraph
import com.blanktheevil.inkmangareader.ui.LocalNavController
import com.blanktheevil.inkmangareader.ui.pages.DemoPage
import com.blanktheevil.inkmangareader.ui.theme.InkMangaReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            InkMangaReaderTheme {
                CompositionLocalProvider(
                    LocalNavController provides rememberNavController()
                ) {
                    Scaffold {
                        Surface(
                            Modifier
                                .padding(bottom = it.calculateBottomPadding())
                                .fillMaxSize()) {
                            PrimaryNavGraph()
                        }
                    }
                }
            }
        }
    }
}
