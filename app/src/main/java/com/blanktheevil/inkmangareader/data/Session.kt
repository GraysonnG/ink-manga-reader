package com.blanktheevil.inkmangareader.data

import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

@JsonClass(generateAdapter = true)
data class Session(
    val token: String,
    val refresh: String,
    val expires: Long,
) {
    companion object {
        const val EXPIRE_TIME = FIFTEEN_MINUTES
    }
}

private const val FIFTEEN_MINUTES: Long = 15 * 60 * 1000

fun Session.isExpired() = System.currentTimeMillis() >= expires
fun Session?.isValid() = this != null && !this.isExpired()
fun Session?.isInvalid() = this == null || this.isExpired()

fun Flow<Session?>.onUniqueSessionState(): Flow<Boolean> =
    filterNotNull()
        .filter { it.isValid() }
        .map { it.isValid() }
        .distinctUntilChanged()
