package com.audigint.throwback.util

import com.audigint.throwback.util.models.Tracks
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifyWebApiService {
    @GET("tracks")
    suspend fun getTracks(@Query("ids") trackIds: String): Tracks
}