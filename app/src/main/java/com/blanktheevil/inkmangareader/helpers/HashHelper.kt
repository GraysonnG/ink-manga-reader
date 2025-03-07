package com.blanktheevil.inkmangareader.helpers

import java.security.MessageDigest

fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256")
        .digest(this.toByteArray())
    return bytes.joinToString("") {
        "%02x".format(it)
    }
}