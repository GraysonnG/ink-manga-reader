package com.blanktheevil.inkmangareader.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class BookmarkModel(
    @PrimaryKey
    val mangaId: String,
    val chapterId: String,
)