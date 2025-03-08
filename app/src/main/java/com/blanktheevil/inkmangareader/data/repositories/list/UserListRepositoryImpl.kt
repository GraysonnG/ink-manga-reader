package com.blanktheevil.inkmangareader.data.repositories.list

import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.api.MangaDexApi
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.dto.RelationshipType
import com.blanktheevil.inkmangareader.data.dto.objects.UserDto
import com.blanktheevil.inkmangareader.data.dto.responses.GetUserListsResponse
import com.blanktheevil.inkmangareader.data.repositories.makeAuthenticatedCall
import com.blanktheevil.inkmangareader.data.repositories.makeCall

/**
 * # "listId"
 */
const val LIST_ID_EXTRA_KEY = "listId"
const val LIST_OWNER_NAME_EXTRA_KEY = "username"

class UserListRepositoryImpl(
    private val mangaDexApi: MangaDexApi,
    private val sessionManager: SessionManager,
) : UserListRepository {
    override suspend fun getLists(userId: String): Either<Map<String, DataList<String>>> =
        makeCall {
            mangaDexApi.getUserLists(id = userId).toMangaLists()
        }

    override suspend fun getCurrentLists(): Either<Map<String, DataList<String>>> =
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

    private fun GetUserListsResponse.toMangaLists(): Map<String, DataList<String>> =
        data.associate { userList ->
            val ids = userList.relationships?.getAllOfType(RelationshipType.MANGA)?.map { rel -> rel.id } ?: emptyList()
            val user = userList.relationships?.getFirstOfType<UserDto>()

            userList.id to DataList(
                title = userList.attributes.name,
                items = ids,
                offset = 0,
                limit = ids.size,
                total = ids.size,
                extras = mapOf(
                    LIST_ID_EXTRA_KEY to userList.id,
                    LIST_OWNER_NAME_EXTRA_KEY to (user?.attributes?.username ?: "Unknown"),
                )
            )
        }

}