package com.blanktheevil.inkmangareader.data.dto.responses

import com.blanktheevil.inkmangareader.data.dto.MangaDexResponse
import com.blanktheevil.inkmangareader.data.dto.objects.ChapterDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetChapterResponse(
    override val data: ChapterDto,
) : MangaDexResponse<ChapterDto>