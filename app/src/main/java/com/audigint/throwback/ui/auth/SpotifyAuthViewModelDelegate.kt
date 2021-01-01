package com.audigint.throwback.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.audigint.throwback.util.Event
import com.audigint.throwback.data.auth.SpotifyUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SpotifyAuthViewModelDelegate : AuthViewModelDelegate {
    override val performLoginEvent = MutableLiveData<Event<LoginEvent>>()

    override val currentUserInfo: LiveData<SpotifyUser?>
        get() = TODO("Not yet implemented")

    override suspend fun emitLogInRequest() = withContext(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            performLoginEvent.value = Event(LoginEvent.RequestLogIn)
        }
    }

    override suspend fun emitLogOutRequest() = withContext(Dispatchers.Main) {
        performLoginEvent.value = Event(LoginEvent.RequestLogOut)
    }

    override fun observeSignedInUser(): LiveData<Boolean> {
        TODO("Not yet implemented")
    }

    override fun observeRegisteredUser(): LiveData<Boolean> {
        TODO("Not yet implemented")
    }

    override fun isSignedIn(): Boolean {
        TODO("Not yet implemented")
    }
}