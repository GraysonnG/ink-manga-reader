package com.blanktheevil.inkmangareader.data.dto.responses

import com.blanktheevil.inkmangareader.data.dto.MangaDexListResponse
import com.blanktheevil.inkmangareader.data.dto.objects.ChapterDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetChapterListResponse(
    override val data: List<ChapterDto>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int,
) : MangaDexListResponse<ChapterDto>