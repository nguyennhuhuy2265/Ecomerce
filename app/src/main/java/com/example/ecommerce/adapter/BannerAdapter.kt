package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.repository.CloudinaryRepository
import com.example.ecommerce.databinding.ItemBannerBinding
import com.example.ecommerce.model.common.Banner

class BannerAdapter(
    private var banners: List<Banner>,
    private val cloudinaryRepository: CloudinaryRepository
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(private val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(banner: Banner, cloudinaryRepository: CloudinaryRepository) {
            val imageUrl = banner.image_public_id.let {
                cloudinaryRepository.getImageUrl(it, 800, 200, "fill")
            }
            Glide.with(binding.ivBanner.context)
                .load(imageUrl)
                .into(binding.ivBanner)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(banners[position], cloudinaryRepository)
    }

    override fun getItemCount(): Int = banners.size

    fun updateBanners(newBanners: List<Banner>) {
        banners = newBanners
        notifyDataSetChanged()
    }
}