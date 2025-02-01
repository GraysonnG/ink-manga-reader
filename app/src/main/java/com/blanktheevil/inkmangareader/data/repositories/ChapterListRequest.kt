package com.blanktheevil.inkmangareader.data.repositories

sealed class ChapterListRequest(
    val type: String,
) {
    data class Generic(val ids: List<String>) : ChapterListRequest(type = ids.joinToString { it.takeLast(4) })
    data class Feed(val mangaId: String) : ChapterListRequest(type = "Feed-$mangaId")
    data object Follows : ChapterListRequest(type = "Follows")
}
