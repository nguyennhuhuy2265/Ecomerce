package com.example.ecommerce.seller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.SellerItemImageBinding

class ImageAdapter(private val images: MutableList<String>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(private val binding: SellerItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            Glide.with(binding.ivImage.context)
                .load(imageUrl)
                .into(binding.ivImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = SellerItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    fun addImage(imageUrl: String) {
        images.add(imageUrl)
        notifyDataSetChanged()
    }

    fun addImages(imageUrls: List<String>) {
        images.addAll(imageUrls)
        notifyDataSetChanged()
    }

    fun getImages(): List<String> = images.toList()
}