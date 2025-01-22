package com.blanktheevil.inkmangareader.data.dto.responses

import com.blanktheevil.inkmangareader.data.dto.MangaDexListResponse
import com.blanktheevil.inkmangareader.data.dto.objects.UserListDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUserListsResponse(
    override val data: List<UserListDto>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int,
) : MangaDexListResponse<UserListDto>
