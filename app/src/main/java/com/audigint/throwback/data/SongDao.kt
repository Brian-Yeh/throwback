package com.audigint.throwback.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM topchartsongs WHERE year >= (:year - 4) AND year <= :year AND position <= :position AND id IS NOT '' ORDER BY RANDOM()")
    fun getSongsByYearAndPosition(year: Int, position: Int): Flow<List<Song>>
}