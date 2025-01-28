package com.blanktheevil.inkmangareader.data.repositories

import com.blanktheevil.inkmangareader.data.ContentRatings
import com.blanktheevil.inkmangareader.data.Tags


sealed class MangaListRequest(
    val type: String
) {
    data class Generic(
        val data: List<String>,
        val name: String? = null,
    ) : MangaListRequest(type = data.joinToString { it.takeLast(4) } + name.orEmpty() )
    data object Popular : MangaListRequest(type = "Popular")
    data object Recent : MangaListRequest(type = "Recent")
    data object Seasonal : MangaListRequest(type = "Seasonal")
    data class Follows(val userId: String) : MangaListRequest(type = "Follows-$userId")
    data class Search(
        val artists: List<String>?,
        val authors: List<String>?,
        val contentRatings: ContentRatings,
        val excludedTags: List<String>?,
        val excludedTagsMode: Tags.Mode?,
        val includedTags: List<String>?,
        val includedTagsMode: Tags.Mode?,
        val order: Pair<String, String>?,
        val publicationDemographic: List<String>?,
        val status: List<String>?,
        val title: String?,
        val year: String?,
    ) : MangaListRequest(type = "Search")
}
