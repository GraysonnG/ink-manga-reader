package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.ScanlationGroup
import com.blanktheevil.inkmangareader.download.DownloadManager
import com.blanktheevil.inkmangareader.reader.ReaderManager
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ColumnScope.ChapterButton(chapter: Chapter) = Column {
    val downloadManager: DownloadManager = koinInject()
    val readerManager: ReaderManager = koinInject()
    var chapterDownloaded by remember { mutableStateOf(false) }

    suspend fun refreshIsDownloaded() {
        delay(100)
        chapterDownloaded = downloadManager.isChapterDownloaded(chapter.id)
    }

    LaunchedEffect(Unit) {
        chapterDownloaded = downloadManager.isChapterDownloaded(chapter.id)
    }

    InternalButton(
        chapterId = chapter.id,
        title = chapter.title.medium,
        isRead = chapter.isRead ?: false,
        isDownloaded = chapterDownloaded,
        refreshIsDownloaded = ::refreshIsDownloaded,
        downloadManager = downloadManager
    ) {
        readerManager.setChapter(chapter.id)
    }

    chapter.relatedScanlationGroup.GroupLink(
        chapterDownloaded = chapterDownloaded
    )
}

@Composable
private fun InternalButton(
    chapterId: String,
    isRead: Boolean,
    isDownloaded: Boolean,
    title: String,
    downloadManager: DownloadManager,
    refreshIsDownloaded: suspend () -> Unit,
    onClick: () -> Unit = { },
) {
    val scope = rememberCoroutineScope()
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    SimpleInkButton(
        onClick = onClick,
        title = {
            InkIcon(
                resId = if(isRead) R.drawable.round_check_circle_24 else R.drawable.outline_circle_24,
                modifier = Modifier.padding(start = 12.dp),
                contentDescription = null,
            )

            Text(
                title,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                maxLines = 2,
                minLines = 1,
                lineHeight = 18.sp,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.size(8.dp))
        },
        background = {
            DownloadBar(
                modifier = Modifier.align(Alignment.BottomStart),
                chapterId = chapterId,
                downloadManager = downloadManager,
                refreshIsDownloaded = refreshIsDownloaded,
                isDownloaded = isDownloaded,
            )
        },
        trailingIcon = {
            Box {
                ChapterMenu(
                    modifier = Modifier.offset(y = (-4).dp),
                    expanded = menuExpanded,
                    isDownloaded = isDownloaded,
                    onDownloadClicked = { downloadManager.downloadChapter(chapterId) },
                    onDeleteDownloadClicked = {
                        scope.launch {
                            downloadManager.removeDownloadedChapter(chapterId)
                            refreshIsDownloaded()
                        }
                    },
                    onReadLaterClicked = { /* TODO */ },
                    onDismissRequest = { menuExpanded = false }
                )

                val downloading by downloadManager.currentDownloads.map {
                    it[chapterId] != null
                }.collectAsState(initial = false)

                if (downloading) {
                    Box(
                        Modifier.size(48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = LocalContentColor.current,
                            strokeWidth = 3.dp,
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            menuExpanded = true
                        }
                    ) {
                        InkIcon(
                            resId = R.drawable.baseline_more_horiz_24,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun ScanlationGroup?.GroupLink(
    chapterDownloaded: Boolean,
) = Row(
    modifier = Modifier.padding(start = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    Row(
        Modifier
            .weight(1f)
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InkIcon(
            modifier = Modifier.size(16.dp),
            resId = R.drawable.round_subdirectory_arrow_right_24,
        )

        Text(
            this@GroupLink?.name ?: "No Group",
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(LocalContentColor.current.copy(alpha = 0.2f))
                .padding(4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = LocalContentColor.current,
            style = MaterialTheme.typography.bodySmall
        )
    }

    AnimatedVisibility(
        visible = chapterDownloaded
    ) {
        Row {
            InkIcon(
                modifier = Modifier.size(16.dp),
                resId = R.drawable.rounded_download_done_24,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = "Downloaded",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DownloadBar(
    downloadManager: DownloadManager,
    refreshIsDownloaded: suspend () -> Unit,
    isDownloaded: Boolean,
    modifier: Modifier = Modifier,
    chapterId: String,
) {
    val downloadProgress by downloadManager.currentDownloads.map {
        it.getOrDefault(chapterId, 0f)
    }.collectAsState(initial = 0f)

    val currentProgress by animateFloatAsState(
        targetValue = if (isDownloaded) 1f else downloadProgress, label = "downloadProgress"
    )

    LaunchedEffect(downloadProgress) {
        if (downloadProgress == 1f) {
            refreshIsDownloaded()
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = !isDownloaded,
        enter = EnterTransition.None,
        exit = fadeOut(),
        label = "download bar animation"
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(currentProgress)
                .height(3.dp)
                .background(LocalContentColor.current)
        )
    }
}

@Composable
private fun ChapterMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    isDownloaded: Boolean,
    onDownloadClicked: () -> Unit,
    onDeleteDownloadClicked: () -> Unit,
    onReadLaterClicked: () -> Unit,
    onDismissRequest: () -> Unit,
) = DropdownMenu(
    modifier = modifier,
    expanded = expanded,
    onDismissRequest = onDismissRequest
) {
    if (isDownloaded) {
        DropdownMenuItem(text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                InkIcon(resId = R.drawable.rounded_download_24)
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = "Remove Download")
            }
        }, onClick = {
            onDismissRequest()
            onDeleteDownloadClicked()
        })
    } else {
        DropdownMenuItem(text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                InkIcon(resId = R.drawable.rounded_download_24)
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = "Download")
            }
        }, onClick = {
            onDismissRequest()
            onDownloadClicked()
        })
    }
    DropdownMenuItem(text = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InkIcon(resId = R.drawable.read_24)
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "Read Later")
        }
    }, onClick = {
        onDismissRequest()
        onReadLaterClicked()
    })
}

@PreviewLightDark
@Composable
private fun Preview() = DefaultPreview {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChapterButton(
            chapter = StubData.chapter(
                isRead = true
            )
        )
        ChapterButton(
            chapter = StubData.chapter(
                title = "Chapter with a really really long name like actually way too fucking long wtf",
                scanlationGroupName = "Group with a really really long name like actually way too fucking long wtf"
            )
        )
    }
}