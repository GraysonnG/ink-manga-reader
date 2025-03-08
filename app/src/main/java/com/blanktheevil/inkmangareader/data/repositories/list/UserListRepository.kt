package com.blanktheevil.inkmangareader.data.repositories.list

import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either

interface UserListRepository {
    /**
     * Gets the custom lists for a particular user by id.
     * @param userId the users Id
     */
    suspend fun getLists(userId: String): Either<Map<String, DataList<String>>>

    /**
     * Gets the custom lists for the currently logged in user.
     */
    suspend fun getCurrentLists(): Either<Map<String, DataList<String>>>

    /**
     * Adds a manga to a list.
     * @param mangaId the id of the manga you want to add.
     * @param listId the id of the list you want to add the manga to.
     */
    suspend fun addMangaToList(mangaId: String, listId: String): Either<Unit>

    /**
     * Removes a manga from a list.
     * @param mangaId the id of the manga you want to remove.
     * @param listId the id of the list you want to remove the manga from.
     */
    suspend fun removeMangaFromList(mangaId: String, listId: String): Either<Unit>
}