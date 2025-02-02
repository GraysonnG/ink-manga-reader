package com.blanktheevil.inkmangareader.reader

import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.mappers.LinkedChapter
import kotlinx.coroutines.flow.StateFlow
import java.util.ArrayList

interface ReaderManager {
    val state: StateFlow<ReaderManagerState>

    fun setChapter(chapterId: String)

    fun closeReader()
    fun expandReader()
    fun shrinkReader()

    fun nextPage()
    fun prevPage()

    fun nextChapter()
    fun prevChapter()

    fun markChapterRead()
}

data class ReaderManagerState(
    val manga: Manga? = null,
    val mangaId: String? = null,
    val chapters: List<LinkedChapter> = emptyList(),
    val currentChapter: Chapter? = null,
    val currentChapterId: String? = null,
    val currentChapterLoading: Boolean = true,
    val currentChapterPagesLoaded: Boolean = true,
    val currentLinkedChapter: LinkedChapter? = null,
    val currentChapterPageUrls: List<String> = emptyList(),
    val currentChapterPageLoaded: MutableList<Boolean> = ArrayList(),
    val currentPage: Int = 0,
    val expanded: Boolean = true,
    val readerType: ReaderType = ReaderType.PAGE
)