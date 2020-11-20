package com.audigint.throwback.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.viewModels
import com.audigint.throwback.R
import com.audigint.throwback.databinding.LoginDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginDialogFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance() = LoginDialogFragment()
    }

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var binding: LoginDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel = loginViewModel
    }

}