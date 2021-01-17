package com.audigint.throwback.util

import com.audigint.throwback.data.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyMetadataService @Inject constructor(val spotifyWebApiHelper: SpotifyWebApiHelper) :
    MetadataService {
    override var metadataMap = mutableMapOf<String, TrackMetadata>()

    override suspend fun fetchMetadata(songs: List<Song>) {
        val idsMissingMetadata = songs.map { it.id }.toMutableList()
            .also { songIds ->
                songIds.removeAll {
                    // Don't retrieve the artwork URL if we have it already
                    metadataMap.containsKey(it)
                }
            }
        Timber.d(idsMissingMetadata.joinToString(",", "", ""))

        if (idsMissingMetadata.isNotEmpty()) {
            try {
                withContext(Dispatchers.IO) {
                    val tracks =
                        spotifyWebApiHelper.getTracks(
                            idsMissingMetadata.joinToString(
                                ",",
                                "",
                                ""
                            )
                        ).tracks
                    for (track in tracks) {
                        // Use smallest image
                        metadataMap[track.id] = TrackMetadata(
                            track.name,
                            track.artists.joinToString(", ", "", "") { it.name },
                            track.album.images.minByOrNull { it.height }?.url ?: ""
                        )
                    }
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable)
            }
        }
    }

    override fun getMetadataForId(songId: String?) = metadataMap[songId]
}