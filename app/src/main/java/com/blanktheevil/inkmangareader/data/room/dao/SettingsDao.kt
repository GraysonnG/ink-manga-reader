package com.blanktheevil.inkmangareader.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.blanktheevil.inkmangareader.data.room.models.SettingsModel

@Dao
interface SettingsDao {
    @Query("SELECT * FROM SettingsModel WHERE `key` = 1")
    suspend fun get(): SettingsModel?
}