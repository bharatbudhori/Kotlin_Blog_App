package com.example.blogapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.ReadMoreActivity
import com.example.blogapp.databinding.BlogItemBinding
import com.example.blogapp.model.BlogItemModel

class BlogAdapter(private val items: List<BlogItemModel>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {
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
            binding.apply {
                heading.text = blogItemModel.heading
                userName.text = blogItemModel.userName
                date.text = blogItemModel.date
                post.text = blogItemModel.post
                likeCount.text = blogItemModel.likeCount.toString()
                Glide.with(binding.root).load(blogItemModel.imageUrl).into(profile)
            }

            //set onclick listener
            binding.root.setOnClickListener{
                val context = binding.root.context
                val intent = Intent(context, ReadMoreActivity::class.java)
                intent.putExtra("blogItem", blogItemModel)
                context.startActivity(intent)
            }
        }

    }

}