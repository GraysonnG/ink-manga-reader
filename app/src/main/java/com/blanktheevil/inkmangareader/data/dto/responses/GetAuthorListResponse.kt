package com.blanktheevil.inkmangareader.data.dto.responses

import com.blanktheevil.inkmangareader.data.dto.MangaDexListResponse
import com.blanktheevil.inkmangareader.data.dto.objects.PersonDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetAuthorListResponse(
    override val data: List<PersonDto>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int,
) : MangaDexListResponse<PersonDto>