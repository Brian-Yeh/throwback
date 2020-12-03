package com.audigint.throwback.data

import javax.inject.Inject

class SongRepository @Inject constructor(private val songDao: SongDao) {
    fun getSongsByYearAndPosition(year: Int, position: Int) = songDao.getSongsByYearAndPosition(year, position)
}