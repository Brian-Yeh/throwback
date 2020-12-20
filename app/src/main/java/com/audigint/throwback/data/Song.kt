package com.audigint.throwback.data

import androidx.room.Entity

@Entity(tableName = "topChartSongs", primaryKeys = ["year", "position", "title"])
data class Song (
    val year: Int,
    val position: Int,
    val id: String?,
    val title: String,
    val artist: String?
) {
    val uri: String?
        get() = if (id.isNullOrEmpty()) null else "spotify:track:$id"
}