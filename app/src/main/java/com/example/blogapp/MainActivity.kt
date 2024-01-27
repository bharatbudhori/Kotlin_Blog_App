package com.example.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blogapp.adapter.BlogAdapter
import com.example.blogapp.databinding.ActivityMainBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference : DatabaseReference
    private val blogItems = mutableListOf<BlogItemModel>()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //to go to saved articles activity
        binding.saveArticalButton.setOnClickListener {
            startActivity(Intent(this, SavedArticlesActivity::class.java))
        }

        //to go to profile activity
        binding.profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://blog-app-9dcc3-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("blogs")

        val userId = auth.currentUser?.uid

        //set user profile image
        if(userId != null){
            loadUserProfileImage(userId)
        }

        //set blog items in recycler view
        //initialize adapter
        val recyclerView = binding.blogRecyclerView
        val blogAdapter = BlogAdapter(blogItems)

        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //fetch data from firebase
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                blogItems.clear()
                for (data in snapshot.children){
                    val blogItem = data.getValue(BlogItemModel::class.java)
                    if (blogItem != null) {
                        blogItems.add(blogItem)
                    }
                }
                blogItems.reverse()
                //notify adapter
                blogAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddArticleActivity::class.java))
        }
    }

    private fun loadUserProfileImage(userId: String) {
        val userReference = FirebaseDatabase.getInstance("https://blog-app-9dcc3-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("users").child(userId.toString())

        userReference.child("profileImage").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Bharat", "onDataChange: $snapshot")
                val profileImage = snapshot.getValue(String::class.java)
                if(profileImage != null){
                    Glide.with(this@MainActivity).load(profileImage).into(binding.profile)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}