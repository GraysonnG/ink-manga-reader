package com.blanktheevil.inkmangareader.stubs

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.repositories.ChapterListRequest
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterListEither
import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepository
import com.blanktheevil.inkmangareader.data.success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChapterRepositoryStub : ChapterRepository {
    override suspend fun get(chapterId: String, hardRefresh: Boolean): StateFlow<Either<Chapter>> =
        MutableStateFlow(success(StubData.chapter()))

    override suspend fun getEager(chapterId: String): Either<Chapter> =
        success(StubData.chapter())

    override suspend fun getList(
        request: ChapterListRequest,
        limit: Int,
        offset: Int,
        hardRefresh: Boolean
    ): StateFlow<ChapterListEither> =
        MutableStateFlow(success(StubData.chapterList(length = 3)))

    override suspend fun markAsRead(mangaId: String, chapterId: String, isRead: Boolean) {

    }

    override suspend fun getPages(chapterId: String, dataSaver: Boolean): Either<List<String>> =
        success(listOf("a.png", "b.png"))
}