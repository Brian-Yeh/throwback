package com.audigint.throwback.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audigint.throwback.ui.models.QueueItem
import com.audigint.throwback.util.QueueManager
import com.audigint.throwback.util.SpotifyArtworkService
import kotlinx.coroutines.launch

class QueueViewModel @ViewModelInject constructor(
    queueManager: QueueManager,
    artworkService: SpotifyArtworkService
) : ViewModel() {
    private val _currentQueue = MutableLiveData<List<QueueItem>>()
    val currentQueue: LiveData<List<QueueItem>> = _currentQueue

    init {
        viewModelScope.launch {
            queueManager.queue.value?.let { songs ->
                artworkService.fetchArtworkUrls(songs)
                val queueItems: List<QueueItem> = songs.map {
                    QueueItem(it.id, it.title, it.artist, artworkService.getArtworkUrlForId(it.id))
                }
                _currentQueue.value = queueItems
            }
        }
    }

}