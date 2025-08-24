package com.blanktheevil.inkmangareader.download

import android.content.Context
import android.content.Intent
import android.util.Log
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.error
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.repositories.makeCall
import com.blanktheevil.inkmangareader.download.room.DownloadDao
import com.blanktheevil.inkmangareader.download.room.DownloadModel
import com.blanktheevil.inkmangareader.download.service.ChapterDownloadService
import com.squareup.moshi.Moshi
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
    private val moshi: Moshi,
    private val downloadDao: DownloadDao,
) : DownloadManager {
    private val coroutineContext = Dispatchers.IO + SupervisorJob()
    private val scope = CoroutineScope(coroutineContext)
    private val chapterAdapter = moshi.adapter(Chapter::class.java)
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

    override fun downloadChapters(chapterIds: List<String>) {
        chapterIds.forEach(::downloadChapter)
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
            val mangaFolder = getMangaFolderFromChapterId(chapterId)
            val images = (File(mangaFolder, chapterId).listFiles()?.toList() ?: emptyList())
                .filter { it.name.contains("page") }
            val sortedImages = images.sortedBy { it.name.split("_", ".")[1].toIntOrNull() }

            sortedImages.map { it.absolutePath }
        }
    }

    override suspend fun getChapterData(chapterId: String): Either<Chapter> = withContext(coroutineContext) {
        makeCall {
            val mangaFolder = getMangaFolderFromChapterId(chapterId)
            val chapterFolder = File(mangaFolder, chapterId)
            val dataFile = File(chapterFolder, "chapter.json")
            val data = dataFile.readLines().joinToString()
            chapterAdapter.fromJson(data)
        }
    }

    override suspend fun removeDownloadedChapter(chapterId: String) {
        downloadDao.remove(chapterId)
        try {
            val mangaFolder = getMangaFolderFromChapterId(chapterId)
            val chapterFolder = File(mangaFolder, chapterId)
            chapterFolder.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMangaFolderFromChapterId(chapterId: String) =
        context.filesDir.listFiles().firstOrNull { item ->
            item.isDirectory && item.listFiles().any { folder -> folder.name == chapterId }
        } ?: throw Exception("mangaFolder not found")
}