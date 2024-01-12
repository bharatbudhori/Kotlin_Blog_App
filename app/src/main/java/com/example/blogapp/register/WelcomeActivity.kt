package com.example.blogapp.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.blogapp.SigninAndRegisterationActivity
import com.example.blogapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private val binding: ActivityWelcomeBinding by lazy {
        ActivityWelcomeBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val intent = Intent(this, SigninAndRegisterationActivity::class.java)
            intent.putExtra("action", "login")
            startActivity(intent)
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, SigninAndRegisterationActivity::class.java)
            intent.putExtra("action", "register")
            startActivity(intent)
        }
    }
}