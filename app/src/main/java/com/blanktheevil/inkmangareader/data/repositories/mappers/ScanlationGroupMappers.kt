package com.blanktheevil.inkmangareader.data.repositories.mappers

import com.blanktheevil.inkmangareader.data.dto.objects.ScanlationGroupDto
import com.blanktheevil.inkmangareader.data.models.ScanlationGroup

fun ScanlationGroupDto.toScanlationGroup(): ScanlationGroup = ScanlationGroup(
    id = id,
    name = attributes.name,
    website = attributes.website
)