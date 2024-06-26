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
import com.example.trainbookingsystem.util.ScreenState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
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
        val passportNo = binding.editTextPassportNum.text.toString()
        val phoneNumber = binding.editTextPhoneNumber.text.toString()
        val fullName = binding.editTextFullName.text.toString()
        val newUser = Account(
            email = email,
            passportNum = passportNo,
            phoneNumber = phoneNumber,
            role = Constants.USER,
            fullName = fullName,
        )
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                newUser.id = task.result.user?.uid ?: ""
                saveUser(newUser)
            } else Toast.makeText(
                requireContext(),
                task.exception?.localizedMessage ?: getString(R.string.error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveUser(newUser: Account) {
        lifecycleScope.launch {
            binding.buttonSignup.visibility = View.GONE
            viewModel.register(newUser)
            viewModel.registerState.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {
                        binding.buttonSignup.visibility = View.GONE
                    }

                    is ScreenState.Error -> {
                        binding.buttonSignup.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            state.message ?: getString(R.string.error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ScreenState.Success -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.registered), Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(requireActivity(), AuthActivity::class.java))
                        requireActivity().finish()
                    }

                }
            }
        }
    }

    private fun allFieldsAreFilled(): Boolean {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val passportNo = binding.editTextPassportNum.text.toString()
        val phoneNumber = binding.editTextPhoneNumber.text.toString()
        val fullName = binding.editTextFullName.text.toString()

        return !(email.isEmpty() || password.isEmpty() || passportNo.isEmpty() || phoneNumber.isEmpty() || fullName.isEmpty())

    }

}