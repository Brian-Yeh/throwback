package com.audigint.throwback.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.audigint.throwback.data.Song
import com.audigint.throwback.data.SongRepository

class QueueViewModel @ViewModelInject constructor(
    repository: SongRepository
) : ViewModel() {
    val currentQueue: LiveData<List<Song>> = repository.getSongsByYearAndPosition(2016, 50).asLiveData()
}