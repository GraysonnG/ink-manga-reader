package com.blanktheevil.inkmangareader.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blanktheevil.inkmangareader.data.models.Manga
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class MangaModel(
    @PrimaryKey
    override val key: String,
    override val data: Manga,
    val lastUpdated: Long,
) : BaseModel<Manga>

fun Manga.toModel(): MangaModel =
    MangaModel(
        key = this.id,
        data = this,
        lastUpdated = System.currentTimeMillis()
    )