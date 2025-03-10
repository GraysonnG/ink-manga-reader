package com.blanktheevil.inkmangareader.data.dto.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarkChapterReadRequest(
    val chapterIdsRead: List<String>,
    val chapterIdsUnread: List<String>,
)
