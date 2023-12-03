package com.example.trainbookingsystem.presentation.auth.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.data.model.Account
import com.example.trainbookingsystem.databinding.FragmentRegisterBinding
import com.example.trainbookingsystem.presentation.auth.AuthActivity
import com.example.trainbookingsystem.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonSignup.setOnClickListener {
            if (allFieldsAreFilled()) register()
            else Toast.makeText(
                requireContext(),
                getString(R.string.fill_upFields),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun register() {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val rePassword = binding.editTextConfirmPassword.text.toString()
        val passportNo = binding.editTextPassportNum.text.toString()
        val phoneNumber = binding.editTextPhoneNumber.text.toString()
        val fullName = binding.editTextFullName.text.toString()
        if (password == rePassword) {
            val newUser = Account(
                email = email,
                passportNum = passportNo,
                phoneNumber = phoneNumber,
                role = Constants.USER,
                fullName = fullName,
            )
            lifecycleScope.launch {
                binding.buttonSignup.visibility = View.GONE
                viewModel.register(newUser, password)
                viewModel.registerState.collect {
                    if (it == "Done") {
                        startActivity(Intent(requireActivity(), AuthActivity::class.java))
                        requireActivity().finish()
                    } else {
                        binding.buttonSignup.visibility = View.VISIBLE
                        if (it != "") Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        } else Toast.makeText(
            requireContext(),
            getString(R.string.passwords_dont_match),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun allFieldsAreFilled(): Boolean {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()
        val passportNo = binding.editTextPassportNum.text.toString()
        val phoneNumber = binding.editTextPhoneNumber.text.toString()
        val fullName = binding.editTextFullName.text.toString()

        return !(email.isEmpty() || password.isEmpty() || passportNo.isEmpty() || phoneNumber.isEmpty() || fullName.isEmpty() || confirmPassword.isEmpty())

    }

}