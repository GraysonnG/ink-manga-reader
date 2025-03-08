package com.blanktheevil.inkmangareader.bookmark

import com.blanktheevil.inkmangareader.data.room.dao.BookmarkDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BookmarkManagerImpl(
    private val bookmarkDao: BookmarkDao,
) : BookmarkManager {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override val bookmarkState = MutableStateFlow((emptyMap<String, String>()))

    init {
        scope.launch {
            bookmarkDao.getAll()?.let {
                bookmarkState.value = it.associate { model -> model.mangaId to model.chapterId }
            }
        }
    }

    override fun setBookmark(mangaId: String, chapterId: String) {
        scope.launch {
            bookmarkDao.insert(mangaId, chapterId)
            bookmarkState.value += mangaId to chapterId
        }
    }

    override fun removeBookmark(mangaId: String) {
        scope.launch {
            bookmarkDao.remove(mangaId)
        }
    }
}