package com.blanktheevil.inkmangareader.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.blanktheevil.inkmangareader.ui.InkIcon
import kotlinx.coroutines.launch

@Composable
fun InkMenuItem(
    @DrawableRes icon: Int,
    text: String,
    onClick: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()

    DropdownMenuItem(
        leadingIcon = { InkIcon(resId = icon) },
        text = { Text(text = text) },
        onClick = { scope.launch { onClick() } },
    )
}