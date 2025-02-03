package com.blanktheevil.inkmangareader.ui.reader

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.WindowInsets
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.navigation.navigateToMangaDetail
import com.blanktheevil.inkmangareader.reader.ReaderManager
import com.blanktheevil.inkmangareader.reader.ReaderManagerState
import com.blanktheevil.inkmangareader.reader.ReaderType
import com.blanktheevil.inkmangareader.ui.LocalNavController
import com.blanktheevil.inkmangareader.ui.LocalWindow
import com.blanktheevil.inkmangareader.ui.hideSystemBars
import com.blanktheevil.inkmangareader.ui.navigationBarSize
import com.blanktheevil.inkmangareader.ui.permanentNavigationBarSize
import com.blanktheevil.inkmangareader.ui.permanentStatusBarSize
import com.blanktheevil.inkmangareader.ui.showSystemBars
import com.blanktheevil.inkmangareader.ui.smartSystemBars
import com.blanktheevil.inkmangareader.ui.statusBarSize
import com.blanktheevil.inkmangareader.ui.theme.springQuick
import com.blanktheevil.inkmangareader.ui.toAsyncPainterImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.math.max

@Composable
fun Reader() {
    val readerManager = koinInject<ReaderManager>()
    val readerState by readerManager.state.collectAsState()
    val readerMiniSize by remember { mutableStateOf(100.dp) }
    val configuration = LocalConfiguration.current
    val expanded = readerState.expanded
    val manga = readerState.manga
    val screenHeight = configuration.screenHeightDp.dp + permanentStatusBarSize + permanentNavigationBarSize
    val window = LocalWindow.current
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)

    LaunchedEffect(expanded, readerState.currentChapter) {
        if (expanded && readerState.currentChapter != null) {
            insetsController.hideSystemBars()
        } else {
            insetsController.showSystemBars()
        }
    }

    val cornerRadius by animateDpAsState(
        targetValue = if (expanded) 0.dp else 16.dp,
        animationSpec = springQuick(),
        label = "corner"
    )

    val width by animateDpAsState(
        targetValue = if (expanded) configuration.screenWidthDp.dp else readerMiniSize,
        animationSpec = springQuick(),
        label = "width"
    )

    val height by animateDpAsState(
        targetValue = if (expanded) screenHeight else readerMiniSize.times(
            16 / 10f
        ),
        animationSpec = springQuick(),
        label = "height"
    )

    val yOffset by animateDpAsState(
        targetValue = if (expanded) {
            0.dp
        } else {
            navigationBarSize.unaryMinus()
        }, label = "yOffset"
    )

    val padding by animateDpAsState(
        targetValue = if (expanded) 0.dp else 16.dp,
        animationSpec = springQuick(),
        label = "padding"
    )

    AnimatedVisibility(
        visible = readerState.currentChapter != null,
        enter = slideInVertically { -100 } + fadeIn(),
        exit = slideOutVertically { 100 } + fadeOut(),
    ) {
        Box(
            Modifier
                .height(screenHeight),
            contentAlignment = Alignment.BottomStart
        ) {
            Box(
                Modifier
                    .offset(y = yOffset)
                    .padding(padding),
            ) {
                Surface(
                    modifier = Modifier
                        .width(width)
                        .height(height),
                    color = Color.Black,
                    shape = RoundedCornerShape(cornerRadius),
                    shadowElevation = if (expanded) 0.dp else 8.dp
                ) {
                    CompositionLocalProvider(value = LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                        AnimatedVisibility(visible = !expanded) {
                            Box(Modifier.fillMaxSize()) {
                                MiniView(
                                    readerState = readerState,
                                    readerManager = readerManager,
                                    coverImageUrl = manga?.coverArt,
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = expanded,
                            exit = ExitTransition.None,
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                            ) {
                                FullView(
                                    readerManager = readerManager,
                                    readerState = readerState,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.MiniView(
    readerManager: ReaderManager,
    readerState: ReaderManagerState,
    coverImageUrl: String?
) {
    val view = LocalView.current

    Image(
        modifier = Modifier.fillMaxSize(),
        painter = coverImageUrl.toAsyncPainterImage(
            crossfade = true
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alpha = 0.5f
    )

    IconButton(
        modifier = Modifier.align(Alignment.Center),
        onClick = { readerManager.expandReader() }
    ) {
        Icon(
            painterResource(id = R.drawable.round_fullscreen_24),
            contentDescription = null
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val chapterName = if (readerState.currentChapter?.chapter != null)
            "Ch. ${readerState.currentChapter.chapter}"
        else
            "Ch. ..."

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = chapterName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )

        IconButton(
            onClick = {
                readerManager.closeReader()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                }
            }
        ) {
            Icon(
                painterResource(id = R.drawable.round_close_24),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun BoxScope.FullView(
    readerManager: ReaderManager,
    readerState: ReaderManagerState,
) {
    var uiVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiVisible) {
        if (uiVisible) {
            delay(3000)
            uiVisible = false
        }
    }

    LaunchedEffect(readerState.currentChapterLoading) {
        if (readerState.currentChapterLoading) {
            uiVisible = true
        }
    }

    BackHandler {
        readerManager.shrinkReader()
    }

    if (readerState.currentChapterLoading) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    } else {
        when (readerState.readerType) {
            ReaderType.PAGE -> {
                PageReader(
                    currentPage = readerState.currentPage,
                    pageUrls = readerState.currentChapterPageUrls,
                    nextButtonClicked = readerManager::nextPage,
                    prevButtonClicked = readerManager::prevPage,
                    middleButtonClicked = {
                        uiVisible = !uiVisible
                    },
                )
            }

            ReaderType.VERTICAL -> {
                StripReader(
                    pageUrls = readerState.currentChapterPageUrls,
                    onScreenClick = { uiVisible = !uiVisible },
                    nextButtonClicked = readerManager::nextChapter,
                    onLastPageViewed = {
                        readerManager.markChapterRead(
                            isRead = true,
                            chapterId = readerState.currentChapterId,
                            mangaId = readerState.mangaId,
                        )
                    },
                )
            }

            ReaderType.HORIZONTAL -> {
                StripReader(
                    isVertical = false,
                    pageUrls = readerState.currentChapterPageUrls,
                    onScreenClick = { uiVisible = !uiVisible },
                    nextButtonClicked = readerManager::nextChapter,
                    onLastPageViewed = {
                        readerManager.markChapterRead(
                            isRead = true,
                            chapterId = readerState.currentChapterId,
                            mangaId = readerState.mangaId,
                        )
                    },
                )
            }
        }

        ProgressBar(
            readerState = readerState
        )

        AnimatedVisibility(
            visible = uiVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                PageUI(
                    readerManager = readerManager,
                    readerState = readerState,
                )
            }
        }
    }
}

@Composable
private fun BoxScope.PageUI(
    readerManager: ReaderManager,
    readerState: ReaderManagerState,
) {
    val navController = LocalNavController.current
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .background(Color.Black.copy(0.8f))
            .padding(top = permanentStatusBarSize)
            .fillMaxWidth()
            .align(Alignment.TopCenter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { readerManager.shrinkReader() }
        ) {
            Icon(
                painterResource(id = R.drawable.round_keyboard_arrow_down_24),
                contentDescription = null
            )
        }

        readerState.manga?.let {
            Text(
                text = it.title.trim(),
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        role = Role.Button
                    ) {
                        readerState.mangaId?.let {
                            coroutineScope.launch {
                                navController.navigateToMangaDetail(it)
                                delay(100)
                                readerManager.shrinkReader()
                            }
                        }
                    },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }

        IconButton(
            onClick = {
                readerManager.closeReader()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                }
            }
        ) {
            Icon(
                painterResource(id = R.drawable.round_close_24),
                contentDescription = null
            )
        }
    }

    Row(
        modifier = Modifier
            .background(Color.Black.copy(0.8f))
            .padding(bottom = navigationBarSize)
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = readerManager::prevChapter) {
            Icon(
                painterResource(R.drawable.round_chevron_left_24),
                contentDescription = null
            )
        }

        readerState.currentChapter?.let {
            Text(
                text = it.title.short.trim(),
                modifier = Modifier.weight(1f, fill = true),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }

        IconButton(onClick = readerManager::nextChapter) {
            Icon(
                painterResource(R.drawable.round_chevron_right_24),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun BoxScope.ProgressBar(
    readerState: ReaderManagerState,
) {
    val progress =
        readerState.currentPage
            .toFloat()
            .plus(1f) / max(1f, readerState.currentChapterPageUrls.size.toFloat())

    val progressAnim by animateFloatAsState(targetValue = progress, label = "progress")

    Row(
        modifier = Modifier
            .padding(bottom = navigationBarSize)
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .height(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        readerState.currentChapterPageLoaded.forEach {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = if (it)
                            Color.White.copy(alpha = 0.3f)
                        else
                            Color.White.copy(alpha = 0.1f)
                    )
            )
        }
    }

    AnimatedVisibility(
        modifier = Modifier
            .padding(bottom = navigationBarSize)
            .align(Alignment.BottomStart),
        visible = readerState.readerType == ReaderType.PAGE
    ) {
        LinearProgressIndicator(
            progress = { progressAnim },
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.Transparent,
        )
    }
}