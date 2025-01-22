package com.blanktheevil.inkmangareader.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class ChapterModel(
    @PrimaryKey override val key: String,
    override val data: Chapter,
    val lastUpdated: Long,
) : BaseModel<Chapter>

fun Chapter.toModel(): ChapterModel =
    ChapterModel(
        key = this.id,
        data = this,
        lastUpdated = System.currentTimeMillis()
    )