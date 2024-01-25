package com.example.blogapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.R
import com.example.blogapp.ReadMoreActivity
import com.example.blogapp.databinding.BlogItemBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BlogAdapter(private val items: List<BlogItemModel>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private val databaseReference =
        FirebaseDatabase.getInstance("https://blog-app-9dcc3-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BlogItemBinding.inflate(inflater, parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItemModel = items[position]
        holder.bind(blogItemModel)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class BlogViewHolder(private val binding: BlogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItemModel: BlogItemModel) {
            val postId: String = blogItemModel.postId ?: ""

            binding.apply {
                heading.text = blogItemModel.heading
                userName.text = blogItemModel.userName
                date.text = blogItemModel.date
                post.text = blogItemModel.post
                likeCount.text = blogItemModel.likeCount.toString()
                Glide.with(binding.root).load(blogItemModel.imageUrl).into(profile)
            }

            //set onclick listener
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ReadMoreActivity::class.java)
                intent.putExtra("blogItem", blogItemModel)
                context.startActivity(intent)
            }

            //check if user has liked the post and update the like button accordingly
            val postLikeReference = databaseReference.child("blogs").child(postId).child("likes")
            val currentUserLiked = currentUser?.uid?.let { uid ->
                postLikeReference.child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                binding.likeButton.setImageResource(R.drawable.heart_fill_red)
                            } else {
                                binding.likeButton.setImageResource(R.drawable.heart_black)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            //do nothing
                        }

                    })
            }

            //handle like button click
            binding.likeButton.setOnClickListener {
                if(currentUser != null){
                    handleLikeButtonClicked(postId, blogItemModel)
                }else{
                    Toast.makeText(binding.root.context, "Please login to like the post", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun handleLikeButtonClicked(postId: String, blogItemModel: BlogItemModel) {
        val userReference = databaseReference.child("users").child(currentUser!!.uid)
        val postLikeReference = databaseReference.child("blogs").child(postId).child("likes")

        //check if user has liked the post and update the like button accordingly

        postLikeReference.child(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                }else{

                }
            }

            override fun onCancelled(error: DatabaseError) {
                //do nothing
            }

        })
    }

}