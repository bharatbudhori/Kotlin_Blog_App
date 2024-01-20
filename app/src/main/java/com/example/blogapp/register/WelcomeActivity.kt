package com.example.blogapp.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.blogapp.MainActivity
import com.example.blogapp.SigninAndRegisterationActivity
import com.example.blogapp.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {
    private val binding: ActivityWelcomeBinding by lazy {
        ActivityWelcomeBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

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

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if(currentUser != null) {
            Log.d("Bharat", "User is already logged in")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}