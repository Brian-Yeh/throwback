package com.audigint.throwback.data

class SongRepository(private val songDao: SongDao) {
    fun getSongsByYearAndPosition(year: Int, position: Int) = songDao.getSongsByYearAndPosition(year, position)
}