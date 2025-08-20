package com.blanktheevil.inkmangareader.data.repositories

import com.blanktheevil.inkmangareader.data.ContentFilter
import com.blanktheevil.inkmangareader.data.ContentRatings
import com.blanktheevil.inkmangareader.data.Order
import com.blanktheevil.inkmangareader.data.Tags
import com.blanktheevil.inkmangareader.data.models.Tag
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchParams(
    val limit: Int = 5,
    val offset: Int = 0,
    val search: String?,
    val contentRating: ContentRatings = ContentFilter.DEFAULT_RATINGS,
    val order: Order,
    val publicationDemographic: List<String>? = null,
    val status: List<String>? = null,
    val includedTags: List<Tag>? = null,
    val excludedTags: List<Tag>? = null,
    val includedTagsMode: Tags.Mode? = null,
    val excludedTagsMode: Tags.Mode? = null,
    val authors: List<String>? = null,
    val artists: List<String>? = null,
    val year: String? = null,
    val createdAtSince: String? = null,
)
