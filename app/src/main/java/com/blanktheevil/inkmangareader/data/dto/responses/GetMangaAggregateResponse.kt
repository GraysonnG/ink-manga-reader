package com.blanktheevil.inkmangareader.data.dto.responses

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMangaAggregateResponse(
    val result: String,
    val volumes: Map<String, AggregateVolumeDto>,
)

@JsonClass(generateAdapter = true)
data class AggregateVolumeDto(
    val volume: String = "none",
    val chapters: Map<String, AggregateChapterDto>,
)

@JsonClass(generateAdapter = true)
data class AggregateChapterDto(
    val chapter: String = "null",
    val id: String,
    val others: List<String>,
    val count: Int,
)
