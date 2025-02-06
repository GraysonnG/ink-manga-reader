package com.blanktheevil.inkmangareader.data.state

import android.util.Log
import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.models.BaseItem
import com.blanktheevil.inkmangareader.data.room.temp.ModelStateDao
import com.blanktheevil.inkmangareader.data.room.temp.ModelStateModel
import com.blanktheevil.inkmangareader.data.success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ModelStateProvider(
    private val modelStateDao: ModelStateDao,
) {
    private val modelStateScope = CoroutineScope(Dispatchers.IO)
    private var states: MutableMap<String, ActiveState<*>> = mutableMapOf()

    companion object {
        private const val EXPIRE_TIME = 15 * 60 * 1000 // 15 MIN
    }

    suspend fun <T> register(
        key: String,
        hardRefresh: Boolean = false,
        networkProvider: suspend () -> Either<T>,
        localProvider: (suspend () -> Either<T>)? = null,
        persist: (suspend (T) -> Unit)? = null,
    ): StateFlow<Either<T>> {
        val activeState = getActiveState<T>(key = key)
        val stateExpired = activeState.expireTime < System.currentTimeMillis()
        states[key] = activeState

        modelStateScope.launch {
            // if activeState is expired or the hard refresh flag is true
            if (stateExpired || hardRefresh) {
                activeState.stateFlow.emit(
                    getNetworkDataAndPersist(
                        activeState,
                        networkProvider,
                        persist,
                    )
                )
            } else {
                // get the local state or an Either.Null
                val localState = localProvider?.invoke() ?: Either.Null()
                // get the local state and if its not a success grab a new network state
                val state = if (localState !is Either.Success)
                    getNetworkDataAndPersist(activeState, networkProvider, persist)
                else localState
                // emit the the state into the flow
                activeState.stateFlow.emit(state)
            }
        }

        return activeState.stateFlow
    }

    suspend fun <T> update(
        key: String,
        persist: (suspend (T) -> Unit)? = null,
        update: T.() -> T
    ) {
        val activeState = getActiveState<T>(key = key)
        activeState.stateFlow.value.successOrNull()?.let {
            val newState = update(it)
            activeState.stateFlow.emit(success(newState))
            persist?.invoke(newState)
            if (newState is BaseItem) {
                Log.d("Update", "Notifying item lists...")
                notifyItemListOfChange(newState)
            }
        }
    }

    @Suppress("unchecked_cast")
    suspend fun <T : BaseItem> updateLists(
        itemId: String,
        persist: (suspend (T) -> Unit)? = null,
        update: T.() -> T,
    ) {
        states.values
            .filterIsListOfBaseItem() // actually filter the list
            .filterIsInstance<ActiveState<DataList<BaseItem>>>() // trick the compiler
            .associate { it.stateFlow to it.stateFlow.value.successOrNull() }
            .forEach { (flow, data) ->
                if (data == null) return@forEach
                val itemIndex = data.items.indexOfFirst { it.id == itemId }

                if (itemIndex >= 0) {
                    val item = data.items
                        .filter { it.id == itemId }
                        .firstNotNullOfOrNull { it as? T }

                    val newItem = item?.update()
                    data.items.toMutableList().apply {
                        newItem?.let { updatedItem ->
                            set(itemIndex, updatedItem)
                            persist?.invoke(updatedItem)
                            flow.emit(success(data.copy(items = this)))
                        }
                    }
                }
            }
    }

    private suspend inline fun <reified T : BaseItem> notifyItemListOfChange(itemChanged: T) =
        states.values
            .filterIsListOfBaseItem(itemChanged.type)
            .filterIsInstance<ActiveState<DataList<T>>>()
            .associate { it.stateFlow to it.stateFlow.value.successOrNull() }
            .forEach { (flow, data) ->
                if (data == null) return@forEach
                val indexOfItem = data.items.indexOfFirst { it.id == itemChanged.id }
                if (indexOfItem >= 0) {
                    val newList = data.items.toMutableList().apply {
                        set(indexOfItem, itemChanged)
                    }

                    Log.d("Notify List Change", "${itemChanged.type.split(".").lastOrNull()} List: ${data.title}")
                    flow.emit(success(data.copy(items = newList)))
                }
            }

    private fun MutableCollection<ActiveState<*>>.filterIsListOfBaseItem(
        baseItemType: String? = null,
    ) = filter {
        it.stateFlow.value.successOrNull()?.let { t ->
            if (t::class.java != DataList::class.java) {
                return@filter false
            } else {
                return@filter if (baseItemType == null) true else {
                    (t as DataList<*>).items.firstOrNull()?.let { t2 ->
                        t2::class.java.name == baseItemType
                    } ?: false
                }
            }
        } ?: false
    }

    private suspend fun <T> getNetworkDataAndPersist(
        activeState: ActiveState<T>,
        networkProvider: suspend () -> Either<T>,
        persist: (suspend (T) -> Unit)? = null,
    ): Either<T> = networkProvider().also { either ->
        either.onSuccess {
            persist?.invoke(it)
            activeState.expireTime = System.currentTimeMillis() + EXPIRE_TIME
            modelStateDao.insert(
                ModelStateModel(
                    key = activeState.key,
                    expireTime = activeState.expireTime
                )
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T> getActiveState(
        key: String,
    ): ActiveState<T> {
        // get the current state if its the wrong type or doesn't exist create a new one
        val activeState = (states[key] as? ActiveState<T>?) ?: run {
            val activeStateExpireTime = modelStateDao.get(key)?.expireTime ?: -1L
            ActiveState(
                key = key,
                stateFlow = MutableStateFlow(Either.Null()),
                expireTime = activeStateExpireTime,
            )
        }
        return activeState
    }

    data class ActiveState<T>(
        val key: String,
        val stateFlow: MutableStateFlow<Either<T>>,
        var expireTime: Long,
    )
}
