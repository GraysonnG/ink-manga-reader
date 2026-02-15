package com.blanktheevil.inkmangareader.data.builders

import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.api.MangaDexApi
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.repositories.makeOptionallyAuthenticatedCall
import com.blanktheevil.inkmangareader.data.repositories.mappers.toMangaList

class MangaListBuilder(
    val mangaDexApi: MangaDexApi,
    val sessionManager: SessionManager,
) {
    suspend fun build(request: MangaListRequest, limit: Int, offset: Int): Either<DataList<Manga>> =
        makeOptionallyAuthenticatedCall(
            sessionManager = sessionManager,
        ) { _ ->
            when (request) {
                is MangaListRequest.Generic -> {
                    mangaDexApi.getManga(ids = request.data, limit = limit, offset = offset)
                        .toMangaList(title = request.name)
                }

                else -> throw Exception("No Handler for ChapterListRequest: $request")
            }
        }
}