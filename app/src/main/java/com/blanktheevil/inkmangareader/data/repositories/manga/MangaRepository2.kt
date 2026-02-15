@file:OptIn(FlowPreview::class)
package com.blanktheevil.inkmangareader.data.repositories.manga

import com.blanktheevil.inkmangareader.data.DEFAULT_LIST_LIMIT
import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.builders.MangaListBuilder
import com.blanktheevil.inkmangareader.data.error
import com.blanktheevil.inkmangareader.data.map
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.ListStateDelegate
import com.blanktheevil.inkmangareader.data.repositories.ListStateDelegateImpl
import com.blanktheevil.inkmangareader.data.repositories.MangaListRequest
import com.blanktheevil.inkmangareader.data.room.dao.ListDao
import com.blanktheevil.inkmangareader.data.room.dao.MangaDao
import com.blanktheevil.inkmangareader.data.toSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MangaRepository2(
    private val mangaBuilder: MangaListBuilder,
    private val mangaDao: MangaDao,
    private val listDao: ListDao,
    private val repoScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ListStateDelegate<Manga> by ListStateDelegateImpl(
    listDao = listDao,
    repoScope = repoScope,
    prefix = "manga",
) {
    private val _mangaState = MutableStateFlow<Map<String, Manga>>(emptyMap())

    fun getMangaList(
        request: MangaListRequest,
        limit: Int = DEFAULT_LIST_LIMIT,
        offset: Int = 0,
        forceRefresh: Boolean,
    ): Flow<Either<DataList<Manga>>> = combine(_mangaState, listsState, ::Pair)
        .distinctUntilChangedBy { it.first }
        .debounce(200)
        .map { (allManga, allLists) ->
            allLists[request.type]
                ?.map { allManga[it]!! }
                ?.toSuccess()
                .takeIf { !forceRefresh }
                ?: mangaBuilder.build(request, limit, offset)
                    .onSuccess {
                        addUpdateManga(it.items)
                        addUpdateList(request.type, it)
                    }
                    .onError { error ->
                        return@map error(error)
                    }
        }
        .distinctUntilChanged()
        .debounce(200)

    private suspend fun addUpdateManga(manga: List<Manga>) {
        manga.forEach { mangaDao.insertModel(it) }

        _mangaState.update { current ->
            current + manga.associateBy { it.id }
        }
    }
}