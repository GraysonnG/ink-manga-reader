package com.blanktheevil.inkmangareader.settings

import com.blanktheevil.inkmangareader.data.ContentFilter
import com.blanktheevil.inkmangareader.data.ContentRatings
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SettingsState(
    val version: Int = 1,
    val dataSaver: Boolean = false,
    val contentFilter: ContentRatings = ContentFilter.DEFAULT_RATINGS,
)
