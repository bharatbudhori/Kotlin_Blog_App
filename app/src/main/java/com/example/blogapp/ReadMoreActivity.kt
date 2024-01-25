package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.databinding.ActivityReadMoreBinding
import com.example.blogapp.model.BlogItemModel

class ReadMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadMoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val blogItem = intent.getParcelableExtra<BlogItemModel>("blogItem")

        binding.backButton.setOnClickListener {
            finish()
        }

        if (blogItem != null) {
            binding.apply {
                titleText.text = blogItem.heading
                username.text = blogItem.userName
                date.text = blogItem.date
                blogDescription.text = blogItem.post
            }

            val userImage = blogItem.imageUrl
            Glide.with(this).load(userImage).apply(RequestOptions.circleCropTransform())
                .into(binding.profileImage)
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

        }
    }
}