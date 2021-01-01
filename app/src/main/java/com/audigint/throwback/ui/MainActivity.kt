package com.audigint.throwback.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.audigint.throwback.R
import com.audigint.throwback.ui.auth.LOGIN_REQUEST_CODE
import com.audigint.throwback.ui.auth.LoginFragmentDirections
import com.audigint.throwback.util.Constants.PREFS_KEY_ACCESS_TOKEN
import com.audigint.throwback.util.Constants.PREFS_KEY_TOKEN_EXP
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
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var spotifyManager: SpotifyManager

    @Inject
    lateinit var spotifyServiceInterceptor: SpotifyServiceInterceptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        launch {
            val action =
                if (sharedPreferences.getLong(PREFS_KEY_TOKEN_EXP, 0) < System.currentTimeMillis()) {
                    SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                } else {
                    when (spotifyManager.connect()) {
                        is SpotifyConnectionResult.Success -> SplashFragmentDirections.actionSplashFragmentToPlayerFragment()
                        is SpotifyConnectionResult.Error -> SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                    }
                }
            if (navController.currentDestination?.id == R.id.splashFragment) {
                navController.navigate(
                    action,
                    NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()
                )
            }
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

            spotifyServiceInterceptor.setAccessToken(resp.accessToken)
            val expTime: Long = System.currentTimeMillis() + resp.expiresIn * 1000 - TOKEN_EXP_BUFFER
            sharedPreferences.edit().putLong(PREFS_KEY_TOKEN_EXP, expTime).apply()
            sharedPreferences.edit().putString(PREFS_KEY_ACCESS_TOKEN, resp.accessToken).apply()

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