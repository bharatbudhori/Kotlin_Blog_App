package com.example.blogapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.databinding.ActivitySigninAndRegisterationBinding
import com.example.blogapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SigninAndRegisterationActivity : AppCompatActivity() {
    private val binding: ActivitySigninAndRegisterationBinding by lazy {
        ActivitySigninAndRegisterationBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        //for visibility of fields
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

            binding.registerButton.setOnClickListener {
                //get data from edit text fields
                val registerName = binding.registerName.text.toString()
                val registerEmail = binding.registerEmail.text.toString()
                val registerPassword = binding.registerPassword.text.toString()

                //check if fields are empty
                if (registerName.isEmpty() || registerEmail.isEmpty() || registerPassword.isEmpty()) {
                    Toast.makeText(this, "Please fill all the details.", Toast.LENGTH_SHORT).show()
                }else{
                    //register user
                    auth.createUserWithEmailAndPassword(registerEmail, registerPassword)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                user?.let {
                                    //add user to database
                                    val userReference = database.getReference("users")
                                    val userId = user.uid
                                    val userData = UserData(registerName, registerEmail, registerPassword)
                                    userReference.child(userId).setValue(userData)

                                    //upload profile picture to storage
                                    val storageReference = storage.reference.child("profile_image/$userId.jpg")
                                    storageReference.putFile(imageUri!!)

                                    Toast.makeText(this, "Registration successful.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

            }
        }

        //set on click listener for choose image button
        binding.cardView.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            //startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
            //do not use above line, it is deprecated use below line instead

            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null){
            imageUri = data.data
            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.registerUserImage)
        }
    }
}

