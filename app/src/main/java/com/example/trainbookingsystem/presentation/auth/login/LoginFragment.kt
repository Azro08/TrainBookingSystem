package com.example.trainbookingsystem.presentation.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.databinding.FragmentLoginBinding
import com.example.trainbookingsystem.presentation.MainActivity
import com.example.trainbookingsystem.util.Constants
import com.example.trainbookingsystem.util.ScreenState
import com.example.trainbookingsystem.util.UsersManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var usersManager: UsersManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindViewModelInputs()
    }

    private fun login() {
        lifecycleScope.launch {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            viewModel.login(email, password)
            viewModel.loginState.collect { state ->
                when (state) {

                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            state.message ?: getString(R.string.user_was_not_found),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ScreenState.Success -> {
                        if (state.data == Constants.ADMIN || state.data == Constants.USER) {
                            usersManager.saveUer(email)
                            usersManager.saveRole(state.data)
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.login_successful), Toast.LENGTH_SHORT
                            ).show()
                            navToMainActivity()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.user_was_not_found), Toast.LENGTH_SHORT
                            ).show()
                            binding.buttonLogin.isClickable = true
                        }
                    }

                }
            }
        }
    }

    private fun bindViewModelInputs() = with(binding) {
        buttonLogin.setOnClickListener {
            if (allFieldsAreFilled()) login()
            else Toast.makeText(
                requireContext(),
                getString(R.string.fill_upFields),
                Toast.LENGTH_SHORT
            )
                .show()
        }

        buttonSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

    }


    private fun navToMainActivity() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun allFieldsAreFilled(): Boolean {
        return !(binding.editTextEmail.text.toString()
            .isEmpty() || binding.editTextPassword.text.toString().isEmpty())
    }

}