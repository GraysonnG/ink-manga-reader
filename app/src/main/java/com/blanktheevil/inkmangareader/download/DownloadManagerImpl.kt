package com.blanktheevil.inkmangareader.download

import android.content.Context
import android.content.Intent
import com.blanktheevil.inkmangareader.download.room.DownloadDao
import com.blanktheevil.inkmangareader.download.room.DownloadModel
import com.blanktheevil.inkmangareader.download.service.ChapterDownloadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DownloadManagerImpl(
    private val context: Context,
    private val downloadDao: DownloadDao,
) : DownloadManager {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val downloadingMap = MutableStateFlow(
        emptyMap<String, Float>()
    )
    override val currentDownloads = downloadingMap.asStateFlow()


    override fun downloadChapter(chapterId: String) {
        val intent = Intent(context, ChapterDownloadService::class.java)
            .putExtra(ChapterDownloadService.EXTRA_KEY, chapterId)
        context.startService(intent)
        downloadingMap.value = downloadingMap.value.toMutableMap().apply {
            put(chapterId, 0f)
        }
    }

    override fun notifyChapterProgress(chapterId: String, progress: Float) {
        downloadingMap.value = downloadingMap.value.toMutableMap().apply {
            put(chapterId, progress)
        }
    }

    override fun notifyChapterDownloadFinished(chapterId: String) {
        scope.launch {
            delay(500)
            downloadingMap.value = downloadingMap.value.toMutableMap().apply {
                remove(chapterId)
            }

            downloadDao.insert(DownloadModel(chapterId))
        }
    }

    override suspend fun isChapterDownloaded(chapterId: String): Boolean {
        return downloadDao.get(chapterId) != null
    }
}