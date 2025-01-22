package com.blanktheevil.inkmangareader.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.room.models.ChapterModel
import com.blanktheevil.inkmangareader.data.room.models.toModel

@Dao
interface ChapterDao : BaseDao<ChapterModel, Chapter> {
    @Query("SELECT * FROM ChapterModel WHERE `key` = :key")
    override suspend fun get(key: String): ChapterModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(data: ChapterModel)

    override suspend fun insertModel(data: Chapter) {
        insert(data.toModel())
    }
}