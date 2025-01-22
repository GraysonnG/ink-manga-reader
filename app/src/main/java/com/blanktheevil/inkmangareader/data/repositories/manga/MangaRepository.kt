package com.blanktheevil.inkmangareader.data.repositories.manga

import com.blanktheevil.inkmangareader.data.DEFAULT_LIST_LIMIT
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.models.MangaList
import kotlinx.coroutines.flow.StateFlow

interface MangaRepository {
    suspend fun get(mangaId: String, hardRefresh: Boolean = false): StateFlow<Either<Manga>>
    suspend fun getList(
        request: MangaListRequest,
        limit: Int = DEFAULT_LIST_LIMIT,
        offset: Int = 0,
        hardRefresh: Boolean,
    ): StateFlow<MangaListEither>
    suspend fun follow(mangaId: String): Either<Unit>
    suspend fun unfollow(mangaId: String): Either<Unit>
    suspend fun getFollowing(mangaId: String): Either<Unit>
//    suspend fun getVolumes(): Either<Volumes>
}

typealias MangaListEither = Either<MangaList>