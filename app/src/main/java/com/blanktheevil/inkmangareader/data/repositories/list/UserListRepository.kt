package com.blanktheevil.inkmangareader.data.repositories.list

import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.models.MangaList

interface UserListRepository {
    suspend fun getLists(userId: String): Either<Map<String, DataList<String>>>
    suspend fun getCurrentLists(): Either<Map<String, DataList<String>>>
    suspend fun addMangaToList(mangaId: String, listId: String): Either<Unit>
    suspend fun removeMangaFromList(mangaId: String, listId: String): Either<Unit>
}