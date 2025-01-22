package com.blanktheevil.inkmangareader.data.room.temp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ModelStateDao {
    @Query("SELECT * FROM ModelStateModel WHERE `key` = :key")
    suspend fun get(key: String): ModelStateModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(modelState: ModelStateModel)
}