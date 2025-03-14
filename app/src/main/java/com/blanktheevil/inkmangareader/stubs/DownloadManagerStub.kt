package com.blanktheevil.inkmangareader.stubs

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.success
import com.blanktheevil.inkmangareader.download.DownloadManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DownloadManagerStub : DownloadManager {
    override val currentDownloads: StateFlow<Map<String, Float>>
        get() = MutableStateFlow(mapOf(StubData.chapter().id to 0.1f))

    override fun downloadChapter(chapterId: String) {

    }

    override fun notifyChapterProgress(chapterId: String, progress: Float) {

    }

    override fun notifyChapterDownloadFinished(chapterId: String) {

    }

    override suspend fun isChapterDownloaded(chapterId: String): Boolean = true

    override suspend fun removeDownloadedChapter(chapterId: String) {

    }

    override suspend fun getChapterPages(chapterId: String): Either<List<String>> {
        return success(emptyList())
    }
}