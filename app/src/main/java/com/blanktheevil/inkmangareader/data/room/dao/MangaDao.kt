package com.blanktheevil.inkmangareader.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.room.models.MangaModel
import com.blanktheevil.inkmangareader.data.room.models.toModel

@Dao
interface MangaDao : BaseDao<MangaModel, Manga> {
    @Query("SELECT * FROM MangaModel WHERE `key` = :key")
    override suspend fun get(key: String): MangaModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(data: MangaModel)

    override suspend fun insertModel(data: Manga) {
        insert(data.toModel())
    }
}