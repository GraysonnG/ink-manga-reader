package com.blanktheevil.inkmangareader.ui.theme

import androidx.annotation.ColorInt
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import coil.imageLoader
import coil.request.ImageRequest
import com.blanktheevil.inkmangareader.data.models.Manga
import kotlin.math.pow

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val LocalPrimarySwatch = compositionLocalOf {
    ColorSwatch(color = Color.Black, onColor = Color.White, rawColor = Color.Black, rawOnColor = Color.White)
}
val LocalContainerSwatch = compositionLocalOf {
    ColorSwatch(color = Color.Black, onColor = Color.White, rawColor = Color.Black, rawOnColor = Color.White)
}
val LocalSurfaceSwatch = compositionLocalOf {
    ColorSwatch(color = Color.Black, onColor = Color.White, rawColor = Color.Black, rawOnColor = Color.White)
}

fun Color.isDark(): Boolean {
    val parts = listOf(red, green, blue)
    val rgb = parts.map {
        if (it < 0.03928) {
            it / 12.92
        } else {
            ((it + 0.055) / 1.055).pow(2.4)
        }
    }
    val l = (0.2126 * rgb[0]) + (0.7152 * rgb[1]) + (0.0722 * rgb[2])
    return l <= 0.179
}

@Composable
fun Manga.toColorPalette(): Palette? {
    val context = LocalContext.current

    var palette by remember { mutableStateOf<Palette?>(null) }

    LaunchedEffect(coverArt) {
        coverArt?.let { url ->
            context.imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(url)
                    .allowHardware(false)
                    .build()
            ).drawable?.toBitmap()?.let { bitmap ->
                Palette.Builder(bitmap)
                    .maximumColorCount(32)
                    .generate()
            }?.let { palette = it }
        }
    }

    return palette
}

val Swatch.darkRgb: Int
    @ColorInt get() =
        Color.hsl(
            hue = hsl[0],
            saturation = hsl[1],
            lightness = 0.05f,
        ).toArgb()

val Swatch.safeRgb: Int
    @ColorInt get() =
        Color.hsl(
            hue = hsl[0],
            saturation = hsl[1].coerceIn(0f, 0.75f),
            lightness = hsl[2].coerceIn(0.25f, 1f),
        ).toArgb()


val Palette?.primary: Color
    @Composable get() = Color(
        this?.lightVibrantSwatch?.safeRgb
            ?: this?.vibrantSwatch?.safeRgb
            ?: this?.dominantSwatch?.safeRgb
            ?: MaterialTheme.colorScheme.primary.toArgb()
    )

val Palette?.onPrimary: Color
    @Composable get() = primary.onColor

val Palette?.container: Color
    @Composable get() = Color(
        this?.darkVibrantSwatch?.safeRgb
            ?: this?.vibrantSwatch?.safeRgb
            ?: this?.dominantSwatch?.safeRgb
            ?: MaterialTheme.colorScheme.primaryContainer.toArgb()
    )

val Palette?.onContainer: Color
    @Composable get() = container.onColor

val Palette?.surface: Color
    @Composable get() = Color(
        this?.darkVibrantSwatch?.darkRgb
            ?: this?.vibrantSwatch?.darkRgb
            ?: this?.dominantSwatch?.darkRgb
            ?: MaterialTheme.colorScheme.surface.toArgb()
    )

val Palette?.onSurface: Color
    @Composable get() = surface.onColor

val Color.onColor: Color
    get() = if (this.isDark())
        Color.White.copy(alpha = 0.85f)
    else
        Color.Black.copy(alpha = 0.85f)

private val colorSwatchAnimationSpec = springSlow<Color>()

val Palette?.primarySwatch: ColorSwatch
    @Composable get() = ColorSwatch(
        color = animateColorAsState(
            targetValue = primary,
            label = "palette-primary",
            animationSpec = colorSwatchAnimationSpec,
        ).value,
        onColor = animateColorAsState(
            targetValue = onPrimary,
            label = "palette-on-primary",
            animationSpec = colorSwatchAnimationSpec,
        ).value,
        rawColor = primary,
        rawOnColor = onPrimary
    )

val Palette?.containerSwatch: ColorSwatch
    @Composable get() = ColorSwatch(
        color = animateColorAsState(
            targetValue = container,
            label = "palette-container",
            animationSpec = colorSwatchAnimationSpec,
        ).value,
        onColor = animateColorAsState(
            targetValue = onContainer,
            label = "palette-on-container",
            animationSpec = colorSwatchAnimationSpec,
        ).value,
        rawColor = container,
        rawOnColor = onContainer,
    )

val Palette?.surfaceSwatch: ColorSwatch
    @Composable get() = ColorSwatch(
        color = animateColorAsState(
            targetValue = surface,
            label = "palette-surface",
            animationSpec = colorSwatchAnimationSpec,
        ).value,
        onColor = animateColorAsState(
            targetValue = onSurface,
            label = "palette-on-surface",
            animationSpec = colorSwatchAnimationSpec,
        ).value,
        rawColor = surface,
        rawOnColor = onSurface
    )

val ColorScheme.primarySwatch: ColorSwatch get() = ColorSwatch(
    color = primary,
    onColor = onPrimary,
    rawColor = primary,
    rawOnColor = onPrimary
)

val ColorScheme.containerSwatch: ColorSwatch get() = ColorSwatch(
    color = primaryContainer,
    onColor = onPrimaryContainer,
    rawColor = primaryContainer,
    rawOnColor = onPrimaryContainer,
)

val ColorScheme.surfaceSwatch: ColorSwatch get() = ColorSwatch(
    color = surface,
    onColor = onSurface,
    rawColor = surface,
    rawOnColor = onSurface,
)

data class ColorSwatch(
    val color: Color,
    val onColor: Color,
    val rawColor: Color,
    val rawOnColor: Color,
)