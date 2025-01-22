package com.blanktheevil.inkmangareader.data.repositories.list

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.models.MangaList

interface UserListRepository {
    suspend fun getLists(userId: String): Either<Map<String, MangaList>>
    suspend fun getCurrentLists(): Either<Map<String, MangaList>>
    suspend fun addMangaToList(mangaId: String, listId: String): Either<Unit>
    suspend fun removeMangaFromList(mangaId: String, listId: String): Either<Unit>
}