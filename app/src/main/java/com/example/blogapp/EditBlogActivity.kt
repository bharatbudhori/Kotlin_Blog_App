package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.blogapp.databinding.ActivityEditBlogBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.database.FirebaseDatabase

class EditBlogActivity : AppCompatActivity() {
    private val binding: ActivityEditBlogBinding by lazy {
        ActivityEditBlogBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val blogItemModel = intent.getParcelableExtra<BlogItemModel>("blogItem")
        binding.apply {
            blogTitle.editText?.setText(blogItemModel?.heading)
            blogDescription.editText?.setText(blogItemModel?.post)

            binding.saveBlogButton.setOnClickListener {
                val updatedTitle = blogTitle.editText?.text.toString().trim()
                val updatedDescription = blogDescription.editText?.text.toString().trim()

                if (updatedTitle.isEmpty()) {
                    blogTitle.error = "Title is required"
                    blogTitle.requestFocus()
                    return@setOnClickListener
                }

                if (updatedDescription.isEmpty()) {
                    blogDescription.error = "Description is required"
                    blogDescription.requestFocus()
                    return@setOnClickListener
                }

                blogItemModel?.heading = updatedTitle
                blogItemModel?.post = updatedDescription

                if(blogItemModel != null) {
                    updateBlogPost(blogItemModel)
                }
            }
        }


    }

    private fun updateBlogPost(blogItemModel: BlogItemModel) {
        val databaseReference = FirebaseDatabase.getInstance("https://blog-app-9dcc3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("blogs")
        databaseReference.child(blogItemModel.postId!!).setValue(blogItemModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Blog post updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update blog post", Toast.LENGTH_SHORT).show()
                }
            }
    }
}