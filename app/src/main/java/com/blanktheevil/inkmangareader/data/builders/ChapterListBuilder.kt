package com.blanktheevil.inkmangareader.data.builders

import android.util.Log
import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.api.MangaDexApi
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.repositories.ChapterListRequest
import com.blanktheevil.inkmangareader.data.repositories.makeOptionallyAuthenticatedCall
import com.blanktheevil.inkmangareader.data.repositories.mappers.toChapterList

class ChapterListBuilder(
    private val mangaDexApi: MangaDexApi,
    private val sessionManager: SessionManager,
) {
    suspend fun build(request: ChapterListRequest, limit: Int, offset: Int): Either<DataList<Chapter>> =
        makeOptionallyAuthenticatedCall(
            sessionManager = sessionManager
        ) { auth ->
            Log.d(ChapterListBuilder::class.simpleName, "I am fetching new data!")
            when (request) {
                is ChapterListRequest.Feed -> {
                    mangaDexApi.getMangaFeed(id = request.mangaId).toChapterList()
                }

                is ChapterListRequest.Generic -> {
                    mangaDexApi.getChapterList(ids = request.ids).toChapterList()
                }

                is ChapterListRequest.Follows -> {
                    if (auth == null) throw Exception("Not Authenticated")
                    mangaDexApi.getFollowsChapterFeed(authorization = auth).toChapterList()
                }

                else -> throw Exception("No Handler for ChapterListRequest: $request")
            }
        }
}
