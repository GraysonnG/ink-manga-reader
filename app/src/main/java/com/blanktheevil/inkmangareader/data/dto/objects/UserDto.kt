package com.blanktheevil.inkmangareader.data.dto.objects

import com.blanktheevil.inkmangareader.data.dto.MangaDexObject
import com.blanktheevil.inkmangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    override val id: String,
    override val type: String,
    override val attributes: Attributes,
    override val relationships: RelationshipList?,
) : MangaDexObject<UserDto.Attributes> {

    @JsonClass(generateAdapter = true)
    data class Attributes(
        val username: String,
    )
}