package com.audigint.throwback.ui

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import androidx.hilt.lifecycle.ViewModelInject
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext

private val CLIENT_ID = "e504f7f2aa3145bc9280c67c210de1fb"
private val REDIRECT_URI = "com.audigint.throwback://"

class LoginViewModel @ViewModelInject constructor() : ViewModel() {

    fun onSignIn() {
//        var builder = AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
//
//        builder.setScopes(arrayOf("streaming"))
//        val request = builder.build();
//
//        AuthorizationClient.openLoginInBrowser(activity, request);
    }

     fun signOut() {
         // TODO: Handle player pause and disconnect

//        mSpotifyAppRemote?.let {
//            it.playerApi.pause()
//            SpotifyAppRemote.disconnect(it)
//        }
//        AuthorizationClient.clearCookies(activity)
//        var builder = AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
//
//        builder.setScopes(arrayOf("streaming"))
//        builder.setShowDialog(true);
//        val request = builder.build();
//
//        openLoginInBrowser(this, request)
    }
}