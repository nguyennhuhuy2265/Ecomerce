package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.ItemBannerBinding
import com.example.ecommerce.model.common.Banner

class BannerAdapter(private var banners: List<Banner>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    // ViewHolder cho banner
    class BannerViewHolder(private val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(banner: Banner) {
            // Load ảnh banner bằng Glide
            Glide.with(binding.ivBanner.context).load(banner.imageUrl).into(binding.ivBanner)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(banners[position])
    }

    override fun getItemCount(): Int = banners.size

    // Cập nhật danh sách banner
    fun updateBanners(newBanners: List<Banner>) {
        banners = newBanners
        notifyDataSetChanged()
    }
}