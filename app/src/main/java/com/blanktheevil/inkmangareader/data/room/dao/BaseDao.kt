package com.blanktheevil.inkmangareader.data.room.dao

interface BaseDao <T, R> {
    suspend fun get(key: String): T?
    suspend fun insert(data: T)
    suspend fun insertModel(data: R)
}