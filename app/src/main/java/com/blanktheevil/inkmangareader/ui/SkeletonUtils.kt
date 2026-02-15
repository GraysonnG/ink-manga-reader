package com.blanktheevil.inkmangareader.ui

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.skeletonBackground() = background(
    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
)