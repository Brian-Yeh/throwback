package com.audigint.throwback.util

import com.audigint.throwback.data.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyArtworkService @Inject constructor(val spotifyWebApiHelper: SpotifyWebApiHelper) {
    private var artworkUrlMap = mutableMapOf<String, String>()

    suspend fun fetchArtworkUrls(songs: List<Song>) {
        val idsMissingArtwork = songs.map { it.id }.toMutableList()
            .also { songIds ->
                songIds.removeAll {
                    // Don't retrieve the artwork URL if we have it already
                    artworkUrlMap.containsKey(it)
                }
            }
        Timber.d(idsMissingArtwork.joinToString(",", "", ""))

        if (idsMissingArtwork.isNotEmpty()) {
            try {
                withContext(Dispatchers.IO) {
                    val tracks =
                        spotifyWebApiHelper.getTracks(
                            idsMissingArtwork.joinToString(
                                ",",
                                "",
                                ""
                            )
                        ).tracks
                    for (track in tracks) {
                        // Use smallest image
                        artworkUrlMap[track.id] =
                            track.album.images.minByOrNull { it.height }?.url ?: ""
                    }
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable)
            }
        }
    }

    fun getArtworkUrlForId(songId: String?) = artworkUrlMap[songId] ?: ""
}