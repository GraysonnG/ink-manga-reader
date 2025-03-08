package com.blanktheevil.inkmangareader.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blanktheevil.inkmangareader.data.room.models.BookmarkModel

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM BookmarkModel WHERE mangaId = :mangaId")
    suspend fun get(mangaId: String): BookmarkModel?

    @Query("SELECT * FROM BookmarkModel")
    suspend fun getAll(): List<BookmarkModel>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: BookmarkModel)

    suspend fun insert(mangaId: String, chapterId: String) {
        insert(BookmarkModel(mangaId, chapterId))
    }

    @Query("DELETE FROM BookmarkModel WHERE mangaId = :mangaId")
    suspend fun remove(mangaId: String)
}