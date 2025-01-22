package com.blanktheevil.inkmangareader.data.dto.responses

import com.blanktheevil.inkmangareader.data.dto.MangaDexListResponse
import com.blanktheevil.inkmangareader.data.dto.objects.MangaDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMangaListResponse(
    override val data: List<MangaDto>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int,
) : MangaDexListResponse<MangaDto>