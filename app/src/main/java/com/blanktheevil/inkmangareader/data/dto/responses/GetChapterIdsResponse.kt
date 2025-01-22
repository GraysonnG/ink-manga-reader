package com.blanktheevil.inkmangareader.data.dto.responses

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetChapterIdsResponse(
    val result: String?,
    val data: List<String>,
)