package com.blanktheevil.inkmangareader.helpers

import java.util.UUID

fun String.isUUID(): Boolean =
    try {
        UUID.fromString(this)
        true
    } catch (e: Exception) {
        false
    }
