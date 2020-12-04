package com.audigint.throwback.utill

import android.content.Context
import android.graphics.Bitmap
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.Track
import timber.log.Timber

object SpotifyManager {
    private const val CLIENT_ID = "e504f7f2aa3145bc9280c67c210de1fb"
    private const val REDIRECT_URI = "com.audigint.throwback://"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val connectionParams = ConnectionParams.Builder(CLIENT_ID)
        .setRedirectUri(REDIRECT_URI)
        .showAuthView(false)
        .build()

    enum class PlaybackState {
        PLAYING,
        PAUSED
    }

    fun getPlaybackState(state: (PlaybackState) -> Unit) {
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback { result ->
            if (result.track.uri == null || result.isPaused) {
                state(PlaybackState.PAUSED)
            } else {
                state(PlaybackState.PLAYING)
            }
        }
    }

    fun connect(context: Context, result: (SpotifyConnectionResult) -> Unit) {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                result(SpotifyConnectionResult.Success)
                return
            }
        }
        SpotifyAppRemote.connect(context, connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    this@SpotifyManager.spotifyAppRemote = spotifyAppRemote
                    Timber.d("Spotify successfully connected")

//                    connected()
                    result(SpotifyConnectionResult.Success)
                }

                override fun onFailure(throwable: Throwable) {
                    Timber.e(throwable)
                    result(SpotifyConnectionResult.Error(throwable.message.toString()))
                }
            })
    }

    private fun connected() {
        spotifyAppRemote?.let {
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            it.playerApi.play(playlistURI)

            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Timber.d("%s by %s", track.name, track.artist.name)
            }
        }
    }

    fun play(uri: String) {
        spotifyAppRemote?.playerApi?.play(uri)
    }

    fun pause() {
        spotifyAppRemote?.playerApi?.pause()
    }

    fun resume() {
        spotifyAppRemote?.playerApi?.resume()
    }

    fun getCurrentTrack(handler: (track: Track) -> Unit) {
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback { result ->
            handler(result.track)
        }
    }

    fun getImage(imageUri: ImageUri, handler: (Bitmap) -> Unit)  {
        spotifyAppRemote?.imagesApi?.getImage(imageUri)?.setResultCallback {
            handler(it)
        }
    }

    fun getCurrentTrackImage(handler: (Bitmap) -> Unit)  {
        getCurrentTrack {
            getImage(it.imageUri) {
                handler(it)
            }
        }
    }

    fun isConnected(): Boolean = spotifyAppRemote?.isConnected ?: false
//    private fun signOut() {
//        mSpotifyAppRemote?.let {
//            it.playerApi.pause()
//            SpotifyAppRemote.disconnect(it)
//        }
//        AuthorizationClient.clearCookies(this)
//    }

    fun disconnect() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }
}