package com.audigint.throwback.util

import com.audigint.throwback.util.models.Tracks

interface SpotifyWebApiHelper {
    suspend fun getTracks(trackIds: String): Tracks
}