package com.blanktheevil.inkmangareader.data.room.models

interface BaseModel<T> {
    val key: String
    val data: T
}