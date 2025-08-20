package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> HorizontalChipGroup(
    modifier: Modifier = Modifier,
    title: String?,
    singleSelection: Boolean = true,
    selectionRequired: Boolean = false,
    leadingIcon: @Composable ((T, Boolean) -> Unit)? = null,
    items: List<T>,
    selectedItems: List<T>,
    onItemSelected: (List<T>) -> Unit,
    itemToString: @Composable (T) -> String = { it.toString() },
) {
    var selected by remember { mutableStateOf(selectedItems) }

    LaunchedEffect(Unit) {
        if (selected.isEmpty() && selectionRequired) {
            selected = if (singleSelection) {
                listOf(items.firstOrNull() ?: return@LaunchedEffect)
            } else {
                emptyList()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        title?.let { Text(text = title) }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items) { item ->
                if (item == null) return@items

                var isSelected = remember(selected) { item in selected }
                val color by animateColorAsState(
                    if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.outline
                )

                FilterChip(
                    leadingIcon = leadingIcon?.let { {
                        leadingIcon(item, isSelected)
                    } },
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = color,
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary
                    ),
                    selected = isSelected,
                    onClick = {
                        if (singleSelection) {
                            selected = if (isSelected && !selectionRequired) {
                                emptyList()
                            } else {
                                listOf(item)
                            }
                        } else {
                            if (!isSelected) {
                                selected += item
                            } else {
                                selected -= item
                            }
                        }

                        onItemSelected(selected)
                    },
                    label = {
                        Text(
                            text = itemToString(item),
                            color = color,
                        )
                    }
                )
            }
        }
    }
}
