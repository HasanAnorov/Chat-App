package com.example.chatapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentLoginBinding
import com.example.chatapp.ui.BindingFragment
import com.example.chatapp.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate

    private val viewModel : LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            setUpConnectingUiState()
            viewModel.connectUser(binding.etUsername.text.toString())
        }

        binding.etUsername.addTextChangedListener {
            binding.etUsername.error = null
        }

        subscribeToEvents()
    }

    private fun subscribeToEvents(){
        lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { event ->
                when(event){
                    is LoginViewModel.LogInEvent.ErrorInputTooShort -> {
                        setUpIdleUiState()
                        binding.etUsername.error = getString(R.string.error_username_too_short,Constants.MIN_USERNAME_LENGTH)
                    }
                    is LoginViewModel.LogInEvent.ErrorLogIn ->{
                        setUpIdleUiState()
                        Toast.makeText(requireContext(),
                            event.error,
                            Toast.LENGTH_LONG).show()
                    }
                    is LoginViewModel.LogInEvent.Success ->{
                        setUpIdleUiState()
                        Toast.makeText(requireContext(),
                            "Success",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setUpConnectingUiState(){
        binding.progressBar.isVisible = true
        binding.btnConfirm.isEnabled = false
    }

    private fun setUpIdleUiState(){
        binding.progressBar.isVisible = false
        binding.btnConfirm.isEnabled = true
    }

}
