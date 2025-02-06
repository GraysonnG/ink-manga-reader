package com.blanktheevil.inkmangareader.data.models

import com.blanktheevil.inkmangareader.data.dto.objects.TagsDto

data class Tag(
    val id: String,
    val name: String,
    val group: String?,
)

fun TagsDto.toTag(): Tag =
    Tag(
        id = this.id,
        name = this.attributes.name["en"] ?: this.attributes.name.values.firstOrNull() ?: "Error",
        group = this.attributes.group,
    )
