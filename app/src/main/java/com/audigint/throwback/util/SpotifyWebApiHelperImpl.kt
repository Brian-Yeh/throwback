package com.audigint.throwback.util

import com.audigint.throwback.util.models.Tracks
import javax.inject.Inject

class SpotifyWebApiHelperImpl @Inject constructor(
    private val apiService: SpotifyWebApiService
) : SpotifyWebApiHelper {
    override suspend fun getTracks(trackIds: String): Tracks = apiService.getTracks(trackIds)
}