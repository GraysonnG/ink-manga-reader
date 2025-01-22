package com.blanktheevil.inkmangareader.data.dto.objects

import com.blanktheevil.inkmangareader.data.dto.MangaDexObject
import com.blanktheevil.inkmangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoverDto(
    override val id: String,
    override val type: String,
    override val attributes: Attributes,
    override val relationships: RelationshipList?,
) : MangaDexObject<CoverDto.Attributes> {

    @JsonClass(generateAdapter = true)
    data class Attributes(
        val volume: String,
        val fileName: String,
        val description: String,
        val version: Int
    )
}