package com.blanktheevil.inkmangareader.data.models

import com.blanktheevil.inkmangareader.data.DataList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Chapter(
    override val id: String,
    override val type: String = Chapter::class.java.name,
    val title: ChapterTitle,
    val volume: String?,
    val chapter: String?,
    val externalUrl: String?,
    val relatedMangaId: String?,
    val relatedManga: Manga?,
    val relatedScanlationGroupId: String?,
    val relatedScanlationGroup: ScanlationGroup?,
    val isRead: Boolean?,
    val availableDate: Long?,
) : BaseItem

typealias ChapterList = DataList<Chapter>

@JsonClass(generateAdapter = true)
data class ChapterTitle(
    val primary: String,
    val short: String,
    val medium: String,
)