package com.audigint.throwback.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM topchartsongs WHERE year >= (:year - 2) AND year <= :year AND position <= :position AND id IS NOT '' LIMIT 50")
    fun getSongsByYearAndPosition(year: Int, position: Int): Flow<List<Song>>
}