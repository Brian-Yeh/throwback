package com.audigint.throwback.ui

import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.audigint.throwback.util.Event
import com.audigint.throwback.data.Song
import com.audigint.throwback.util.QueueManager
import com.audigint.throwback.util.SpotifyManager
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.PlayerState
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerViewModel @ViewModelInject constructor(
    private val spotifyManager: SpotifyManager,
    queueManager: QueueManager
) : ViewModel() {
    val currentQueueData: LiveData<List<Song>> = queueManager.queue

    private var _currentQueue: List<Song> = emptyList()
    var currentQueue: List<Song>
        get() = _currentQueue
        set(newQueue) {
            trackNum = 0
            _currentQueue = newQueue.also {
                it[trackNum].let { song ->
                    _currentSong.value = song
                }
            }
        }

    val showQueue = MutableLiveData<Event<Boolean>>()

    private val _currentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song>
        get() = _currentSong

    private val _trackTitle = MutableLiveData<String>()
    val trackTitle: LiveData<String>
        get() = _trackTitle

    private val _trackArtist = MutableLiveData<String>()
    val trackArtist: LiveData<String>
        get() = _trackArtist

    private val _trackArtwork = MutableLiveData<Bitmap>()
    val trackArtwork: LiveData<Bitmap>
        get() = _trackArtwork

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    private var startOfSession = true
    private var hasPlayed = false
    private var hasQueuedNext = false
    private var trackNum = 0
    private var lastQueued = ""
    private var currentImageUri: ImageUri? = null

    init {
        Timber.d("PlayerViewModel init")
        spotifyManager.subscribeToPlayerState()?.setEventCallback {
            checkPlaybackState(it)
        }
        spotifyManager.pause()
    }

    fun showQueue() {
        showQueue.value = Event(true)
    }

    fun onPlayClick() {
        viewModelScope.launch {
            if (!hasPlayed) {
                startSession()
            } else if (spotifyManager.isPlaying()) {
                spotifyManager.pause()
            } else {
                spotifyManager.resume()
            }
        }
    }

    fun onNextClick() {
        playNext()
    }

    fun startSession() {
        currentSong.value?.let {
            spotifyManager.addToQueue(it)
            lastQueued = it.id.toString()
            spotifyManager.play(it)
            spotifyManager.next()
        }
    }

    fun startCurrentSong() {
        currentSong.value?.let {
            spotifyManager.play(it)
        }
    }

    private fun queueNextSong() {
        if (currentQueue.isNotEmpty() && !hasQueuedNext) {
            val nextTrackNum = (trackNum + 1) % currentQueue.size
            with(currentQueue[nextTrackNum]) {
                if (lastQueued != this.id) {
                    spotifyManager.addToQueue(this)
                    lastQueued = this.id.toString()
                }
            }
        }
    }

    fun playNext() {
        trackNum = (trackNum + 1) % currentQueue.size
        _currentSong.value = currentQueue[trackNum]
        spotifyManager.next()
    }

    private fun loadArtworkBitmap(uri: ImageUri) {
        viewModelScope.launch {
            _trackArtwork.value = spotifyManager.getArtworkImage(uri)
            currentImageUri = uri
        }
    }

    fun checkPlaybackState(playerState: PlayerState) {
        with(playerState) {
            _isPlaying.value = !playerState.isPaused
            if (track != null && currentQueue.isNotEmpty()) {

                if (startOfSession) {
                    // Cycle through the queue until we reach the first song
                    if (track.uri != currentSong.value?.uri) {
                        spotifyManager.next()
                    } else {
                        startOfSession = false
                        hasPlayed = true
                        startCurrentSong()
                    }
                } else {
                    if (_trackTitle.value != track.name) {
                        _trackTitle.value = track.name
                        _trackArtist.value = track.artist.name
                    }

                    if (currentImageUri != track.imageUri) {
                        loadArtworkBitmap(track.imageUri)
                    }

                    if (!hasQueuedNext && playbackPosition == 0L) {
                        queueNextSong()
                        hasQueuedNext = true
                    } else if (playbackPosition > 0L && hasQueuedNext) {
                        hasQueuedNext = false
                    }
                }
            }
        }
    }
}
