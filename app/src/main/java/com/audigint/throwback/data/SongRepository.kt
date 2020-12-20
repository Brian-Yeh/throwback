package com.audigint.throwback.data

import android.content.SharedPreferences
import com.audigint.throwback.utill.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongRepository @Inject constructor(private val songDao: SongDao, private val sharedPreferences: SharedPreferences) {
    val currentQueue: Flow<List<Song>>
        get() = songDao.getSongsByYearAndPosition(
            sharedPreferences.getInt(Constants.PREFS_KEY_YEAR, 2016),
            sharedPreferences.getInt(Constants.PREFS_KEY_POSITION, 10)
        )
}