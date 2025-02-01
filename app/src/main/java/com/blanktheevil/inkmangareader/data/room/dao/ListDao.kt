package com.blanktheevil.inkmangareader.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.models.BaseItem
import com.blanktheevil.inkmangareader.data.room.models.ListModel
import com.blanktheevil.inkmangareader.data.room.models.toModel

@Dao
interface ListDao : BaseDao<ListModel, DataList<out BaseItem>>{
    @Query("SELECT * FROM ListModel WHERE `key` = :key")
    override suspend fun get(key: String): ListModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(data: ListModel)

    override suspend fun insertModel(data: DataList<out BaseItem>) {
        insert(data.toModel())
    }

    suspend fun insertModelWithKey(
        key: String,
        data: DataList<out BaseItem>,
    ) {
        insert(data.toModel(key = key))
    }
}