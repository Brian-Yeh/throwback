package com.audigint.throwback.testUtils.fakes

import com.audigint.throwback.data.Song
import com.audigint.throwback.data.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeSongRepository : SongRepository {
    private val song1 = Song(2020, 1, "id1", "Song1", "Fake")
    private val song2 = Song(2020, 2, "id2", "Song2", "Fake")

    override fun setAndGetCurrentQueue(year: Int): Flow<List<Song>> {
        return flowOf(listOf(song1, song2))
    }
}