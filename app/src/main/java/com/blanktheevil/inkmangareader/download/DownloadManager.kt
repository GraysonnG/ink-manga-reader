package com.blanktheevil.inkmangareader.download

import kotlinx.coroutines.flow.StateFlow

interface DownloadManager {
    val currentDownloads: StateFlow<Map<String, Float>>
    fun downloadChapter(chapterId: String)
    fun notifyChapterProgress(chapterId: String, progress: Float)
    fun notifyChapterDownloadFinished(chapterId: String)
    suspend fun isChapterDownloaded(chapterId: String): Boolean
    suspend fun removeDownloadedChapter(chapterId: String)
}