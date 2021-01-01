package com.audigint.throwback.util

import android.content.SharedPreferences
import com.audigint.throwback.util.Constants.PREFS_KEY_ACCESS_TOKEN
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class SpotifyServiceInterceptor @Inject constructor(sharedPreferences: SharedPreferences) :
    Interceptor {
    private var accessToken = sharedPreferences.getString(PREFS_KEY_ACCESS_TOKEN, "")

    fun setAccessToken(token: String) {
        accessToken = token
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        Timber.d("Set header with accessToken = $accessToken")

        val request = chain.request()
        val requestBuilder = request.newBuilder().addHeader("Authorization", "Bearer $accessToken")
        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }
}