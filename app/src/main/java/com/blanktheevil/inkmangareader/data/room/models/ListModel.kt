package com.blanktheevil.inkmangareader.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.models.BaseItem
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class ListModel(
    @PrimaryKey val key: String,
    val ids: List<String>,
    val limit: Int,
    val offset: Int,
    val total: Int,
    val title: String?,
    val extras: Map<String, String>?,
)

fun DataList<out BaseItem>.toModel(
    key: String = createRoomKey()
): ListModel =
    ListModel(
        key = key,
        ids = items.map { it.id },
        limit = limit,
        offset = offset,
        total = total,
        title = title,
        extras = extras,
    )

fun DataList<out BaseItem>.createRoomKey(): String =
    items.joinToString { it.id.takeLast(4) }
