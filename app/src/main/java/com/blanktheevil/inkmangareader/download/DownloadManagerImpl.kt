package com.blanktheevil.inkmangareader.download

import android.content.Context
import android.content.Intent
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.repositories.makeCall
import com.blanktheevil.inkmangareader.data.success
import com.blanktheevil.inkmangareader.download.room.DownloadDao
import com.blanktheevil.inkmangareader.download.room.DownloadModel
import com.blanktheevil.inkmangareader.download.service.ChapterDownloadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DownloadManagerImpl(
    private val context: Context,
    private val downloadDao: DownloadDao,
) : DownloadManager {
    private val coroutineContext = Dispatchers.IO + SupervisorJob()
    private val scope = CoroutineScope(coroutineContext)
    private val downloadingMap = MutableStateFlow(
        emptyMap<String, Float>()
    )
    override val currentDownloads = downloadingMap.asStateFlow()


    override fun downloadChapter(chapterId: String) {
        scope.launch {
            if (isChapterDownloaded(chapterId)) return@launch

            val intent = Intent(context, ChapterDownloadService::class.java)
                .putExtra(ChapterDownloadService.EXTRA_KEY, chapterId)
            context.startService(intent)
            downloadingMap.value = downloadingMap.value.toMutableMap().apply {
                put(chapterId, 0f)
            }
        }
    }

    override fun notifyChapterProgress(chapterId: String, progress: Float) {
        downloadingMap.value = downloadingMap.value.toMutableMap().apply {
            put(chapterId, progress)
        }
    }

    override fun notifyChapterDownloadFinished(chapterId: String) {
        scope.launch {
            downloadDao.insert(DownloadModel(chapterId))
            delay(500)
            downloadingMap.value = downloadingMap.value.toMutableMap().apply {
                remove(chapterId)
            }

        }
    }

    override suspend fun isChapterDownloaded(chapterId: String): Boolean {
        return downloadDao.get(chapterId) != null
    }

    override suspend fun getChapterPages(chapterId: String): Either<List<String>> = withContext(coroutineContext) {
        makeCall {
            val chapterDir = File(context.filesDir, chapterId)
            val images = chapterDir.listFiles()?.toList() ?: emptyList()
            val sortedImages = images.sortedBy { it.name.split("_")[1].toIntOrNull() }

            sortedImages.map { it.absolutePath }
        }
    }

    override suspend fun removeDownloadedChapter(chapterId: String) {
        downloadDao.remove(chapterId)
        File(context.filesDir, chapterId).deleteRecursively()
    }
}