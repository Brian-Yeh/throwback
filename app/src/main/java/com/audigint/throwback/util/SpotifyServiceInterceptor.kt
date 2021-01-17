package com.audigint.throwback.util

import com.audigint.throwback.data.PreferencesStorage
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class SpotifyServiceInterceptor @Inject constructor(prefs: PreferencesStorage) :
    Interceptor {
    private var accessToken = prefs.accessToken

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