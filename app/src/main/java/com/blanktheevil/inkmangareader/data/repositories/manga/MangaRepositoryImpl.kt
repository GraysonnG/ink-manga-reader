package com.blanktheevil.inkmangareader.data.repositories.manga

import android.util.Log
import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.Sorting
import com.blanktheevil.inkmangareader.data.api.GithubApi
import com.blanktheevil.inkmangareader.data.api.MangaDexApi
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.dto.RelationshipType
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.repositories.getListFromRoom
import com.blanktheevil.inkmangareader.data.repositories.insertIntoRoom
import com.blanktheevil.inkmangareader.data.repositories.makeAuthenticatedCall
import com.blanktheevil.inkmangareader.data.repositories.makeCall
import com.blanktheevil.inkmangareader.data.repositories.makeKey
import com.blanktheevil.inkmangareader.data.repositories.mappers.LinkedChapter
import com.blanktheevil.inkmangareader.data.repositories.mappers.toLinkedChapters
import com.blanktheevil.inkmangareader.data.repositories.mappers.toManga
import com.blanktheevil.inkmangareader.data.repositories.mappers.toMangaList
import com.blanktheevil.inkmangareader.data.room.dao.ListDao
import com.blanktheevil.inkmangareader.data.room.dao.MangaDao
import com.blanktheevil.inkmangareader.data.state.ModelStateProvider
import kotlinx.coroutines.flow.StateFlow

class MangaRepositoryImpl(
    private val modelStateProvider: ModelStateProvider,
    private val mangaDexApi: MangaDexApi,
    private val githubApi: GithubApi,
    private val mangaDao: MangaDao,
    private val listDao: ListDao,
    private val sessionManager: SessionManager,
) : MangaRepository {
    companion object {
        private const val MANGA_PREFIX = "manga"
        private const val MANGA_LIST_PREFIX = "manga-list"
    }

    override suspend fun get(
        mangaId: String,
        hardRefresh: Boolean,
    ): StateFlow<Either<Manga>> = modelStateProvider.register(
        key = makeKey(MANGA_PREFIX, mangaId),
        hardRefresh = hardRefresh,
        networkProvider = { makeCall { mangaDexApi.getMangaById(id = mangaId).data.toManga() } },
        localProvider = { makeCall { mangaDao.get(key = mangaId)?.data } },
        persist = { manga -> mangaDao.insertModel(manga) },
    )

    override suspend fun getList(
        request: MangaListRequest,
        limit: Int,
        offset: Int,
        hardRefresh: Boolean,
    ): StateFlow<MangaListEither> = modelStateProvider.register(
        key = makeKey(MANGA_LIST_PREFIX, request.type, limit, offset),
        hardRefresh = hardRefresh,
        networkProvider = request.getNetworkProvider(limit = limit, offset = offset),
        localProvider = {
            makeCall {
                getListFromRoom(
                    key = makeKey(MANGA_LIST_PREFIX, request.type, limit, offset),
                    itemDao = mangaDao,
                    listDao = listDao,
                )
            }
        },
        persist = { mangaList ->
            mangaList.insertIntoRoom(
                key = makeKey(MANGA_LIST_PREFIX, request.type, limit, offset),
                itemDao = mangaDao,
                listDao = listDao,
            )
        }
    )

    override suspend fun follow(mangaId: String): Either<Unit> =
        makeAuthenticatedCall(sessionManager) { auth ->
            mangaDexApi.followManga(authorization = auth, id = mangaId)
            Unit
        }

    override suspend fun unfollow(mangaId: String): Either<Unit> =
        makeAuthenticatedCall(sessionManager) { auth ->
            mangaDexApi.unfollowManga(authorization = auth, id = mangaId)
            Unit
        }

    override suspend fun getFollowing(mangaId: String): Either<Unit> =
        makeAuthenticatedCall(sessionManager) { auth ->
            mangaDexApi.getIsUserFollowingManga(authorization = auth, id = mangaId)
            Unit
        }

    override suspend fun getAggregate(mangaId: String): Either<List<LinkedChapter>> =
        makeCall {
            mangaDexApi.getMangaAggregate(id = mangaId)
                .toLinkedChapters()
        }

    private fun <T : MangaListRequest> T.getNetworkProvider(
        limit: Int,
        offset: Int,
    ): suspend () -> MangaListEither = suspend {
        when (this) {
            is MangaListRequest.Generic -> makeCall {
                Log.d("Generic List", data.joinToString(","))
                mangaDexApi.getManga(ids = data, limit = limit, offset = offset)
                    .toMangaList(this.name)
            }

            is MangaListRequest.Popular -> makeCall {
                mangaDexApi.getMangaPopular(limit = limit, offset = offset)
                    .toMangaList(title = "Popular")
            }

            is MangaListRequest.Recent -> makeCall {
                mangaDexApi.getMangaRecent(limit = limit, offset = offset)
                    .toMangaList(title = "Recent")
            }

            is MangaListRequest.Follows -> makeAuthenticatedCall(sessionManager) { auth ->
                mangaDexApi.getFollowsList(authorization = auth, limit = limit, offset = offset)
                    .toMangaList()
            }

            is MangaListRequest.Seasonal -> makeCall {
                githubApi.getSeasonalData().let {
                    mangaDexApi.getManga(ids = it.mangaIds, limit = it.mangaIds.size, offset = 0)
                        .toMangaList(title = it.name)
                }
            }

            is MangaListRequest.UserList -> makeAuthenticatedCall(sessionManager) { auth ->
                val res = mangaDexApi.getListById(
                    authorization = auth,
                    listId = listId
                )
                val ids = res.data.relationships
                    ?.getAllOfType(RelationshipType.MANGA)
                    ?.map { it.id }
                    ?: emptyList()

                if (ids.isNotEmpty()) {
                    mangaDexApi.getManga(ids = ids.take(15), limit = limit, offset = offset)
                        .toMangaList(
                            title = res.data.attributes.name,
                            extras = mapOf("listId" to listId)
                        )
                } else {
                    DataList(items = emptyList(), title = res.data.attributes.name)
                }
            }

            is MangaListRequest.Search -> makeCall {
                mangaDexApi.getMangaSearch(
                    artists = artists,
                    authors = authors,
                    contentRating = contentRatings,
                    excludedTags = excludedTags,
                    excludedTagsMode = excludedTagsMode,
                    includedTags = includedTags,
                    includedTagsMode = includedTagsMode,
                    limit = limit,
                    offset = offset,
                    order = order.toOrder(),
                    publicationDemographic = publicationDemographic,
                    status = status,
                    title = title,
                    year = year,
                ).toMangaList()
            }

            else -> throw Exception("no request of this type available")
        }
    }

    private fun Pair<String, String>?.toOrder(): Map<String, String> {
        if (this == null) return emptyMap()

        val map = mutableMapOf<String, String>()

        if (this != Sorting.MAP.values.elementAt(0)) {
            map["order[$first]"] = second
        }

        return map
    }
}