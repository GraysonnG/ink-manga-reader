package com.blanktheevil.inkmangareader.data

import com.blanktheevil.inkmangareader.data.models.Tag

const val DEFAULT_LIST_LIMIT = 20

object ContentFilter {
    const val SAFE: ContentRating = "safe"
    const val SUGGESTIVE: ContentRating = "suggestive"
    const val EROTICA: ContentRating = "erotica"
    const val NSFW: ContentRating = "pornographic"

    val DEFAULT_RATINGS: ContentRatings = listOf(
        SAFE,
        SUGGESTIVE,
    )
}

object Sorting {
    val MAP = mapOf(
        "none" to null,
        "bestMatch" to ("relevance" to "desc"),
        "latestUpload" to ("latestUploadedChapter" to "desc"),
        "oldestUpload" to ("latestUploadedChapter" to "asc"),
        "titleAsc" to ("title" to "asc"),
        "titleDesc" to ("title" to "desc"),
        "ratingHigh" to ("rating" to "desc"),
        "ratingLow" to ("rating" to "asc"),
        "followsHigh" to ("followedCount" to "desc"),
        "followsLow" to ("followedCount" to "asc"),
        "recentDesc" to ("createdAt" to "desc"),
        "recentAsc" to ("createdAt" to "asc"),
        "yearAsc" to ("year" to "asc"),
        "yearDesc" to ("year" to "desc"),
    )
}

object Tags {
    enum class Mode {
        AND,
        OR
    }

    val PopularFilters = listOf(
        Tag(id = "391b0423-d847-456f-aff0-8b0cfc03066b", name = "Action", group = "genre"),
        Tag(id = "4d32cc48-9f00-4cca-9b5a-a839f0764984", name = "Comedy", group = "genre"),
        Tag(id = "cdc58593-87dd-415e-bbc0-2ec27bf404cc", name = "Fantasy", group = "genre"),
        Tag(id = "ace04997-f6bd-436e-b261-779182193d3d", name = "Isekai", group = "genre"),
        Tag(id = "ee968100-4191-4968-93d3-f82d72be7e46", name = "Mystery", group = "genre"),
        Tag(id = "423e2eae-a7a2-4a8b-ac03-a8351462d71d", name = "Romance", group = "genre"),
        Tag(id = "e5301a23-ebd9-49dd-a0cb-2add944c7fe9", name = "Slice of Life", group = "genre"),
        Tag(id = "256c8bd9-4904-4360-bf4f-508a76d67183", name = "Sci-Fi", group = "genre"),
        Tag(id = "07251805-a27e-4d59-b488-f0bfbec15168", name = "Thriller", group = "genre"),
    )
}


typealias ContentRating = String
typealias ContentRatings = List<ContentRating>