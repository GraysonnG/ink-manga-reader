package com.blanktheevil.inkmangareader.stubs

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaListEither
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.data.success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MangaRepositoryStub : MangaRepository {
    override suspend fun get(mangaId: String, hardRefresh: Boolean): StateFlow<Either<Manga>> =
        MutableStateFlow(success(StubData.manga()))

    override suspend fun getList(
        request: MangaListRequest,
        limit: Int,
        offset: Int,
        hardRefresh: Boolean
    ): StateFlow<MangaListEither> =
        MutableStateFlow(success(StubData.mangaList("Manga List", 5)))

    override suspend fun follow(mangaId: String): Either<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun unfollow(mangaId: String): Either<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getFollowing(mangaId: String): Either<Unit> {
        TODO("Not yet implemented")
    }
}
