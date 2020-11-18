package com.audigint.throwback.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topChartSongs", primaryKeys = ["year", "position", "title"])
data class Song (
    val year: Int,
    val position: Int,
    val id: String?,
    val title: String,
    val artist: String?
)