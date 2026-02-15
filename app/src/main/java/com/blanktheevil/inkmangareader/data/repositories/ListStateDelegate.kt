package com.blanktheevil.inkmangareader.data.repositories

import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.map
import com.blanktheevil.inkmangareader.data.models.BaseItem
import com.blanktheevil.inkmangareader.data.room.dao.ListDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface ListStateDelegate <T : BaseItem> {
    val listsState: MutableStateFlow<Map<String, DataList<String>>>

    suspend fun addUpdateList(key: String, data: DataList<T>)
}

class ListStateDelegateImpl<T : BaseItem>(
    repoScope: CoroutineScope,
    private val listDao: ListDao,
    private val prefix: String,
) : ListStateDelegate<T> {
    init {
        repoScope.launch {
            listsState.update { current ->
                current + listDao.getAll().associate { model ->
                    model.key to DataList(
                        items = model.ids,
                        title = model.title,
                        offset = model.offset,
                        limit = model.limit,
                        total = model.total,
                        extras = model.extras,
                    )
                }
            }
        }


        CoroutineScope(Dispatchers.IO).launch {
            // do something async on the io thread
        }
    }

    override val listsState = MutableStateFlow<Map<String, DataList<String>>>(emptyMap())
    override suspend fun addUpdateList(
        key: String,
        data: DataList<T>
    ) {
        // example: manga-list-Follows
        val listKey = "$prefix-list-$key"

        listDao.insertModelWithKey(listKey, data)

        listsState.update { current ->
            current + (key to data.map { it.id })
        }
    }
}