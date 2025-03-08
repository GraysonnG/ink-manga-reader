package com.blanktheevil.inkmangareader.bookmark

import kotlinx.coroutines.flow.MutableStateFlow

interface BookmarkManager {
    val bookmarkState: MutableStateFlow<Map<String, String>>
    fun setBookmark(mangaId: String, chapterId: String)
    fun removeBookmark(mangaId: String)
}