package com.example.reels

// SplashScreenActivity.kt
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay for a few seconds
        Handler().postDelayed({
            // Start the main activity
            startActivity(Intent(this, MainActivity::class.java))
            // Finish splash activity so it's removed from back stack
            finish()
        }, 2000) // 2000 ms = 2 seconds delay
    }
}
