package com.blanktheevil.inkmangareader.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScanlationGroup(
    val id: String,
    val name: String,
    val website: String?,
)
