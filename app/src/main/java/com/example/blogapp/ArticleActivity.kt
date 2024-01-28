package com.example.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.adapter.ArticleAdapter
import com.example.blogapp.databinding.ActivityArticleBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArticleActivity : AppCompatActivity() {
    private val binding: ActivityArticleBinding by lazy {
        ActivityArticleBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private val auth = FirebaseAuth.getInstance()
    private lateinit var blogAdapter: ArticleAdapter
    private val EDIT_BLOG_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        val currentUserId = auth.currentUser?.uid
        val recyclerView = binding.articleRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        blogAdapter =
            ArticleAdapter(this, emptyList(), object : ArticleAdapter.OnItemClickListener {
                override fun onEditClick(blogItem: BlogItemModel){
                    val intent = Intent(this@ArticleActivity, EditBlogActivity::class.java)
                    intent.putExtra("blogItem", blogItem)
                    startActivityForResult(intent, EDIT_BLOG_REQUEST_CODE)
                }

                override fun onDeleteClick(blogItem: BlogItemModel) {
                    deleteBlogPost(blogItem)
                }

                override fun onReadMoreClick(blogItem: BlogItemModel) {
                    val intent = Intent(this@ArticleActivity, ReadMoreActivity::class.java)
                    intent.putExtra("blogItem", blogItem)
                    startActivity(intent)
                }
            })
        recyclerView.adapter = blogAdapter

        //get saved blog items from database
        databaseReference =
            FirebaseDatabase.getInstance("https://blog-app-9dcc3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("blogs")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val blogSaveList = ArrayList<BlogItemModel>()
                for (data in snapshot.children) {
                    val blogItem = data.getValue(BlogItemModel::class.java)
                    if (blogItem != null && currentUserId == blogItem.userId) {
                        blogSaveList.add(blogItem)
                    }
                }

                blogAdapter.setData(blogSaveList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Toast.makeText(this@ArticleActivity, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteBlogPost(blogItem: BlogItemModel) {
        val postId = blogItem.postId
        if (postId != null) {
            databaseReference.child(postId).removeValue().addOnSuccessListener { //delete successful
                Toast.makeText(this, "Blog post deleted", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { //delete failed
                Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_BLOG_REQUEST_CODE && resultCode == RESULT_OK) {

        }
    }
}
