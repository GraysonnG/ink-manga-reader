package com.blanktheevil.inkmangareader.data.repositories.mappers

import com.blanktheevil.inkmangareader.data.dto.objects.UserDto
import com.blanktheevil.inkmangareader.data.models.User

fun UserDto.toUser(): User = User(
    id = id,
    username = attributes.username
)