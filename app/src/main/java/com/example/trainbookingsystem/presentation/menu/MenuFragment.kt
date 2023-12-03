package com.example.trainbookingsystem.presentation.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.databinding.FragmentMenuBinding
import com.example.trainbookingsystem.presentation.auth.AuthActivity
import com.example.trainbookingsystem.presentation.language.LanguageFragment
import com.example.trainbookingsystem.util.UsersManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var usersManager: UsersManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.textViewEmail.text = firebaseAuth.currentUser?.email
        binding.textViewLanguage.setOnClickListener {
            val languageFragment = LanguageFragment()
            languageFragment.show(parentFragmentManager, "languageFragment")
        }
        binding.accountHeader.setOnClickListener {
            findNavController().navigate(R.id.nav_menu_profile)
        }
        binding.textViewLogout.setOnClickListener {
            logout()
        }

    }

    private fun logout() {
        usersManager.removeUser()
        usersManager.removeRole()
        firebaseAuth.signOut()
        startActivity(Intent(requireActivity(), AuthActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}