package com.blanktheevil.inkmangareader.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

@Composable
fun InkImage(
    modifier: Modifier = Modifier,
    painter: Painter,
    shape: Shape = RectangleShape,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
) {
    Image(
        modifier = Modifier
            .clip(shape)
            .background(color = placeholderColor)
            .then(modifier),
        painter = painter,
        contentDescription = contentDescription,
        contentScale = contentScale
    )
}