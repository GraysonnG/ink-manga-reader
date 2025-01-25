package com.blanktheevil.inkmangareader.download.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class DownloadModel(
    @PrimaryKey val chapterId: String,
)
