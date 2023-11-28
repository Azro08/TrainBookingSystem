package com.example.trainbookingsystem.presentation.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.databinding.ActivityAuthBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setViewPager()
    }

    private fun setViewPager() {

        val adapter = ViewPagerAdapter(this)
        binding.authViewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.authViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.login)
                1 -> getString(R.string.register)
                else -> ""
            }
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}