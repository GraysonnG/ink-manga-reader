package com.blanktheevil.inkmangareader.data.state

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

        // if activeState is expired or the hard refresh flag is true
        modelStateScope.launch {
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
            if (newState is BaseItem) { notifyItemListOfChange(newState) }
        }
    }

    private suspend fun <T : BaseItem> notifyItemListOfChange(itemChanged: T) =
        states.values.filterIsInstance<ActiveState<DataList<T>>>()
            .associate { it.stateFlow to it.stateFlow.value.successOrNull() }
            .forEach { (flow, data) ->
                if (data == null) return@forEach
                val indexOfItem = data.items.indexOfFirst { it.id == itemChanged.id }
                if (indexOfItem >= 0) {
                    val newList = data.items.toMutableList().apply {
                        set(indexOfItem, itemChanged)
                    }

                    flow.emit(success(data.copy(items = newList)))
                }
            }

    private suspend fun <T> getNetworkDataAndPersist(
        activeState: ActiveState<T>,
        networkProvider: suspend () -> Either<T>,
        persist: (suspend (T) -> Unit)? = null,
    ): Either<T> = networkProvider().also { either ->
        either.onSuccess {
            persist?.invoke(it)
            activeState.expireTime = System.currentTimeMillis() + EXPIRE_TIME
            if (it is BaseItem) { notifyItemListOfChange(it) }
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
