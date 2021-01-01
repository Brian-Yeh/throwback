package com.audigint.throwback.util

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.audigint.throwback.data.Song
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume


@Singleton
class SpotifyManager @Inject constructor(@ApplicationContext val context: Context) {
    private val clientID = "e504f7f2aa3145bc9280c67c210de1fb"
    private val redirectURI = "com.audigint.throwback://"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val connectionParams = ConnectionParams.Builder(clientID)
        .setRedirectUri(redirectURI)
        .showAuthView(false)
        .build()

    private val _onSpotifyConnected = MutableLiveData<Event<Boolean>>()
    val onSpotifyConnected: LiveData<Event<Boolean>>
        get() = _onSpotifyConnected

    enum class PlaybackState {
        PLAYING,
        PAUSED
    }

    private fun getPlaybackState(state: (PlaybackState) -> Unit) {
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback { result ->
            if (result.track.uri == null || result.isPaused) {
                state(PlaybackState.PAUSED)
            } else {
                state(PlaybackState.PLAYING)
            }
        }
    }

    suspend fun connect() = suspendCancellableCoroutine<SpotifyConnectionResult> { cont ->
        SpotifyAppRemote.connect(context, connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    this@SpotifyManager.spotifyAppRemote = spotifyAppRemote
                    Timber.d("Spotify successfully connected")
                    if (cont.isActive) {
                        cont.resume(SpotifyConnectionResult.Success)
                        _onSpotifyConnected.value = Event(true)
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    Timber.e(throwable)
                    if (cont.isActive) {
                        cont.resume(SpotifyConnectionResult.Error(throwable.message.toString()))
                        _onSpotifyConnected.value = Event(false)
                    }
                }
            })
    }

    fun play(song: Song) {
        spotifyAppRemote?.playerApi?.let { player ->
            song.uri?.let {
                player.play(it)
                Timber.d("Playing ${song.title}")
            }
        }
    }

    fun pause() {
        spotifyAppRemote?.playerApi?.pause()
        Timber.d("SpotifyManager.pause")
    }

    fun resume() {
        spotifyAppRemote?.playerApi?.resume()
        Timber.d("SpotifyManager.resume")
    }

    fun next() {
        spotifyAppRemote?.playerApi?.skipNext()
        Timber.d("SpotifyManager.next")
    }

    fun prev() {
        spotifyAppRemote?.playerApi?.skipPrevious()
    }

    suspend fun isPlaying(): Boolean = suspendCancellableCoroutine { cont ->
        getPlaybackState {
            if (it == PlaybackState.PLAYING) cont.resume(true)
            else cont.resume(false)
        }
    }

    fun getCurrentTrack(handler: (track: Track) -> Unit) {
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback { result ->
            handler(result.track)
        }
    }

    fun addToQueue(song: Song) {
        song.uri?.let {
            spotifyAppRemote?.playerApi?.queue(it)
            Timber.d("${song.title} added to queue")
        }
    }

    suspend fun getArtworkImage(imageUri: ImageUri) = suspendCancellableCoroutine<Bitmap> { cont ->
        spotifyAppRemote?.imagesApi?.getImage(imageUri)?.setResultCallback {
            cont.resume(it)
        }
    }

    fun subscribeToPlayerState() = spotifyAppRemote?.playerApi?.subscribeToPlayerState()

    fun disconnect() {
        pause()
        SpotifyAppRemote.disconnect(spotifyAppRemote)
    }
}