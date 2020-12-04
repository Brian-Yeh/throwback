package com.audigint.throwback.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.audigint.throwback.utill.Event
import com.audigint.throwback.data.Song

class PlayerViewModel : ViewModel() {
    val showQueue = MutableLiveData<Event<Boolean>>()
    var isPlaying = false
    lateinit var currentSong: Song

    init {

    }

    fun showQueue() {
        showQueue.value = Event(true)
    }
}