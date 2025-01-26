package com.blanktheevil.inkmangareader.data

import com.squareup.moshi.JsonClass

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