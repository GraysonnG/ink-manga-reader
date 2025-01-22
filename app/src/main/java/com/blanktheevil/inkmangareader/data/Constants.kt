package com.blanktheevil.inkmangareader.data


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
}


typealias ContentRating = String
typealias ContentRatings = List<ContentRating>