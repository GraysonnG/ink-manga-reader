package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.ChapterList
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon

@Composable
fun Volume(title: String, chapters: List<Chapter>) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clip(RoundedCornerShape(8.dp))
        .border(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp)
        )
        .padding(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    val firstItemChapter = chapters.lastOrNull()?.chapter
    val lastItemChapter = chapters.firstOrNull()?.chapter

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(modifier = Modifier.weight(1f), text = title, style = MaterialTheme.typography.labelSmall)
        Text(modifier = Modifier.weight(1f), text = "Chapters $firstItemChapter-$lastItemChapter", textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            InkIcon(resId = R.drawable.baseline_more_horiz_24)
        }
    }
    val chaptersGrouped = remember(chapters) {
        chapters.groupBy { it.chapter }
    }


    chaptersGrouped.forEach { (ch, items) ->
            if (items.size > 1) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Chapter ${ch ?: 0}", style = MaterialTheme.typography.labelSmall)
                    items.forEach {
                        ChapterButton(chapter = it)
                    }
                }
            } else {
                items.firstOrNull()?.let {
                    ChapterButton(chapter = it)
                }
            }
        }
}

fun LazyListScope.volumeItems(volumes: Map<String, List<Chapter>>) =
    items(volumes.entries.toList()) { (volume, chapters) ->
        Volume("Vol. $volume", chapters)
    }


@Composable
fun Volumes(chapters: ChapterList) = Column {
    val volumes = remember(chapters) {
        chapters.items.groupBy { it.volume ?: "No Volume" }
    }

    volumes.forEach { (vol, chapters) ->
        Volume("Vol. $vol", chapters)
    } 
}

@Composable
fun VolumesSkeleton() = Column {
    repeat(3) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    repeat(3) {
                        Box(modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                            .height(12.dp)
                            .width(50.dp)
                        )
                    }
                }

                repeat(3) {
                    Column {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .height(44.dp)
                        )
                        Box(modifier = Modifier
                            .offset(x = 32.dp)
                            .clip(RoundedCornerShape(bottomEnd = 4.dp, bottomStart = 4.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            .height(20.dp)
                            .width(100.dp)
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSkeleton() = DefaultPreview {
    VolumesSkeleton()
}

@PreviewLightDark
@Composable
private fun Preview() = DefaultPreview {
    Volume(title = "Volume 1", chapters = StubData.chapterList(length = 5).items + StubData.chapter(
        chapter = 1
    ))
}

@PreviewLightDark
@Composable
private fun PreviewVolumeList() = DefaultPreview {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Volumes(chapters = StubData.chapterList(
            vol = { i -> if (i % 4 != 0) i % 4 else null },
            length = 16
        ))
    }
}

