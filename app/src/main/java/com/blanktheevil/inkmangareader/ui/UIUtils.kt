package com.blanktheevil.inkmangareader.ui

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }
fun Dp.dpToPx(density: Density) = with(density) { this@dpToPx.toPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }
fun Int.pxToDp(density: Density) = with(density) { this@pxToDp.toDp() }

val statusBarSize
    @Composable get() = with (LocalDensity.current) {
        WindowInsets.statusBars.getTop(this).toDp()
    }

@OptIn(ExperimentalLayoutApi::class)
val permanentStatusBarSize
    @Composable get() = with (LocalDensity.current) {
        WindowInsets.statusBarsIgnoringVisibility.getTop(this).toDp()
    }

val navigationBarSize
    @Composable get() = with(LocalDensity.current) {
        WindowInsets.navigationBars.getBottom(this).toDp()
    }

@OptIn(ExperimentalLayoutApi::class)
val permanentNavigationBarSize
    @Composable get() = with(LocalDensity.current) {
        WindowInsets.navigationBarsIgnoringVisibility.getBottom(this).toDp()
    }

@Composable
fun smartSystemBars() {
    val window = LocalWindow.current
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    DisposableEffect(Unit) {
        controller.apply {
            hide(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            controller.apply {
                show(WindowInsetsCompat.Type.statusBars())
                show(WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }
}

fun WindowInsetsControllerCompat.showSystemBars() {
    show(WindowInsetsCompat.Type.statusBars())
    show(WindowInsetsCompat.Type.navigationBars())
    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
}

fun WindowInsetsControllerCompat.hideSystemBars() {
    hide(WindowInsetsCompat.Type.statusBars())
    hide(WindowInsetsCompat.Type.navigationBars())
    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}

@Composable
fun InkIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current,
    @DrawableRes resId: Int,
) {
    Icon(
        painter = painterResource(id = resId), 
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
    )
}

@Composable
fun String?.toAsyncPainterImage(
    crossfade: Boolean = false,
    @DrawableRes placeholder: Int? = null,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
): AsyncImagePainter {
    val context = LocalContext.current
    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .apply { placeholder?.let { placeholder(placeholder) } }
            .dispatcher(dispatcher)
            .data(this)
            .crossfade(crossfade)
            .scale(Scale.FIT)
            .build(),
        contentScale = ContentScale.Crop,
        onError = {
            Log.d("AsyncImage Error", it.result.throwable.message.toString())
        }
    )
}
