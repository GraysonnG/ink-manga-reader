package com.blanktheevil.inkmangareader.data

import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.data.models.Tag

const val DEFAULT_LIST_LIMIT = 20

sealed class ContentFilter(val nameRes: Int, val value: ContentRating) {
    data object Safe : ContentFilter(R.string.content_filter_safe, "safe")
    data object Suggestive : ContentFilter(R.string.content_filter_suggestive, "suggestive")
    data object Erotica : ContentFilter(R.string.content_filter_erotica, "erotica")
    data object Nsfw : ContentFilter(R.string.content_filter_nsfw, "pornographic")

    companion object {
        val list by lazy {
            listOf(Safe, Suggestive, Erotica, Nsfw)
        }
        val default_ratings = listOf(Safe, Suggestive)
        val DEFAULT_RATINGS = default_ratings.map { it.value }
    }
}

sealed class Order(val nameRes: Int, val mapping: Pair<String, String>?) {
    data object None : Order(R.string.order_none, null)
    data object Relevant : Order(R.string.order_best_match, "relevance" to "desc")
    data object LatestUpload : Order(R.string.order_latest_upload, "latestUploadedChapter" to "desc")
    data object OldestUpload : Order(R.string.order_oldest_upload, "latestUploadedChapter" to "asc")
    data object TitleAsc : Order(R.string.order_title_ascending, "title" to "asc")
    data object TitleDesc : Order(R.string.order_title_descending, "title" to "desc")
    data object RatingHigh : Order(R.string.order_rating_highest, "rating" to "desc")
    data object RatingLow : Order(R.string.order_rating_lowest, "rating" to "asc")
    data object FollowsHigh : Order(R.string.order_follows_highest, "followedCount" to "desc")
    data object FollowsLow : Order(R.string.order_follows_lowest, "followedCount" to "asc")
    data object RecentDesc : Order(R.string.order_recent_descending, "createdAt" to "desc")
    data object RecentAsc : Order(R.string.order_recent_ascending, "createdAt" to "asc")
    data object YearAsc : Order(R.string.order_year_ascending, "year" to "asc")
    data object YearDesc : Order(R.string.order_year_descending, "year" to "desc")

    companion object {
        val list by lazy {
            listOf(
                None,
                Relevant,
                LatestUpload,
                OldestUpload,
                TitleAsc,
                TitleDesc,
                RatingHigh,
                RatingLow,
                FollowsHigh,
                FollowsLow,
                RecentDesc,
                RecentAsc,
                YearAsc,
                YearDesc
            )
        }
    }
}

sealed class Status(val nameRes: Int, val value: String) {
    data object Ongoing : Status(R.string.status_ongoing, "ONGOING")
    data object Completed : Status(R.string.status_completed, "COMPLETED")
    data object Hiatus : Status(R.string.status_hiatus, "HIATUS")
    data object Cancelled : Status(R.string.status_cancelled, "CANCELLED")

    companion object {
        val list by lazy {
            listOf(Ongoing, Completed, Hiatus, Cancelled)
        }
    }
}

sealed class Demographic(val nameRes: Int, val value: String) {
    data object Shounen : Demographic(R.string.demographic_shounen, "shounen")
    data object Shoujo : Demographic(R.string.demographic_shoujo, "shoujo")
    data object Seinen : Demographic(R.string.demographic_seinen, "seinen")
    data object Josei : Demographic(R.string.demographic_josei, "josei")

    companion object {
        val list by lazy {
            listOf(Shounen, Shoujo, Seinen, Josei)
        }
    }
}

object Tags {
    enum class Mode {
        AND,
        OR
    }

    val PopularFilters by lazy {
        listOf(
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
}

typealias ContentRating = String
typealias ContentRatings = List<ContentRating>