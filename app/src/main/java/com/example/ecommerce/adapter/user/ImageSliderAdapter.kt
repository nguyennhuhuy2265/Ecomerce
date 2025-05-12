package com.example.ecommerce.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.UserItemImageSliderBinding

class ImageSliderAdapter : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {
    private var imageUrls = emptyList<String>()

    fun updateImages(urls: List<String>) {
        imageUrls = urls
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = UserItemImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position % imageUrls.size])
    }

    override fun getItemCount(): Int = if (imageUrls.isEmpty()) 0 else Int.MAX_VALUE

    inner class ImageViewHolder(private val binding: UserItemImageSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            Glide.with(binding.root)
                .load(imageUrl)
                .placeholder(R.drawable.ic_logo)
                .into(binding.imageView)
        }
    }
}