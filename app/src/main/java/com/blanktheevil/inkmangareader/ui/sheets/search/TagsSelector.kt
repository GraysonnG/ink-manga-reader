package com.blanktheevil.inkmangareader.ui.sheets.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.Tags
import com.blanktheevil.inkmangareader.data.models.Tag
import com.blanktheevil.inkmangareader.ui.InkIcon
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelector(
    initialIncludedTags: List<Tag> = emptyList(),
    initialExcludedTags: List<Tag> = emptyList(),
    tags: List<Tag>,
    onTagChanged: (included: List<Tag>, excluded: List<Tag>) -> Unit,
    onTagModeChanged: (included: Tags.Mode, excluded: Tags.Mode) -> Unit,
) {
    val locale = LocalContext.current.resources.configuration.locales[0]
    val categories = remember(tags) { tags.groupBy { it.group ?: "Other Options" } }
    var includedTags by remember { mutableStateOf(initialIncludedTags) }
    val includedTagMode by remember { mutableStateOf(Tags.Mode.AND) }
    var excludedTags by remember { mutableStateOf(initialExcludedTags) }
    val excludedTagMode by remember { mutableStateOf(Tags.Mode.OR) }

    LaunchedEffect(includedTags, excludedTags) {
        onTagChanged(includedTags, excludedTags)
    }

    fun handleTagClicked(tag: Tag) {
        when (tag) {
            in includedTags -> {
                includedTags -= tag
                excludedTags += tag
            }
            in excludedTags -> {
                excludedTags -= tag
            }
            else -> {
                includedTags += tag
            }
        }
    }

    fun String.cap(locale: Locale): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                locale
            ) else it.toString()
        }
    }

    Column {
        // disclaimer
        // groups
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
        ) {
            categories.forEach { (name, tags) ->
                Text(text = name.cap(locale))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    tags.forEach { tag ->
                        val color = when (tag) {
                            in includedTags -> MaterialTheme.colorScheme.primary
                            in excludedTags -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.outline
                        }

                        val icon = when(tag) {
                            in includedTags -> R.drawable.round_add_24
                            in excludedTags -> R.drawable.round_close_24
                            else -> null
                        }

                        FilterChip(
                            leadingIcon = icon?.let { {
                                InkIcon(
                                    modifier = Modifier.size(16.dp),
                                    resId = it,
                                    tint = color,
                                )
                            } },
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = false,
                                borderColor = color,
                            ),
                            selected = false,
                            onClick = { handleTagClicked(tag) },
                            label = {
                                Text(
                                    text = tag.name,
                                    color = color,
                                )
                            }
                        )
                    }
                }
            }
        }
        // modes
    }
}