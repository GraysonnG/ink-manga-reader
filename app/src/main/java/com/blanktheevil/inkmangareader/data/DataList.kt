package com.blanktheevil.inkmangareader.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataList<T>(
    val items: List<T>,
    val title: String? = null,
    val offset: Int = 0,
    val limit: Int = 0,
    val total: Int = -1,
)

fun <T> emptyDataList(): DataList<T> = DataList(emptyList())