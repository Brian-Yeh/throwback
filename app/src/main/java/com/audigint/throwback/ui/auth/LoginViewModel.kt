package com.audigint.throwback.ui.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel @ViewModelInject constructor(
    authViewModelDelegate: AuthViewModelDelegate
) : ViewModel(), AuthViewModelDelegate by authViewModelDelegate {

    fun onSignIn() {
        Timber.d("Sign in Clicked")
        viewModelScope.launch {
            emitLogInRequest()
        }
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