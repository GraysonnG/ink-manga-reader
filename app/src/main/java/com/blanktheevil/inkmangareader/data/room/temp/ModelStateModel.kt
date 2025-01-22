package com.blanktheevil.inkmangareader.data.room.temp

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class ModelStateModel(
    @PrimaryKey val key: String,
    var expireTime: Long,
)