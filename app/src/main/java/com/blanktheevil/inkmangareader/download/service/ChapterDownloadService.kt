package com.blanktheevil.inkmangareader.download.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepository
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.download.DownloadManager
import com.squareup.moshi.Moshi
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

/**
 * Download Service for downloading chapters.
 *
 * Launch this service with the following extras:
 *  - `chapterId` - a string containing the id of the chapter you wish to download.
 *
 * Download Folder Structure:
 * ```
 * filesDir
 * └── mangaId
 *     ├── cover.jpg
 *     ├── manga.json
 *     └── chapterId
 *         ├── chapter.json
 *         └── page_{x}.jpg
 * ```
 */
class ChapterDownloadService : Service() {
    companion object {
        const val EXTRA_KEY = "chapterId"
    }

    private val downloadManager: DownloadManager by inject()
    private val chapterRepository: ChapterRepository by inject()
    private val mangaRepository: MangaRepository by inject()
    private val moshi: Moshi by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val jobs: MutableMap<String, Job> = mutableMapOf()

    private val mangaAdapter = moshi.adapter(Manga::class.java)
    private val chapterAdapter = moshi.adapter(Chapter::class.java)

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
        var chapter = chapterRepository.getEager(chapterId).successOrNull()
        val manga = chapter?.relatedManga ?: chapter?.relatedMangaId?.let {
            mangaRepository.getEager(it).successOrNull()
        }

        chapter = chapter?.copy(
            relatedManga = manga
        )

        // save data used in offline mode
        saveMangaData(manga)
        saveChapterData(manga, chapter)
        downloadCoverImage(manga, chapter, manga?.coverArt)

        var progress = 0
        val total = pages.size

        pages.mapIndexed { index, url ->
            async {
                downloadPage(manga, chapter, url, "page_$index")
                progress += 1
                downloadManager.notifyChapterProgress(
                    chapterId = chapterId,
                    progress = progress.toFloat() / total
                )
            }
        }.awaitAll()
    }

    private suspend fun downloadPage(
        manga: Manga?,
        chapter: Chapter?,
        pageUrl: String,
        name: String,
    ) {
        val manga = manga ?: return
        val chapter = chapter ?: return

        Log.d("ChapterDownloadService", "Page: $name | Url: $pageUrl")

        val chapterFolder = getChapterFolder(manga, chapter)
        val outputFile = File(chapterFolder, "$name.jpg")
        val bitmap = applicationContext.imageLoader.execute(
            ImageRequest.Builder(applicationContext)
                .data(pageUrl)
                .build()
        ).drawable?.toBitmap()

        bitmap?.let { saveImage(outputFile, it) }
    }

    private suspend fun downloadCoverImage(manga: Manga?, chapter: Chapter?, coverImageUrl: String?) {
        val manga = manga ?: return
        val chapter = chapter ?: return
        val coverImageUrl = coverImageUrl ?: return

        Log.d("ChapterDownloadService", "CoverImage: $coverImageUrl")

        val chapterFolder = getChapterFolder(manga, chapter)
        val file = File(chapterFolder, "cover.jpg")
        val bitmap = applicationContext.imageLoader.execute(
            ImageRequest.Builder(applicationContext)
                .data(coverImageUrl)
                .build()
        ).drawable?.toBitmap()

        bitmap?.let { saveImage(file, it) }
    }

    private fun saveMangaData(manga: Manga?) = manga?.let {
        val mangaFile = File(applicationContext.filesDir, manga.id).apply {
            if (!exists()) mkdirs()
        }

        File(mangaFile, "manga.json").apply {
            writeText(mangaAdapter.toJson(manga))
        }
    }

    private fun saveChapterData(manga: Manga?, chapter: Chapter?) {
        val manga = manga ?: return
        val chapter = chapter ?: return
        val chapterFile = getChapterFolder(manga, chapter)
        File(chapterFile, "chapter.json").apply {
            writeText(chapterAdapter.toJson(chapter))
        }
    }

    private fun saveImage(file: File, bitmap: Bitmap) {
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }

    private fun createFile(
        path: File,
        fileName: String,
    ) = File(path, fileName).apply {
        if (!exists()) mkdirs()
    }

    private fun getChapterFolder(manga: Manga, chapter: Chapter): File {
        val mangaFolder = createFile(applicationContext.filesDir, manga.id)
        return createFile(mangaFolder, chapter.id)
    }

    override fun onBind(p0: Intent?): IBinder? = null
}