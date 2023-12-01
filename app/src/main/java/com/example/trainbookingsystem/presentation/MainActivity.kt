package com.example.trainbookingsystem.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.databinding.ActivityMainBinding
import com.example.trainbookingsystem.presentation.auth.AuthActivity
import com.example.trainbookingsystem.util.UsersManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var usersManager: UsersManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        getUser()
        setBottomNavBar()
    }

    private fun getUser() {
        if (!usersManager.isLoggedIn()) {
            navToAuthActivity()
        }
    }

    private fun navToAuthActivity() {
        startActivity(Intent(this@MainActivity, AuthActivity::class.java))
        this.finish()
    }

    private fun setBottomNavBar() {
        val navController = findNavController(R.id.nav_host)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.ticketsListFragment,
                R.id.historyFragment,
                R.id.menuFragment
            )
        )

        val topLevelDestinations = setOf(
            R.id.ticketsListFragment,
            R.id.historyFragment,
            R.id.menuFragment
        )
        // Show the bottom navigation view for top-level destinations only
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevelDestinations) {
                binding.bottomNavView.visibility = View.VISIBLE
                binding.bottomNavView.visibility = View.VISIBLE
            } else {
                binding.bottomNavView.visibility = View.GONE
                binding.bottomNavView.visibility = View.GONE
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navController)
    }

}