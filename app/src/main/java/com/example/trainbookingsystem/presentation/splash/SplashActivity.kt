package com.example.trainbookingsystem.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.databinding.ActivityAuthBinding
import com.example.trainbookingsystem.databinding.ActivitySplashBinding
import com.example.trainbookingsystem.presentation.auth.AuthActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = getColor(R.color.transparent)
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        Handler(mainLooper).postDelayed(Runnable {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }, 2000)
    }
}