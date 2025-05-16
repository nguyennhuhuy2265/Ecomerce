package com.example.ecommerce.adapter.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.AdminItemBannerBinding

class BannerAdapter(
    private val imageUrls: MutableList<String>,
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(
        private val binding: AdminItemBannerBinding,
        private val onRemoveClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String, position: Int) {
            Glide.with(binding.ivBannerImage.context)
                .load(imageUrl)
                .thumbnail(0.25f)
                .override(100, 100)
                .into(binding.ivBannerImage)
            binding.btnRemoveBanner.setOnClickListener {
                onRemoveClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = AdminItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding, onRemoveClick)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(imageUrls[position], position)
    }

    override fun getItemCount(): Int = imageUrls.size
}