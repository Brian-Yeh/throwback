package com.audigint.throwback

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.audigint.throwback.data.SongRepository
import com.audigint.throwback.data.SongRoomDatabase
import com.audigint.throwback.ui.LoginDialogFragment
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private val LOG_TAG = "MainActivity"
private val CLIENT_ID = "e504f7f2aa3145bc9280c67c210de1fb"
private val REDIRECT_URI = "com.audigint.throwback://"
private var mSpotifyAppRemote: SpotifyAppRemote? = null
private var songRepository: SongRepository? = null

private val DIALOG_LOGIN = "dialog_login"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val signInButton = findViewById<Button>(R.id.signInButton)
//        signInButton?.setOnClickListener() {
//            signIn()
//        }
//
//        val signOutButton = findViewById<Button>(R.id.signOutButton)
//        signOutButton?.setOnClickListener() {
//            signOut()
//        }

        // TODO: Use Database
//        val songsDao = SongRoomDatabase.getDatabase(applicationContext).songDao()
//        songRepository = SongRepository(songsDao)
    }

    override fun onStart() {
        super.onStart()
        Timber.d("Showing Login Dialog")
        LoginDialogFragment().show(supportFragmentManager, DIALOG_LOGIN)
//        val songsArray = songRepository?.getSongsByYearAndPosition(2012, 10)
//        if (songsArray == null) {
//            Log.w(LOG_TAG, "Failed to get songs")
//        } else {
//            for (song in songsArray.orEmpty()) {
//                Log.d(LOG_TAG, "${song.title}")
//            }
//        }
    }

    private fun signIn() {
        var builder = AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(arrayOf("streaming"))
        val request = builder.build();

        AuthorizationClient.openLoginInBrowser(this, request);
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
                    Log.d(LOG_TAG, "Connected! Yay!")

                    // Now you can start interacting with App Remote
                    connected()
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e(LOG_TAG, throwable.message, throwable)

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
                Timber.d(track.name + " by " + track.artist.name)
            }
        }
    }

    private fun signOut() {
        mSpotifyAppRemote?.let {
            it.playerApi.pause()
            SpotifyAppRemote.disconnect(it)
        }
        AuthorizationClient.clearCookies(this)
//        var builder = AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
//
//        builder.setScopes(arrayOf("streaming"))
//        builder.setShowDialog(true);
//        val request = builder.build();
//
//        openLoginInBrowser(this, request)
    }

    override fun onStop() {
        super.onStop()
        mSpotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }
}