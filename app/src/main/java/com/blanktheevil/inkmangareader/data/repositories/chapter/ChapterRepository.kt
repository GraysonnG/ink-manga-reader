package com.blanktheevil.inkmangareader.data.repositories.chapter

import com.blanktheevil.inkmangareader.data.DEFAULT_LIST_LIMIT
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.ChapterList
import com.blanktheevil.inkmangareader.data.repositories.ChapterListRequest
import kotlinx.coroutines.flow.StateFlow

interface ChapterRepository {
    suspend fun get(chapterId: String, hardRefresh: Boolean): StateFlow<Either<Chapter>>
    suspend fun getEager(chapterId: String): Either<Chapter>
    suspend fun getList(request: ChapterListRequest, limit: Int = DEFAULT_LIST_LIMIT, offset: Int = 0, hardRefresh: Boolean): StateFlow<ChapterListEither>
    suspend fun markAsRead(mangaId: String, chapterId: String, isRead: Boolean)
    suspend fun getPages(chapterId: String, dataSaver: Boolean): Either<List<String>>
}

typealias ChapterListEither = Either<ChapterList>