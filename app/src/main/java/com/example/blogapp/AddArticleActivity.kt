package com.example.blogapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.blogapp.databinding.ActivityAddArticleBinding
import com.example.blogapp.model.BlogItemModel
import com.example.blogapp.model.UserData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class AddArticleActivity : AppCompatActivity() {
    private val binding: ActivityAddArticleBinding by lazy {
        ActivityAddArticleBinding.inflate(layoutInflater)
    }

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blog-app-9dcc3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("blogs")
    private val userReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blog-app-9dcc3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            finish()
        }

        binding.addBlogButton.setOnClickListener {
            val title = binding.blogTitle.editText?.text.toString().trim()
            val description = binding.blogDescription.editText?.text.toString().trim()

            if (title.isEmpty()) {
                binding.blogTitle.error = "Title is required"
                binding.blogTitle.requestFocus()
                return@setOnClickListener
            }
            if (description.isEmpty()) {
                binding.blogDescription.error = "Description is required"
                binding.blogDescription.requestFocus()
                return@setOnClickListener
            }

            val user: FirebaseUser? = auth.currentUser

            if (user != null) {
                val userId: String = user.uid
                val userName: String = user.displayName ?: "Anonymous"
                val userImageUrl = user.photoUrl ?: ""

                //fetch user name and user profile from firebase
                Log.d("Bharat", "User id: $userId")
                userReference.child(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.d("Bharat", "User data from DB: $snapshot")
                            val userData = snapshot.getValue(UserData::class.java)
                            if (userData != null) {
                                Log.d("Bharat", "User name from DB: ${userData.name}")
                                val userNameFromDB: String = userData.name
                                val userImageUrlFromDB: String = userData.profileImage

                                val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

                                //create a blog object
                                var blogItem = BlogItemModel(
                                    title,
                                    userNameFromDB,
                                    currentDate,
                                    description,
                                    0,
                                    userImageUrlFromDB,
                                    )
                                //Generate a key for the blog
                                val key = databaseReference.push().key
                                Log.d("Bharat", "Key: $key")
                                if (key != null) {
                                    //save the blog to firebase
                                    blogItem.postId = key
                                    val blogReference = databaseReference.child(key)
                                    blogReference.setValue(blogItem)
                                        .addOnSuccessListener {
                                            Log.d("Bharat", "Blog added successfully")
                                            Toast.makeText(
                                                this@AddArticleActivity,
                                                "Blog added successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Log.d("Bharat", "Failed to add blog")
                                            Toast.makeText(
                                                this@AddArticleActivity,
                                                "Failed to add blog",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }else{
                                Log.d("Bharat", "User data is null")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }


                    })
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            finish()
        }
    }
}