package com.blanktheevil.inkmangareader.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.ScanlationGroup
import com.blanktheevil.inkmangareader.download.DownloadManager
import com.blanktheevil.inkmangareader.helpers.isNew
import com.blanktheevil.inkmangareader.reader.ReaderManager
import com.blanktheevil.inkmangareader.stubs.StubData
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.theme.LocalContainerSwatch
import com.blanktheevil.inkmangareader.ui.theme.LocalPrimarySwatch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ColumnScope.ChapterButton(
    chapter: Chapter
) = Box {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )
    val downloadManager: DownloadManager = koinInject()
    val readerManager: ReaderManager = koinInject()
    var chapterDownloaded by remember { mutableStateOf(false) }
    var isRead by remember(chapter.isRead) {
        mutableStateOf(chapter.isRead ?: false)
    }
    val isNew = remember(chapter.availableDate, chapter.isRead) {
        chapter.isNew
    }

    suspend fun refreshIsDownloaded() {
        delay(100)
        chapterDownloaded = downloadManager.isChapterDownloaded(chapter.id)
    }

    LaunchedEffect(Unit) {
        chapterDownloaded = downloadManager.isChapterDownloaded(chapter.id)
    }

    if (isNew) {
        val isNewString = stringResource(id = R.string.chapter_button_badge_new)

        Badge(
            contentColor = LocalPrimarySwatch.current.onColor,
            containerColor = LocalPrimarySwatch.current.color,
            modifier = Modifier
                .zIndex(2f)
                .offset(
                    y = 6.dp.unaryMinus(),
                    x = 6.dp.unaryMinus(),
                ),
            content = {
                Text(isNewString)
            }
        )
    }

    Column {
        InternalButton(
            chapterId = chapter.id,
            title = chapter.title.medium,
            colors = InkButtonColors(
                container = LocalContainerSwatch.current.color,
                onContainer = LocalContainerSwatch.current.onColor,
            ),
            isDownloaded = chapterDownloaded,
            isExternal = chapter.externalUrl != null,
            refreshIsDownloaded = ::refreshIsDownloaded,
            downloadManager = downloadManager,
            leadingIcon = {
                InkIcon(
                    resId = if(isRead) R.drawable.round_check_circle_24 else R.drawable.outline_circle_24,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            role = Role.Button,
                            onClick = {
                                readerManager.markChapterRead(
                                    isRead = !isRead,
                                    mangaId = chapter.relatedMangaId,
                                    chapterId = chapter.id,
                                )
                                isRead = !isRead
                            },
                        )
                        .padding(12.dp),
                    contentDescription = null,
                )
            }
        ) {
            if (chapter.externalUrl == null) {
                readerManager.setChapter(chapter.id)
            } else {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(chapter.externalUrl)
                    launcher.launch(this)
                }
            }
        }

        chapter.relatedScanlationGroup.GroupLink(
            chapterDownloaded = chapterDownloaded
        )
    }
}

@Composable
private fun InternalButton(
    chapterId: String,
    isDownloaded: Boolean,
    isExternal: Boolean,
    title: String,
    downloadManager: DownloadManager,
    refreshIsDownloaded: suspend () -> Unit,
    leadingIcon: @Composable () -> Unit,
    colors: InkButtonColors = DefaultInkButtonColors,
    onClick: () -> Unit = { },
) {
    val scope = rememberCoroutineScope()
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    SimpleInkButton(
        onClick = onClick,
        colors = colors,
        title = {
            leadingIcon()

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

            if (isExternal) {
                InkIcon(
                    resId = R.drawable.round_open_in_new_24,
                    modifier = Modifier.padding(12.dp),
                    contentDescription = null,
                )
                return@SimpleInkButton
            }

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
    val noGroupString = stringResource(id = R.string.chapter_button_group_none)

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
            this@GroupLink?.name ?: noGroupString,
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
    val removeDownloadString = stringResource(id = R.string.chapter_button_menu_item_remove)
    val downloadString = stringResource(id = R.string.chapter_button_menu_item_download)
    val readLaterString = stringResource(id = R.string.chapter_button_menu_item_read_later)

    if (isDownloaded) {
        InkMenuItem(icon = R.drawable.rounded_download_24, text = removeDownloadString) {
            onDeleteDownloadClicked()
            onDismissRequest()
        }
    } else {
        InkMenuItem(icon = R.drawable.rounded_download_24, text = downloadString) {
            onDownloadClicked()
            onDismissRequest()
        }
    }

    InkMenuItem(icon = R.drawable.read_24, text = readLaterString) {
        onReadLaterClicked()
        onDismissRequest()
    }
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
                externalUrl = "some url"
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