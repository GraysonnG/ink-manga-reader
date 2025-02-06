package com.blanktheevil.inkmangareader.helpers

import com.blanktheevil.inkmangareader.data.models.Chapter
import kotlin.time.Duration.Companion.days

private val SEVEN_DAYS = 7.days

val Chapter.isNew: Boolean
    get() = isRead != true &&
            availableDate != null &&
            availableDate > System.currentTimeMillis() - SEVEN_DAYS.inWholeMilliseconds
