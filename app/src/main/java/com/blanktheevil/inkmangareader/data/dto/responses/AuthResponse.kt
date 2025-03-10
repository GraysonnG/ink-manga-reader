package com.blanktheevil.inkmangareader.data.dto.responses

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val result: String,
    val token: AuthTokenDto
)

@JsonClass(generateAdapter = true)
data class AuthTokenDto(
    val session: String,
    val refresh: String,
)

@JsonClass(generateAdapter = true)
data class AuthData(
    val username: String,
    val password: String,
)

@JsonClass(generateAdapter = true)
data class Refresh(
    val token: String,
)