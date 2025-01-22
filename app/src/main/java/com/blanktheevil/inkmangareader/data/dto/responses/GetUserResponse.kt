package com.blanktheevil.inkmangareader.data.dto.responses

import com.blanktheevil.inkmangareader.data.dto.MangaDexResponse
import com.blanktheevil.inkmangareader.data.dto.objects.UserDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUserResponse(
    override val data: UserDto,
) : MangaDexResponse<UserDto>