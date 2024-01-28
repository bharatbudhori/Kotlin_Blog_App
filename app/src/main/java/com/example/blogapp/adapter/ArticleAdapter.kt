package com.example.blogapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.databinding.ArticleItemBinding
import com.example.blogapp.model.BlogItemModel

class ArticleAdapter(
    private val context: Context,
    private var blogList: List<BlogItemModel>,
    private val itemClickListener: OnItemClickListener
):RecyclerView.Adapter<ArticleAdapter.BlogViewHolder>() {

    interface OnItemClickListener {
        fun onEditClick(blogItem: BlogItemModel)
        fun onDeleteClick(blogItem: BlogItemModel)

        fun onReadMoreClick(blogItem: BlogItemModel)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleAdapter.BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ArticleItemBinding.inflate(inflater, parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleAdapter.BlogViewHolder, position: Int) {
        val blogItem = blogList[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    fun setData(blogSaveList: ArrayList<BlogItemModel>) {
        this.blogList = blogSaveList
        notifyDataSetChanged()
    }

    inner class BlogViewHolder(
        private val binding: ArticleItemBinding
    ): RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(blogItem: BlogItemModel) {
            binding.apply {
                heading.text = blogItem.heading
                userName.text = blogItem.userName
                date.text = blogItem.date
                post.text = blogItem.post
                Glide.with(binding.root).load(blogItem.imageUrl).into(profile)
            }

            //handle read more click
            binding.readMoreButton.setOnClickListener {
                itemClickListener.onReadMoreClick(blogItem)
            }

            //handle edit click
            binding.editButton.setOnClickListener {
                itemClickListener.onEditClick(blogItem)
            }

            //handle delete click
            binding.deleteButton.setOnClickListener {
                itemClickListener.onDeleteClick(blogItem)
            }
        }

    }

}