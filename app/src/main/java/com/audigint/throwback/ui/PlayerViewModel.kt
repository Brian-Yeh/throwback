package com.audigint.throwback.ui

import android.graphics.Bitmap
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audigint.throwback.data.Song
import com.audigint.throwback.di.MainDispatcher
import com.audigint.throwback.ui.models.QueueItem
import com.audigint.throwback.util.Event
import com.audigint.throwback.util.QueueManager
import com.audigint.throwback.util.SpotifyManager
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerViewModel @ViewModelInject constructor(
    private val spotifyManager: SpotifyManager,
    private val queueManager: QueueManager,
    @MainDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    val currentQueueData: LiveData<List<QueueItem>> = queueManager.queue

    private var _currentQueue: List<QueueItem> = emptyList()
    var currentQueue: List<QueueItem>
        get() = _currentQueue
        set(newQueue) {
            trackNum = 0
            _currentQueue = newQueue.also {
                onNewQueue()
                it[trackNum].let { queueItem ->
                    _currentSong.value = queueItem.song
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

    private var nextSong: Song? = null

    private var trackWasStarted = false
    private var playingNext = false
    private var startOfSession = true

    private var trackNum = 0
    private var currentImageUri: ImageUri? = null

    lateinit var yearSpinner: Spinner
    val yearSpinnerClickListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            queueManager.year = adapterView?.getItemAtPosition(pos) as Int
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {}
    }

    init {
        Timber.d("PlayerViewModel init")
        spotifyManager.subscribeToPlayerState()?.setEventCallback {
            checkPlaybackState(it)
        }
        spotifyManager.pause()
    }

    fun setInitialYear() {
        yearSpinner.setSelection(queueManager.year - yearSpinner.adapter.getItem(0) as Int)
    }

    fun showQueue() {
        showQueue.value = Event(true)
    }

    fun onPlayClick() {
        viewModelScope.launch(dispatcher) {
            when {
                startOfSession -> {
                    startSession()
                }
                spotifyManager.isPlaying() -> {
                    spotifyManager.pause()
                }
                else -> {
                    spotifyManager.resume()
                }
            }
        }
    }

    fun onNextClick() {
        if (startOfSession) startSession() else playNext()
    }

    fun onNewQueue() {
        spotifyManager.pause()
        startOfSession = true
        currentImageUri = null
        trackWasStarted = false
        nextSong = null
        _trackArtwork.value = null
        _trackTitle.value = ""
        _trackArtist.value = ""
    }

    fun startSession() {
        startOfSession = false
        startCurrentSong()
    }

    fun startCurrentSong() {
        currentSong.value?.let {
            spotifyManager.play(it)
        }
        setNextSong()
    }

    fun setNextSong() {
        Timber.d("Set next song to ${currentQueue[getNextSongIndex()].song?.title}")
        nextSong = currentQueue[getNextSongIndex()].song
    }

    fun playNext() {
        if (!playingNext) {
            playingNext = true
            trackWasStarted = false
            currentQueue[getNextSongIndex()].song?.let {
                spotifyManager.play(it)
            }
        }
    }

    private fun startedNextSong(track: Track): Boolean = track.uri == nextSong?.uri

    private fun onNextSongPlayed() {
        playingNext = false
        trackNum = getNextSongIndex()
        _currentSong.value = currentQueue[trackNum].song
        setNextSong()
    }

    private fun getNextSongIndex() = (trackNum + 1) % currentQueue.size

    private fun setTrackWasStarted(playerState: PlayerState) {
        val position = playerState.playbackPosition
        val duration = playerState.track.duration
        val isPlaying = !playerState.isPaused

        if (!trackWasStarted && position > 0 && duration > 0 && isPlaying) {
            trackWasStarted = true
        }
    }

    fun checkPlaybackState(playerState: PlayerState) {
        with(playerState) {
            _isPlaying.value = !playerState.isPaused

            if (track != null && currentQueue.isNotEmpty() && !startOfSession) {
                if (!trackWasStarted) {
                    // Ensure that the correct next song has been played
                    if (playingNext) {
                        if (startedNextSong(track)) {
                            onNextSongPlayed()
                        }
                        updateMetadataAndArt(this)
                        return
                    } else if (currentSong.value?.uri != track.uri) {
                        startCurrentSong()
                    }
                }
                setTrackWasStarted(this)

                val hasEnded = trackWasStarted && !playingNext && playbackPosition == 0L
                val songPlayingIsNotRight = !playingNext && track?.uri != currentSong.value?.uri

                if (hasEnded) {
                    spotifyManager.pause()
                    playNext()
                } else if (songPlayingIsNotRight && !isPaused) {
                    spotifyManager.pause()
                    startCurrentSong()
                }
                updateMetadataAndArt(this)
            }
        }
    }

    private fun loadArtworkBitmap(uri: ImageUri) {
        viewModelScope.launch(dispatcher) {
            _trackArtwork.value = spotifyManager.getArtworkImage(uri)
            currentImageUri = uri
        }
    }

    fun updateMetadataAndArt(playerState: PlayerState) {
        with(playerState) {
            if (_trackTitle.value != track.name) {
                _trackTitle.value = track.name
                _trackArtist.value = track.artist.name
            }

            if (currentImageUri != track.imageUri) {
                loadArtworkBitmap(track.imageUri)
            }
        }
    }
}
