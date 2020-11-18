package com.audigint.throwback.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SongDao {
    @Query("SELECT * FROM topchartsongs WHERE year >= (:year - 4) AND year <= :year AND position <= :position AND id IS NOT NULL ORDER BY RANDOM()")
    fun getSongsByYearAndPosition(year: Int, position: Int): Array<Song>
}