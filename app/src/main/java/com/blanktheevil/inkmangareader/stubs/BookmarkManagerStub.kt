package com.blanktheevil.inkmangareader.stubs

import com.blanktheevil.inkmangareader.bookmark.BookmarkManager
import kotlinx.coroutines.flow.MutableStateFlow

class BookmarkManagerStub : BookmarkManager {
    override val bookmarkState = MutableStateFlow(emptyMap<String, String>())

    override fun setBookmark(mangaId: String, chapterId: String) {}

    override fun removeBookmark(mangaId: String) {}
}