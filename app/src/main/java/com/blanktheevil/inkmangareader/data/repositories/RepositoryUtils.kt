package com.blanktheevil.inkmangareader.data.repositories

import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.isExpired
import com.blanktheevil.inkmangareader.data.models.BaseItem
import com.blanktheevil.inkmangareader.data.room.dao.BaseDao
import com.blanktheevil.inkmangareader.data.room.dao.ListDao
import com.blanktheevil.inkmangareader.data.room.models.BaseModel
import com.blanktheevil.inkmangareader.data.success
import com.blanktheevil.inkmangareader.data.error

suspend fun <T> makeCall(
    callback: suspend () -> T?,
): Either<T> {
    return try {
        val result = callback()
        if (result != null) success(result) else Either.Null()
    } catch (e: Exception) {
        e.printStackTrace()
        error(e)
    }
}

suspend fun <T> makeAuthenticatedCall(
    sessionManager: SessionManager,
    callback: suspend (auth: String) -> T?
) = makeCall {
    if (sessionManager.session.value == null || sessionManager.session.value?.isExpired() == true)
        throw Exception("Session Expired or null")

    val validSession = sessionManager.session.value!!
    callback(validSession.token)
}

suspend fun <T> makeOptionallyAuthenticatedCall(
    sessionManager: SessionManager,
    callback: suspend (auth: String?) -> T?
) = makeCall {
    val session = sessionManager.session.value.let {
        if (it == null || it.isExpired()) null else it
    }
    callback(session?.token)
}

fun makeKey(prefix: String, vararg key: Any) =
    "$prefix-${key.joinToString(separator = "-") { it.toString() }}"

suspend fun <T : BaseItem, R : BaseDao<*, T>> DataList<T>.insertIntoRoom(
    key: String,
    itemDao: R,
    listDao: ListDao,
) {
    this.items.forEach { itemDao.insertModel(it) }
    listDao.insertModelWithKey(key = key, data = this)
}

suspend fun <T : BaseItem, R : BaseDao<out BaseModel<T>, T>> getListFromRoom(
    key: String,
    itemDao: R,
    listDao: ListDao,
): DataList<T>? {
    val list = listDao.get(key) ?: return null
    return DataList(
        items = list.ids.mapNotNull { itemDao.get(it)?.data },
        title = list.title,
        limit = list.limit,
        offset = list.offset,
        total = list.total,
    )
}
