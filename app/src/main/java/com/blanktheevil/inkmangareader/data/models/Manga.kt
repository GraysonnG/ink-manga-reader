package com.blanktheevil.inkmangareader.data.models

import com.blanktheevil.inkmangareader.data.DataList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Manga(
    override val id: String,
    override val type: String = Manga::class.java.name,
    val coverArt: String?,
    val title: String,
    val description: String,
    val tags: List<String>,
) : BaseItem

typealias MangaList = DataList<Manga>