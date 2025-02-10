package com.blanktheevil.inkmangareader.data.dto.responses

import com.blanktheevil.inkmangareader.data.dto.MangaDexResponse
import com.blanktheevil.inkmangareader.data.dto.objects.UserListDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUserListResponse(
    override val data: UserListDto,
) : MangaDexResponse<UserListDto>