package com.blanktheevil.inkmangareader.ui

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

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