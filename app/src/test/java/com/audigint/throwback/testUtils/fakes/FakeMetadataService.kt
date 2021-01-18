package com.audigint.throwback.testUtils.fakes

import com.audigint.throwback.data.Song
import com.audigint.throwback.util.MetadataService
import com.audigint.throwback.util.TrackMetadata

class FakeMetadataService : MetadataService {
    override var metadataMap = mutableMapOf<String, TrackMetadata>()

    override suspend fun fetchMetadata(songs: List<Song>) {
        songs.forEach {
            metadataMap.putIfAbsent(it.id ?: "", TrackMetadata(it.title, it.artist ?: "", ""))
        }
    }

    override fun getMetadataForId(songId: String?) = metadataMap[songId]
}