package com.blanktheevil.inkmangareader.data.repositories.list

import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.api.MangaDexApi
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.dto.objects.MangaDto
import com.blanktheevil.inkmangareader.data.dto.responses.GetUserListsResponse
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.data.repositories.mappers.toMangaLists
import com.blanktheevil.inkmangareader.data.repositories.makeAuthenticatedCall
import com.blanktheevil.inkmangareader.data.repositories.makeCall

class UserListRepositoryImpl(
    private val mangaDexApi: MangaDexApi,
    private val sessionManager: SessionManager,
) : UserListRepository {
    override suspend fun getLists(userId: String): Either<Map<String, MangaList>> =
        makeCall {
            mangaDexApi.getUserLists(id = userId).toMangaLists()
        }

    override suspend fun getCurrentLists(): Either<Map<String, MangaList>> =
        makeAuthenticatedCall(sessionManager) { auth ->
            mangaDexApi.getCurrentUserLists(authorization = auth).toMangaLists()
        }

    override suspend fun addMangaToList(mangaId: String, listId: String): Either<Unit> =
        makeAuthenticatedCall(sessionManager) { auth ->
            mangaDexApi.addMangaToList(authorization = auth, id = mangaId, listId = listId)
        }

    override suspend fun removeMangaFromList(mangaId: String, listId: String): Either<Unit> =
        makeAuthenticatedCall(sessionManager) { auth ->
            mangaDexApi.removeMangaFromList(authorization = auth, id = mangaId, listId = listId)
        }

    private suspend fun GetUserListsResponse.toMangaLists(): Map<String, MangaList> {
        val listMap = data.associateWith {
            it.relationships?.getAllOfType<MangaDto>()?.map { rel -> rel.id } ?: emptyList()
        }

        val mangaList = mangaDexApi.getManga(ids = listMap.values.flatten(), limit = 100, offset = 0)
            .toMangaLists()

        return listMap.map { (listData, mangaIds) ->
            listData.id to DataList(
                title = listData.attributes.name,
                items = mangaList.items.filter { it.id in mangaIds },
                limit = 100,
                offset = 0,
            )
        }.toMap()
    }
}