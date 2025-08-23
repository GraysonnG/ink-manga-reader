package com.blanktheevil.inkmangareader.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blanktheevil.inkmangareader.settings.SettingsState
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class SettingsModel(
    @PrimaryKey
    val key: Int = 1,
    val data: SettingsState,
)

fun SettingsState.toModel(): SettingsModel =
    SettingsModel(
        key = this.version,
        data = this
    )