package com.audigint.throwback.data

import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.audigint.throwback.util.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

class SongRoomDatabaseTest {
    private lateinit var songDao: SongDao
    private lateinit var db: SongRoomDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = SongRoomDatabase.getDatabase(context)
        songDao = db.songDao()
    }

    @Test
    fun shouldReturn50ItemsMax_whenGettingSongs() = runBlocking {
        val songs = songDao.getSongsByYearAndPosition(Constants.DEFAULT_YEAR, 60).first()
        assertTrue(songs.size <= 50)
    }

    @Test
    fun yearsShouldBeWithinFour_whenGettingSongs() = runBlocking {
        val songs = songDao.getSongsByYearAndPosition(2012, 60).first()
        assertTrue(songs.isNotEmpty())
        assertTrue(songs.all {
            it.year in 2008..2012
        })
    }

    @After
    fun closeDb() {
        db.close()
    }
}