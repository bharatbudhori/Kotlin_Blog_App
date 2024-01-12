package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.blogapp.databinding.ActivitySigninAndRegisterationBinding

class SigninAndRegisterationActivity : AppCompatActivity() {
    private val binding: ActivitySigninAndRegisterationBinding by lazy {
        ActivitySigninAndRegisterationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val action = intent.getStringExtra("action")

        if (action == "login") {
            //adjust visibility for login
            binding.loginButton.visibility = View.VISIBLE
            binding.loginEmailAddress.visibility = View.VISIBLE
            binding.loginTextPassword.visibility = View.VISIBLE

            binding.registerButton.isClickable = false
            binding.registerButton.alpha = 0.5f
            binding.registerNewHere.isClickable = false
            binding.registerNewHere.alpha = 0.5f
            binding.registerEmail.visibility = View.GONE
            binding.registerPassword.visibility = View.GONE
            binding.cardView.visibility = View.GONE
            binding.registerName.visibility = View.GONE

        } else if (action == "register") {
            binding.loginButton.isClickable = false
            binding.loginButton.alpha = 0.5f
        }
    }
}