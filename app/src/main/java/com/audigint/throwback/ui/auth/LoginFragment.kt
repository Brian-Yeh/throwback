package com.audigint.throwback.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.audigint.throwback.util.EventObserver
import com.audigint.throwback.databinding.FragmentLoginBinding
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


private const val CLIENT_ID = "e504f7f2aa3145bc9280c67c210de1fb"
private const val REDIRECT_URI = "com.audigint.throwback://"
const val LOGIN_REQUEST_CODE = 8888

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = loginViewModel

        loginViewModel.performLoginEvent.observe(viewLifecycleOwner, EventObserver { req ->
            Timber.d("Showing Login")
            if (req == LoginEvent.RequestLogIn) {
                activity?.let {
                    val builder = AuthenticationRequest.Builder(
                        CLIENT_ID, AuthenticationResponse.Type.TOKEN,
                        REDIRECT_URI
                    ).apply {
                        setScopes(arrayOf("app-remote-control"))
                    }

                    val request: AuthenticationRequest = builder.build()
                    AuthenticationClient.openLoginActivity(
                        it,
                        LOGIN_REQUEST_CODE, request
                    )
                }
            } else if (req == LoginEvent.RequestLogOut) {
                //  TODO Handle logout
            }
        })
    }
}