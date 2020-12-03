package com.audigint.throwback.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.audigint.throwback.Event

class PlayerViewModel : ViewModel() {
    val showQueue = MutableLiveData<Event<Boolean>>()

    fun showQueue() {
        showQueue.value = Event(true)
    }
}