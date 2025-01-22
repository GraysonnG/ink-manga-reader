package com.blanktheevil.inkmangareader.helpers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 2022-12-19T17:02:29
fun getCreatedAtSinceString(amountOfMonths: Long = 6): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val now = LocalDateTime.now()
    val monthsAgo = now.minusMonths(amountOfMonths)
    return formatter.format(monthsAgo)
}