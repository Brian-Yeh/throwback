package com.audigint.throwback.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.audigint.throwback.utill.Event
import com.audigint.throwback.data.auth.SpotifyUser

enum class LoginEvent {
    RequestLogIn, RequestLogOut
}

interface AuthViewModelDelegate {
    /**
     * Live updated value of the current user
     */
    val currentUserInfo: LiveData<SpotifyUser?>

    /**
     * Emits Events when a sign-in event should be attempted
     */
    val performLoginEvent: MutableLiveData<Event<LoginEvent>>

    /**
     * Emit an Event on performSignInEvent to request sign-in
     */
    suspend fun emitLogInRequest()

    /**
     * Emit an Event on performSignInEvent to request sign-out
     */
    suspend fun emitLogOutRequest()

    fun observeSignedInUser(): LiveData<Boolean>

    fun observeRegisteredUser(): LiveData<Boolean>

    fun isSignedIn(): Boolean
}