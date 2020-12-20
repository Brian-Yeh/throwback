package com.audigint.throwback.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.audigint.throwback.utill.Event
import com.audigint.throwback.data.Song
import com.audigint.throwback.utill.QueueManager
import com.audigint.throwback.utill.SpotifyManager
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

    private val _currentSongTitle = MutableLiveData<String>()
    val currentSongTitle: LiveData<String>
        get() = _currentSongTitle

    private val _currentSongArtist = MutableLiveData<String>()
    val currentSongArtist: LiveData<String>
        get() = _currentSongArtist

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    private var startOfSession = true
    private var hasPlayed = false
    private var hasQueuedNext = false
    private var trackNum = 0

    init {
        Timber.d("PlayerViewModel init")
        spotifyManager.subscribeToPlayerState()?.setEventCallback {
            checkPlaybackState(it)
        }
    }

    fun showQueue() {
        showQueue.value = Event(true)
    }

    fun onPlayClick() {
        viewModelScope.launch {
            if (!hasPlayed) {
                startSession()
                hasPlayed = true
            } else if (spotifyManager.isPlaying()) {
                spotifyManager.pause()
                _isPlaying.value = false
            } else {
                spotifyManager.resume()
                _isPlaying.value = true
            }
        }
    }

    fun onNextClick() {
        playNext()
    }

    fun startSession() {
        currentSong.value?.let { spotifyManager.addToQueue(it) }
    }

    fun startCurrentSong() {
        currentSong.value?.let {
            spotifyManager.play(it)
            _isPlaying.value = true
        }
    }

    private fun queueNextSong() {
        if (currentQueue.isNotEmpty()) {
            val nextTrackNum = (trackNum + 1) % currentQueue.size
            spotifyManager.addToQueue(currentQueue[nextTrackNum])
            hasQueuedNext = true
        }
    }

    fun playNext() {
        trackNum = (trackNum + 1) % currentQueue.size
        _currentSong.value = currentQueue[trackNum]
        spotifyManager.next()
    }

    // TODO: Remove
    fun playPrev() {
        // Wrap around to the end of Queue if we're at 0
        trackNum = if (trackNum == 0) currentQueue.size - 1 else trackNum - 1
        _currentSong.value = currentQueue[trackNum]
        startCurrentSong()
    }

    fun checkPlaybackState(playerState: PlayerState) {
        with(playerState) {
            if (track != null && currentQueue.isNotEmpty()) {
                Timber.d("playerState duration = $playbackPosition / ${track.duration}")

                if (startOfSession) {
                    // Cycle through the queue until we reach the first song
                    if (track.uri != currentSong.value?.uri) {
                        spotifyManager.next()
                    } else {
                        Timber.d("${track.uri} == ${currentSong.value?.uri}, first song found")
                        startOfSession = false
                        startCurrentSong()
                    }
                } else {
                    if (track.uri == currentSong.value?.uri && _currentSongTitle.value != track.name) {
                        _currentSongTitle.value = track.name
                        _currentSongArtist.value = track.artist.name
                    }

                    if (!hasQueuedNext && playbackPosition == 0L) {
                        queueNextSong()
                    } else if (playbackPosition > 0L) {
                        hasQueuedNext = false
                    }
                }
            }
        }
    }
}
