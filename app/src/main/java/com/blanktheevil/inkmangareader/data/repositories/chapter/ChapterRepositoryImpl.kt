package com.blanktheevil.inkmangareader.data.repositories.chapter

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.api.MangaDexApi
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.dto.requests.MarkChapterReadRequest
import com.blanktheevil.inkmangareader.data.map
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.ChapterList
import com.blanktheevil.inkmangareader.data.repositories.ChapterListRequest
import com.blanktheevil.inkmangareader.data.repositories.getListFromRoom
import com.blanktheevil.inkmangareader.data.repositories.insertIntoRoom
import com.blanktheevil.inkmangareader.data.repositories.makeAuthenticatedCall
import com.blanktheevil.inkmangareader.data.repositories.makeCall
import com.blanktheevil.inkmangareader.data.repositories.makeKey
import com.blanktheevil.inkmangareader.data.repositories.makeOptionallyAuthenticatedCall
import com.blanktheevil.inkmangareader.data.repositories.mappers.convertToUrls
import com.blanktheevil.inkmangareader.data.repositories.mappers.toChapter
import com.blanktheevil.inkmangareader.data.repositories.mappers.toChapterList
import com.blanktheevil.inkmangareader.data.room.dao.ChapterDao
import com.blanktheevil.inkmangareader.data.room.dao.ListDao
import com.blanktheevil.inkmangareader.data.state.ModelStateProvider
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChapterRepositoryImpl(
    private val mangaDexApi: MangaDexApi,
    private val chapterDao: ChapterDao,
    private val listDao: ListDao,
    private val modelStateProvider: ModelStateProvider,
    private val sessionManager: SessionManager,
) : ChapterRepository {
    companion object {
        private const val CHAPTER_PREFIX = "chapter"
        private const val CHAPTER_LIST_PREFIX = "chapter-list"
    }

    override suspend fun get(
        chapterId: String,
        hardRefresh: Boolean,
    ): StateFlow<Either<Chapter>> = modelStateProvider.register(
        key = makeKey(CHAPTER_PREFIX, chapterId),
        hardRefresh = hardRefresh,
        networkProvider = { makeCall { mangaDexApi.getChapter(id = chapterId).data.toChapter() } },
        localProvider = { makeCall { chapterDao.get(chapterId)?.data } },
        persist = { chapter -> chapterDao.insertModel(chapter) },
    )

    override suspend fun getEager(chapterId: String): Either<Chapter> = makeCall {
        get(chapterId = chapterId, hardRefresh = false).value.successOrNull()
            ?: mangaDexApi.getChapter(id = chapterId).data.toChapter()
    }

    override suspend fun getList(
        request: ChapterListRequest,
        limit: Int,
        offset: Int,
        hardRefresh: Boolean
    ): StateFlow<ChapterListEither> = modelStateProvider.register(
        key = makeKey(CHAPTER_LIST_PREFIX, request.type, limit, offset),
        hardRefresh = hardRefresh,
        networkProvider = request.getNetworkProvider(limit, offset),
        localProvider = {
            makeCall {
                getListFromRoom(
                    key = makeKey(CHAPTER_LIST_PREFIX, request.type, limit, offset),
                    itemDao = chapterDao,
                    listDao = listDao,
                )
            }
        },
        persist = { chapterList ->
            chapterList.insertIntoRoom(
                key = makeKey(CHAPTER_LIST_PREFIX, request.type, limit, offset),
                itemDao = chapterDao,
                listDao = listDao
            )
        }
    )

    override suspend fun markAsRead(mangaId: String, chapterId: String, isRead: Boolean) {
        makeAuthenticatedCall(sessionManager) { auth ->
            mangaDexApi.markChapterRead(
                authorization = auth,
                id = mangaId,
                body = MarkChapterReadRequest(
                    chapterIdsRead = if (isRead) listOf(chapterId) else emptyList(),
                    chapterIdsUnread = if (isRead) emptyList() else listOf(chapterId)
                )
            )

            modelStateProvider.update<Chapter>(
                key = makeKey(CHAPTER_PREFIX, chapterId),
                persist = { chapterDao.insertModel(it) },
            ) { copy(isRead = isRead) }
        }
    }

    override suspend fun getPages(chapterId: String, dataSaver: Boolean): Either<List<String>> = makeCall {
        mangaDexApi.getChapterPages(chapterId = chapterId).convertToUrls(dataSaver = dataSaver)
    }

    private fun <T : ChapterListRequest> T.getNetworkProvider(
        limit: Int,
        offset: Int,
    ): suspend () -> ChapterListEither = suspend {
        when (this) {
            is ChapterListRequest.Generic -> handleChapterFeed {
                mangaDexApi.getChapterList(ids = this.ids, limit, offset)
                    .toChapterList()
            }


            is ChapterListRequest.Feed -> handleChapterFeed {
                mangaDexApi.getMangaFeed(id = this.mangaId, limit, offset)
                    .toChapterList()
            }

            is ChapterListRequest.Follows -> handleChapterFeed { auth ->
                mangaDexApi.getFollowsChapterFeed(authorization = auth!!, limit = limit, offset = offset)
                    .toChapterList()
            }

            else -> throw Exception("no request of this type available")
        }
    }

    private suspend fun handleChapterFeed(
        provider: suspend (auth: String?) -> ChapterList
    ): ChapterListEither = makeOptionallyAuthenticatedCall(sessionManager) { auth ->
        var chapterList = provider(auth)
        val mangaIds = chapterList.items.mapNotNull { it.relatedMangaId }.distinct()

        if (auth != null) {
            coroutineScope {
                launch {
                    val ids = mangaDexApi.getReadChapterIdsByMangaIds(
                        authorization = auth,
                        ids = mangaIds,
                    ).data

                    for (id in ids) {
                        val key = makeKey(CHAPTER_PREFIX, id)
                        modelStateProvider.update<Chapter>(key) {
                            copy(isRead = true)
                        }
                    }

                    chapterList = chapterList.map {
                        it.copy(isRead = it.id in ids)
                    }
                }
            }
        }

        chapterList
    }
}