package com.blanktheevil.inkmangareader.adapters

import com.blanktheevil.inkmangareader.data.Order
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class OrderAdapter : JsonAdapter<Order>(){
    override fun fromJson(jsonReader: JsonReader): Order? {
        return when (jsonReader.nextString()) {
            "none" -> Order.None
            "relevant" -> Order.Relevant
            "latestUpload" -> Order.LatestUpload
            "oldestUpload" -> Order.OldestUpload
            "titleAsc" -> Order.TitleAsc
            "titleDesc" -> Order.TitleDesc
            "ratingHigh" -> Order.RatingHigh
            "ratingLow" -> Order.RatingLow
            "followsHigh" -> Order.FollowsHigh
            "followsLow" -> Order.FollowsLow
            "recentDesc" -> Order.RecentDesc
            "recentAsc" -> Order.RecentAsc
            "yearAsc" -> Order.YearAsc
            "yearDesc" -> Order.YearDesc
            "null" -> null
            else -> throw IllegalArgumentException("Unknown order type")
        }
    }

    override fun toJson(jsonWriter: JsonWriter, order: Order?) {
        jsonWriter.value(
            when (order) {
                is Order.None -> "none"
                is Order.Relevant -> "relevant"
                is Order.LatestUpload -> "latestUpload"
                is Order.OldestUpload -> "oldestUpload"
                is Order.TitleAsc -> "titleAsc"
                is Order.TitleDesc -> "titleDesc"
                is Order.RatingHigh -> "ratingHigh"
                is Order.RatingLow -> "ratingLow"
                is Order.FollowsHigh -> "followsHigh"
                is Order.FollowsLow -> "followsLow"
                is Order.RecentDesc -> "recentDesc"
                is Order.RecentAsc -> "recentAsc"
                is Order.YearAsc -> "yearAsc"
                is Order.YearDesc -> "yearDesc"
                null -> "null"
            }
        )
    }
}