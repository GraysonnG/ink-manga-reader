package com.blanktheevil.inkmangareader.data.dto.responses

import com.blanktheevil.inkmangareader.data.dto.MangaDexListResponse
import com.blanktheevil.inkmangareader.data.dto.objects.CoverDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMangaCoversResponse(
    override val data: List<CoverDto>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int
) : MangaDexListResponse<CoverDto>