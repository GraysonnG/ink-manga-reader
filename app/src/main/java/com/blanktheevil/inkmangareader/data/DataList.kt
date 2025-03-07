package com.blanktheevil.inkmangareader.data

import com.blanktheevil.inkmangareader.data.models.BaseItem
import com.blanktheevil.inkmangareader.helpers.sha256
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataList<T>(
    val items: List<T>,
    val title: String? = null,
    val offset: Int = 0,
    val limit: Int = 0,
    val total: Int = -1,
    val extras: Map<String, String>? = null,
)

fun <T> emptyDataList(): DataList<T> = DataList(emptyList())

val DataList<out BaseItem>.id: String get() = items.joinToString("") { it.id }.sha256()

inline fun <T, R> DataList<T>.map(transform: (T) -> R) = DataList(
    title = title,
    items = items.map(transform),
    offset = offset,
    limit = limit,
    total = total,
    extras = extras,
)

operator fun <T> DataList<T>.plus(other: DataList<T>): DataList<T> {
    return DataList(
        items = this.items + other.items,
        title = this.title,
        offset = this.offset,
        limit = this.limit,
        total = this.total + other.total,
        extras = this.extras?.plus(other.extras ?: emptyMap()),
    )
}