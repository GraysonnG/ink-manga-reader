package com.blanktheevil.inkmangareader.download.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepository
import com.blanktheevil.inkmangareader.download.DownloadManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream

class ChapterDownloadService : Service() {

    companion object {
        const val EXTRA_KEY = "chapterIds"
    }

    private val downloadManager: DownloadManagerImpl by inject()
    private val chapterRepository: ChapterRepository by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val jobs: MutableMap<String, Job> = mutableMapOf()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val chapterId = intent?.getStringExtra(EXTRA_KEY) ?: return START_NOT_STICKY

        if (jobs[chapterId] != null && !(jobs[chapterId]!!.isCompleted)) return START_NOT_STICKY

        jobs[chapterId] = serviceScope.launch {
            downloadChapter(chapterId = chapterId)
            downloadManager.notifyChapterDownloadFinished(chapterId)
        }

        jobs.entries.associate { (id, job) ->
            id to job.isCompleted
        }.forEach { (key, completed) ->
            if (completed) jobs.remove(key)
        }


        return START_STICKY
    }

    private suspend fun downloadChapter(chapterId: String) = coroutineScope {
        val pages = chapterRepository.getPages(chapterId, false).successOrNull() ?: emptyList()

        var progress = 0
        val total = pages.size

        pages.mapIndexed { index, url ->
            async {
                downloadPage(chapterId, url, "page_$index")
                progress += 1
                downloadManager.notifyChapterProgress(
                    chapterId = chapterId,
                    progress = progress.toFloat() / total
                )
            }
        }.awaitAll()
    }


    private suspend fun downloadPage(chapterId: String, url: String, name: String) {
        Log.d("ChapterDownloadService", "Page: $name | Url: $url")
        val chapter = File(applicationContext.filesDir, chapterId).apply {
            if (!exists()) mkdirs()
        }
        val file = File(chapter, "$name.jpg")
        val bitmap = applicationContext.imageLoader.execute(
            ImageRequest.Builder(applicationContext)
                .data(url)
                .build()
        ).drawable?.toBitmap()

        bitmap?.let { saveImage(file, it) }
    }

    private fun saveImage(file: File, bitmap: Bitmap) {
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null
}