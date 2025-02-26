package com.blanktheevil.inkmangareader.data.repositories

import com.blanktheevil.inkmangareader.data.ContentFilter
import com.blanktheevil.inkmangareader.data.ContentRatings
import com.blanktheevil.inkmangareader.data.Sorting
import com.blanktheevil.inkmangareader.data.Tags
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchParams(
    val limit: Int = 5,
    val offset: Int = 0,
    val search: String,
    val contentRating: ContentRatings = ContentFilter.DEFAULT_RATINGS,
    val order: Pair<String, String>? = Sorting.MAP.values.elementAt(1),
    val publicationDemographic: List<String>? = null,
    val status: List<String>? = null,
    val includedTags: List<String>? = null,
    val excludedTags: List<String>? = null,
    val includedTagsMode: Tags.Mode? = null,
    val excludedTagsMode: Tags.Mode? = null,
    val authors: List<String>? = null,
    val artists: List<String>? = null,
    val year: String? = null,
)
