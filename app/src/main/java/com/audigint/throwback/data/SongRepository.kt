package com.audigint.throwback.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface SongRepository {
    fun setAndGetCurrentQueue(year: Int): Flow<List<Song>>
}

class DefaultSongRepository @Inject constructor(
    private val songDao: SongDao,
    private val sharedPreferences: SharedPreferencesStorage
) : SongRepository {

    override fun setAndGetCurrentQueue(year: Int): Flow<List<Song>> {
        sharedPreferences.year = year
        return songDao.getSongsByYearAndPosition(
            year,
            sharedPreferences.position
        )
    }
}