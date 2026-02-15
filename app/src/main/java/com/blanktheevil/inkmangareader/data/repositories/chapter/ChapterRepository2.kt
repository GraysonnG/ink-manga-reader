@file:OptIn(FlowPreview::class)
package com.blanktheevil.inkmangareader.data.repositories.chapter

import com.blanktheevil.inkmangareader.data.DEFAULT_LIST_LIMIT
import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.builders.ChapterBuilder
import com.blanktheevil.inkmangareader.data.builders.ChapterListBuilder
import com.blanktheevil.inkmangareader.data.emptyDataList
import com.blanktheevil.inkmangareader.data.error
import com.blanktheevil.inkmangareader.data.isEmpty
import com.blanktheevil.inkmangareader.data.map
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.repositories.ChapterListRequest
import com.blanktheevil.inkmangareader.data.repositories.ListStateDelegate
import com.blanktheevil.inkmangareader.data.repositories.ListStateDelegateImpl
import com.blanktheevil.inkmangareader.data.room.dao.ChapterDao
import com.blanktheevil.inkmangareader.data.room.dao.ListDao
import com.blanktheevil.inkmangareader.data.success
import com.blanktheevil.inkmangareader.data.toSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChapterRepository2(
    private val chapterBuilder: ChapterBuilder,
    private val chapterListBuilder: ChapterListBuilder,
    private val chapterDao: ChapterDao,
    private val listDao: ListDao,
    private val repoScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ListStateDelegate<Chapter> by ListStateDelegateImpl(
    listDao = listDao,
    repoScope = repoScope,
    prefix = "chapter",
) {
    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeChaptersAndLists()
        }
    }

    private val _chaptersState = MutableStateFlow<Map<String, Chapter>>(emptyMap())

    suspend fun getChapter(chapterId: String): Flow<Either<Chapter>> =
        _chaptersState
            .map { chapters ->
                chapters[chapterId]?.let { success(it) } ?: chapterBuilder.build(chapterId).also {
                    it.onSuccess { chapter -> addUpdateChapters(listOf(chapter)) }
                }
            }
            .debounce(200)

    suspend fun getChapterList(
        request: ChapterListRequest,
        limit: Int = DEFAULT_LIST_LIMIT,
        offset: Int = 0,
        forceRefresh: Boolean
    ): Flow<Either<DataList<Chapter>>> = combine(_chaptersState, listsState, ::Pair)
        .debounce(200)
        .map { (allChapters, allLists) ->
            allLists[request.type]
                ?.map { allChapters[it]!! }
                ?.toSuccess()
                .takeIf { !forceRefresh }
                ?: chapterListBuilder.build(request, limit, offset)
                    .onSuccess {
                        addUpdateChapters(it.items)
                        addUpdateList(request.type, it)
                    }
                    .onError {
                        return@map error(it)
                    }
        }
        .distinctUntilChanged()
        .debounce(200)

    fun markAsRead(chapterId: String) {
        _chaptersState.update { current ->
            val chapter = current[chapterId] ?: return
            current + (chapterId to chapter.copy(isRead = true))
        }
    }

    private suspend fun addUpdateChapters(chapters: List<Chapter>) {
        chapters.forEach { chapterDao.insertModel(it) }

        _chaptersState.update { current ->
            current + chapters.associateBy { it.id }
        }
    }

    private suspend fun initializeChaptersAndLists() {
        addUpdateChapters(chapterDao.getAll().map { it.data })
    }
}