package com.blanktheevil.inkmangareader.download.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DownloadDao {
    @Query("SELECT * FROM DownloadModel WHERE `chapterId` = :key")
    suspend fun get(key: String): DownloadModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: DownloadModel)

    @Query("DELETE FROM DownloadModel")
    suspend fun clear()

    @Query("DELETE FROM DownloadModel WHERE `chapterId` = :key")
    suspend fun remove(key: String)
}