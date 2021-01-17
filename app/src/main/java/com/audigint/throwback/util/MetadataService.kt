package com.audigint.throwback.util

import com.audigint.throwback.data.Song

interface MetadataService {
    var metadataMap: MutableMap<String, TrackMetadata>

    suspend fun fetchMetadata(songs: List<Song>)
    fun getMetadataForId(songId: String?): TrackMetadata?
}