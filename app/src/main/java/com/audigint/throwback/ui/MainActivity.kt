package com.audigint.throwback.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.audigint.throwback.R
import com.audigint.throwback.ui.auth.LOGIN_REQUEST_CODE
import com.audigint.throwback.ui.auth.LoginFragmentDirections
import com.audigint.throwback.utill.SpotifyConnectionResult
import com.audigint.throwback.utill.SpotifyManager
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    @Inject
    lateinit var spotifyManager: SpotifyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        launch {
            val result = spotifyManager.connect()
            var action: NavDirections
            when (result) {
                is SpotifyConnectionResult.Success -> action =
                    SplashFragmentDirections.actionSplashFragmentToPlayerFragment()
                is SpotifyConnectionResult.Error -> action =
                    SplashFragmentDirections.actionSplashFragmentToLoginFragment()
            }
            navController.navigate(
                action,
                NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()
            )
//            spotifyManager.onSpotifyConnected.observe(this@MainActivity, EventObserver { connected ->
//                if (connected) showPlayerFragment() else showLoginFragment()
//            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        spotifyManager.disconnect()
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

    private fun showLoginFragment() {
        val action = PlayerFragmentDirections.actionPlayerFragmentToLoginFragment()
        navController.navigate(action)
    }

    private fun showPlayerFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToPlayerFragment()
        navController.navigate(action)
    }

    fun showQueue() {
        Timber.d("showQueue")
        QueueListDialogFragment().show(supportFragmentManager, "queue")
    }
}