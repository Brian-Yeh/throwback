package com.audigint.throwback.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.audigint.throwback.R
import com.audigint.throwback.data.PreferencesStorage
import com.audigint.throwback.ui.auth.LOGIN_REQUEST_CODE
import com.audigint.throwback.ui.auth.LoginFragmentDirections
import com.audigint.throwback.util.Constants.TOKEN_EXP_BUFFER
import com.audigint.throwback.util.SpotifyConnectionResult
import com.audigint.throwback.util.SpotifyManager
import com.audigint.throwback.util.SpotifyServiceInterceptor
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
    lateinit var sharedPreferences: PreferencesStorage

    @Inject
    lateinit var spotifyManager: SpotifyManager

    @Inject
    lateinit var spotifyServiceInterceptor: SpotifyServiceInterceptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onResume() {
        super.onResume()
        connectToSpotifyIfNeeded()
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

            spotifyServiceInterceptor.setAccessToken(resp.accessToken)
            val expTime: Long = System.currentTimeMillis() + resp.expiresIn * 1000 - TOKEN_EXP_BUFFER
            sharedPreferences.tokenExp = expTime
            sharedPreferences.accessToken = resp.accessToken

            launch {
                val res = spotifyManager.connect()
                if (res is SpotifyConnectionResult.Success) {
                    if (navController.currentDestination?.id == R.id.loginFragment) {
                        val action = LoginFragmentDirections.actionLoginFragmentToPlayerFragment()
                        navController.navigate(action)
                    }
                }
            }
        }
    }

    fun connectToSpotifyIfNeeded() {
        var navDir: NavDirections? = null
        launch {
            if (sharedPreferences.tokenExp < System.currentTimeMillis()
                && !spotifyManager.isConnected()
            ) {
                if (navController.currentDestination?.id == R.id.splashFragment) {
                    navDir = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                } else if (navController.currentDestination?.id == R.id.playerFragment) {
                    navDir = PlayerFragmentDirections.actionPlayerFragmentToLoginFragment()
                }
            } else {
                val connectResult: SpotifyConnectionResult = spotifyManager.connect()
                if (connectResult is SpotifyConnectionResult.Success)
                    if (navController.currentDestination?.id == R.id.splashFragment) {
                        navDir = SplashFragmentDirections.actionSplashFragmentToPlayerFragment()
                    } else if (navController.currentDestination?.id == R.id.loginFragment) {
                        navDir = LoginFragmentDirections.actionLoginFragmentToPlayerFragment()
                    } else {
                        if (navController.currentDestination?.id == R.id.splashFragment) {
                            navDir = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                        } else if (navController.currentDestination?.id == R.id.playerFragment) {
                            navDir = PlayerFragmentDirections.actionPlayerFragmentToLoginFragment()
                        }
                    }

            }
            navDir?.let {
                navController.navigate(
                    it,
                    NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()
                )
            }
        }
    }

    fun showQueue() {
        Timber.d("showQueue")
        QueueListDialogFragment().show(supportFragmentManager, "queue")
    }
}