package com.blanktheevil.inkmangareader.stubs

import com.blanktheevil.inkmangareader.reader.ReaderManager
import com.blanktheevil.inkmangareader.reader.ReaderManagerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReaderManagerStub : ReaderManager {
    override val state: StateFlow<ReaderManagerState>
        get() = MutableStateFlow(ReaderManagerState())

    override fun setChapter(chapterId: String) {
    }

    override fun closeReader() {
    }

    override fun expandReader() {
    }

    override fun shrinkReader() {
    }

    override fun nextPage() {
    }

    override fun prevPage() {
    }

    override fun nextChapter() {
    }

    override fun prevChapter() {
    }

    override fun markChapterRead(isRead: Boolean, mangaId: String?, chapterId: String?) {
    }
}