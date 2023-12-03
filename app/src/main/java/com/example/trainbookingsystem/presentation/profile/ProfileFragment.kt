package com.example.trainbookingsystem.presentation.profile

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
import com.example.trainbookingsystem.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getUserDetails()
        binding.buttonEditProfile.setOnClickListener {
            binding.layoutAccountDetails.visibility = View.VISIBLE
        }

        binding.buttonSave.setOnClickListener {
            if (allFieldsAreFilled()) updateAccount()
            else Toast.makeText(
                requireContext(),
                getString(R.string.fill_upFields),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun allFieldsAreFilled(): Boolean {
        val fullName = binding.editTextFullName.text.toString()
        val phoneNumber = binding.editTextPhoneNumber.text.toString()
        val passportNum = binding.editTextPassportNum.text.toString()
        return (fullName.isNotEmpty() || phoneNumber.isNotEmpty() || passportNum.isNotEmpty())
    }

    private fun updateAccount() {
        val fullName = binding.editTextFullName.text.toString()
        val phoneNumber = binding.editTextPhoneNumber.text.toString()
        val passportNum = binding.editTextPassportNum.text.toString()
        var newPassword = binding.editTextNewPassword.text.toString()
        var oldPassword = ""
        if (newPassword.isEmpty()) newPassword = ""
        else {
            if (binding.editTextOldPassword.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.old_password_is_required),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                oldPassword = binding.editTextOldPassword.text.toString()
            }
        }

        val updatedFields: Map<String, Any> = mapOf(
            "fullName" to fullName,
            "passportNum" to passportNum,
            "phoneNumber" to phoneNumber
        )

        lifecycleScope.launch {
            viewModel.updateUserFields(updatedFields, newPassword, oldPassword)
            viewModel.isUpdated.collect {
                if (it == "Done") binding.layoutAccountDetails.visibility = View.GONE
                else Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getUserDetails() {
        lifecycleScope.launch {
            viewModel.user.collect {
                if (it != null) {
                    setUserDetails(it)
                }
            }
        }
    }

    private fun setUserDetails(account: Account) {
        binding.textViewEmail.text = account.email
        binding.editTextFullName.setText(account.fullName)
        binding.editTextPhoneNumber.setText(account.phoneNumber)
        binding.editTextPassportNum.setText(account.passportNum)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}