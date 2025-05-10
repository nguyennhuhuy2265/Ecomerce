package com.example.ecommerce.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.ItemBannerBinding
import com.example.ecommerce.model.Banner

class BannerAdapter(
    private var banners: List<Banner>
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(private val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(banner: Banner) {
            Glide.with(binding.ivBanner.context)
                .load(banner.imageUrl)
                .thumbnail(0.25f)
                .override(800, 200)
                .into(binding.ivBanner)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(banners[position])
    }

    override fun getItemCount(): Int = banners.size

    fun updateBanners(newBanners: List<Banner>) {
        banners = newBanners
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: BannerViewHolder) {
        Glide.with(holder.itemView.context).clear(holder.itemView)
    }
}