package com.example.blogapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.databinding.ActivitySigninAndRegisterationBinding
import com.example.blogapp.model.UserData
import com.example.blogapp.register.WelcomeActivity
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
        database = FirebaseDatabase.getInstance("https://blog-app-9dcc3-default-rtdb.asia-southeast1.firebasedatabase.app/")
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

            binding.loginButton.setOnClickListener {
                //get data from edit text fields
                val loginEmail = binding.loginEmailAddress.text.toString()
                val loginPassword = binding.loginTextPassword.text.toString()

                //check if fields are empty
                if (loginEmail.isEmpty() || loginPassword.isEmpty()) {
                    Toast.makeText(this, "Please fill all the details.", Toast.LENGTH_SHORT).show()
                }else{
                    //log in logcat
                    Log.d("Bharat", "Email: $loginEmail")
                    //log in user
                    auth.signInWithEmailAndPassword(loginEmail, loginPassword)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Bharat", "signInWithEmail:success")
                                Toast.makeText(this, "Login successful 😊.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d("Bharat", "signInWithEmail:failure", task.exception)
                                Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

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
                    //log in logcat
                    Log.d("Bharat", "Name: $registerName")
                    //register user
                    auth.createUserWithEmailAndPassword(registerEmail, registerPassword)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                //log out user after registration
                                auth.signOut()
                                user?.let {
                                    //add user to database
                                    val userReference = database.getReference("users")
                                    val userId = user.uid
                                    val userData = UserData(registerName, registerEmail, registerPassword, "")
                                    userReference.child(userId).setValue(userData).addOnCompleteListener(
                                        this
                                    ) { task ->
                                        if (task.isSuccessful) {
                                            Log.d("Bharat", "User data saved successfully")
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Log.d("Bharat", "Error in user data")
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(this, "Error in user data", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    //upload profile picture to storage
                                    val storageReference = storage.reference.child("profile_image/$userId.jpg")
                                    storageReference.putFile(imageUri!!).addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            storageReference.downloadUrl.addOnCompleteListener{
                                                imageUri -> val imageUrl = imageUri.result.toString()

                                                //update user profile image
                                                userReference.child(userId).child("profileImage").setValue(imageUrl)
                                                Glide.with(this)
                                                    .load(imageUrl)
                                                    .apply(RequestOptions.circleCropTransform())
                                                    .into(binding.registerUserImage)
                                            }
                                            Log.d("Bharat", "Image uploaded successfully")
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Log.d("Bharat", "Error in user Image")
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(this, "Error in user Image", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    Toast.makeText(this, "Registration successful.", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, WelcomeActivity::class.java))
                                    finish()
                                }
                            } else {
                                Log.d("Bharat", "Registration failed.")
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

