package com.audigint.throwback

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.audigint.throwback.data.SongRepository
import com.audigint.throwback.ui.PlayerFragmentDirections
import com.audigint.throwback.ui.QueueListDialogFragment
import com.audigint.throwback.ui.auth.LOGIN_REQUEST_CODE
import com.audigint.throwback.ui.auth.LoginFragmentDirections
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

private val LOG_TAG = "MainActivity"
private val CLIENT_ID = "e504f7f2aa3145bc9280c67c210de1fb"
private val REDIRECT_URI = "com.audigint.throwback://"
private var mSpotifyAppRemote: SpotifyAppRemote? = null
private var songRepository: SongRepository? = null
private lateinit var navController: NavController
private var signedIn: Boolean = false

private val DIALOG_LOGIN = "dialog_login"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)

        // TODO: Check login state
//        if (!signedIn) {
//            val action = PlayerFragmentDirections.actionPlayerFragmentToLoginFragment()
//            navController.navigate(action)
//        }

        // TODO: Use Database
//        val songsDao = SongRoomDatabase.getDatabase(applicationContext).songDao()
//        songRepository = SongRepository(songsDao)
    }

    override fun onStart() {
        super.onStart()
//        val songsArray = songRepository?.getSongsByYearAndPosition(2012, 10)
//        if (songsArray == null) {
//            Log.w(LOG_TAG, "Failed to get songs")
//        } else {
//            for (song in songsArray.orEmpty()) {
//                Log.d(LOG_TAG, "${song.title}")
//            }
//        }
    }

    private fun connect() {
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote
                    Timber.d("Connected! Yay!")

                    // Now you can start interacting with App Remote
                    connected()
                }

                override fun onFailure(throwable: Throwable) {
                    Timber.e(throwable)

                    // Something went wrong when attempting to connect! Handle errors here
                }
            })
    }

    private fun connected() {
        mSpotifyAppRemote?.let {
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            it.playerApi.play(playlistURI)

            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Timber.d("%s by %s", track.name, track.artist.name)
            }
        }
    }

//    private fun signOut() {
//        mSpotifyAppRemote?.let {
//            it.playerApi.pause()
//            SpotifyAppRemote.disconnect(it)
//        }
//        AuthorizationClient.clearCookies(this)
//    }

    override fun onStop() {
        super.onStop()
        mSpotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val resp: AuthenticationResponse = AuthenticationClient.getResponse(resultCode, data)

        if (requestCode == LOGIN_REQUEST_CODE) {
            Timber.d("Spotify login request returned")
            // TODO: check scopes
            val action = LoginFragmentDirections.actionLoginFragmentToPlayerFragment()
            navController.navigate(action)
        }
    }

    fun showQueue() {
        Timber.d("showQueue")
        QueueListDialogFragment().show(supportFragmentManager, "queue")
    }
}