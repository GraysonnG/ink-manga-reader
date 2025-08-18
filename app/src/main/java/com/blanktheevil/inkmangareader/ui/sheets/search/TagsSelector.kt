package com.blanktheevil.inkmangareader.ui.sheets.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.Tags
import com.blanktheevil.inkmangareader.data.models.Tag
import com.blanktheevil.inkmangareader.helpers.mutableStateOfFalse
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.cap
import com.blanktheevil.inkmangareader.ui.theme.springGentle
import com.blanktheevil.inkmangareader.ui.theme.springQuick
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TagSelector(
    initialIncludedTags: List<Tag> = emptyList(),
    initialExcludedTags: List<Tag> = emptyList(),
    tags: List<Tag>,
    onTagChanged: (included: List<Tag>, excluded: List<Tag>) -> Unit,
    onTagModeChanged: (included: Tags.Mode, excluded: Tags.Mode) -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    val categories = remember(tags) { tags.sortedBy {
        it.name.lowercase(locale)
    }.groupBy { it.group ?: "Other Options" } }
    var includedTags by remember { mutableStateOf(initialIncludedTags) }
    var includedTagMode by remember { mutableStateOf(Tags.Mode.AND) }
    var excludedTags by remember { mutableStateOf(initialExcludedTags) }
    var excludedTagMode by remember { mutableStateOf(Tags.Mode.OR) }

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

    Column(
        modifier = Modifier
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        categories.forEach { (name, tags) ->
            val selectedInCategory = remember(includedTags, excludedTags) {
                tags.filter { it in includedTags || it in excludedTags }
            }
            var categoryOpen by remember { mutableStateOfFalse() }

            Column {
                CategoryTitle(
                    name = name,
                    locale = locale,
                    categoryOpen = categoryOpen,
                ) { categoryOpen = !categoryOpen }

                SharedTransitionLayout {
                    AnimatedContent (
                        targetState = categoryOpen,
                    ) { it ->
                        when (it) {
                            false -> SelectedTags(
                                tags = selectedInCategory,
                                includedTags = includedTags,
                                excludedTags = excludedTags,
                                onTagClicked = ::handleTagClicked,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@AnimatedContent,
                            )
                            true -> FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Chips(
                                    items = tags,
                                    includedTags = includedTags,
                                    excludedTags = excludedTags,
                                    handleTagClicked = ::handleTagClicked,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this@AnimatedContent,
                                )
                            }
                        }
                    }
                }
            }
        }

        InclusionExclusion(
            includeMode = includedTagMode,
            excludeMode = excludedTagMode,
        ) { include, exclude ->
            includedTagMode = include
            excludedTagMode = exclude
            onTagModeChanged(includedTagMode, excludedTagMode)
        }
    }
}

@Composable
private fun CategoryTitle(
    name: String,
    locale: Locale,
    categoryOpen: Boolean,
    onTitleClicked: () -> Unit,
) = Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .clickable(
                enabled = true,
                onClick = onTitleClicked
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
) {
    val rotation by animateFloatAsState(
        if (categoryOpen) 0f else -90f,
        animationSpec = springQuick()
    )

    Text(
        text = name.cap(locale),
    )

    Icon(
        painterResource(R.drawable.round_keyboard_arrow_down_24),
        modifier = Modifier.rotate(rotation),
        contentDescription = null
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun FlowRowScope.Chips(
    items: List<Tag>,
    includedTags: List<Tag>,
    excludedTags: List<Tag>,
    handleTagClicked: (Tag) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) = with(sharedTransitionScope) {
    items.forEach { tag ->
        TagChip(
            modifier = Modifier
                .animateContentSize(),
            tag = tag,
            included = tag in includedTags,
            excluded = tag in excludedTags,
            locale = LocalConfiguration.current.locales[0],
            onTagClicked = handleTagClicked,
            sharedTransitionScope = this,
            animatedVisibilityScope = animatedVisibilityScope,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SelectedTags(
    tags: List<Tag>,
    includedTags: List<Tag>,
    excludedTags: List<Tag>,
    onTagClicked: (Tag) -> Unit,
    locale: Locale = LocalConfiguration.current.locales[0],
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
)  = with(sharedTransitionScope) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(tags, key = { it.id }) { tag ->
            TagChip(
                modifier = Modifier
                    .animateItem(
                        fadeInSpec = tween(durationMillis = 250),
                        fadeOutSpec = tween(durationMillis = 100),
                        placementSpec = springGentle(),
                    ),
                tag = tag,
                included = tag in includedTags,
                excluded = tag in excludedTags,
                locale = locale,
                onTagClicked = onTagClicked,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,

            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private inline fun TagChip(
    modifier: Modifier = Modifier,
    tag: Tag,
    included: Boolean,
    excluded: Boolean,
    locale: Locale,
    crossinline onTagClicked: (Tag) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) = with(sharedTransitionScope) {
    val (textColorAnim, colorAnim, icon) =
        tag.getReactiveUIElements(included, excluded)

    FilterChip(
        modifier = Modifier
            .then(modifier)
            .sharedElement(
                rememberSharedContentState(key = tag.id),
                animatedVisibilityScope = animatedVisibilityScope,
            )
            .skipToLookaheadSize(),
        leadingIcon = icon?.let { {
            InkIcon(
                modifier = Modifier.size(16.dp),
                resId = it,
                tint = textColorAnim,
            )
        } },
        selected = included || excluded,
        onClick = { onTagClicked(tag) },
        label = {
            Text(
                text = tag.name.cap(locale),
                color = textColorAnim,
            )
        },
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = included || excluded,
            borderColor = colorAnim,
        ),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colorAnim,
        ),
    )
}

@Composable
private fun InclusionExclusion(
    includeMode: Tags.Mode,
    excludeMode: Tags.Mode,
    onTagModeClicked: (Tags.Mode, Tags.Mode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Inclusion Mode")
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    onClick = { onTagModeClicked(Tags.Mode.AND, excludeMode) },
                    shape = SegmentedButtonDefaults.itemShape(0, 2),
                    selected = includeMode == Tags.Mode.AND,
                ) {
                    Text("AND")
                }

                SegmentedButton(
                    onClick = { onTagModeClicked(Tags.Mode.OR, excludeMode) },
                    shape = SegmentedButtonDefaults.itemShape(1, 2),
                    selected = includeMode == Tags.Mode.OR,
                ) {
                    Text("OR")
                }
            }
        }
        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Exclusion Mode")
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    onClick = { onTagModeClicked(includeMode, Tags.Mode.AND) },
                    shape = SegmentedButtonDefaults.itemShape(0, 2),
                    selected = excludeMode == Tags.Mode.AND,
                ) {
                    Text("AND")
                }

                SegmentedButton(
                    onClick = { onTagModeClicked(includeMode, Tags.Mode.OR) },
                    shape = SegmentedButtonDefaults.itemShape(1, 2),
                    selected = excludeMode == Tags.Mode.OR,
                ) {
                    Text("OR")
                }
            }
        }
    }
}

@Composable
private fun Tag.getReactiveUIElements(
    included: Boolean,
    excluded: Boolean,
): TagUIData {
    val textColorAnim by getTextColor(included, excluded)
    val colorAnim by getColor(included, excluded)
    val icon = getIcon(included, excluded)
    return TagUIData(textColorAnim, colorAnim, icon)
}

@Composable
private fun Tag.getTextColor(
    included: Boolean,
    excluded: Boolean,
) = animateColorAsState(
    targetValue = when {
        included -> MaterialTheme.colorScheme.onPrimary
        excluded -> MaterialTheme.colorScheme.onError
        else -> MaterialTheme.colorScheme.outline
    },
    label = "textColor"
)

@Composable
private fun Tag.getColor(
    included: Boolean,
    excluded: Boolean,
) = animateColorAsState(
    targetValue = when {
        included -> MaterialTheme.colorScheme.primary
        excluded -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    },
    label = "color"
)

@Composable
private fun Tag.getIcon(
    included: Boolean,
    excluded: Boolean,
): Int? = when {
    included -> R.drawable.round_add_24
    excluded -> R.drawable.outline_sub_24
    else -> null
}

private data class TagUIData(
    val textColor: Color,
    val color: Color,
    val icon: Int?
)

@PreviewLightDark
@Composable
private fun Preview() = DefaultPreview {
    TagSelector(
        initialExcludedTags = emptyList(),
        initialIncludedTags = emptyList(),
        tags = StubData.tagList(32),
        onTagChanged = {_,_->},
        onTagModeChanged = {_,_->},
    )
}
