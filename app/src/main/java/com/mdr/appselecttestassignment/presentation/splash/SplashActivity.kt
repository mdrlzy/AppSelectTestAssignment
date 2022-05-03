package com.mdr.appselecttestassignment.presentation.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mdr.appselecttestassignment.presentation.main.MainActivity
import android.content.Intent

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}