package com.blanktheevil.inkmangareader.ui

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

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
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
): AsyncImagePainter {
    val context = LocalContext.current
    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
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
