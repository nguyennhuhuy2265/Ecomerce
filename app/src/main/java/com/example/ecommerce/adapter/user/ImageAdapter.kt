package com.example.ecommerce.adapter.user

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.UserItemReviewImageBinding

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private val imageUris = mutableListOf<String>() // Lưu URI tạm thời thay vì URL

    fun addImage(uri: String) {
        if (imageUris.size < 5) {
            imageUris.add(uri)
            notifyItemInserted(imageUris.size - 1)
        }
    }

    fun removeImage(position: Int) {
        if (position in 0 until imageUris.size) {
            imageUris.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getImageUris(): List<String> = imageUris.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserItemReviewImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageUris[position])
    }

    override fun getItemCount(): Int = imageUris.size

    inner class ViewHolder(private val binding: UserItemReviewImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: String) {
            Glide.with(binding.imgReview).load(Uri.parse(uri)).into(binding.imgReview)
            binding.imgRemove.setOnClickListener {
                removeImage(adapterPosition)
            }
        }
    }
}