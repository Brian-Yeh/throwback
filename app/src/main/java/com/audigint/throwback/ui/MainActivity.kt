package com.audigint.throwback.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.audigint.throwback.R
import com.audigint.throwback.ui.auth.LOGIN_REQUEST_CODE
import com.audigint.throwback.ui.auth.LoginFragmentDirections
import com.audigint.throwback.utill.SpotifyConnectionResult
import com.audigint.throwback.utill.SpotifyManager
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber



@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onStart() {
        super.onStart()
        SpotifyManager.connect(this) { result ->
            if (result is SpotifyConnectionResult.Error) {
                showLoginFragment()
            }
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

    private fun showLoginFragment() {
        val action = PlayerFragmentDirections.actionPlayerFragmentToLoginFragment()
        navController.navigate(action)
    }

    fun showQueue() {
        Timber.d("showQueue")
        QueueListDialogFragment().show(supportFragmentManager, "queue")
    }
}