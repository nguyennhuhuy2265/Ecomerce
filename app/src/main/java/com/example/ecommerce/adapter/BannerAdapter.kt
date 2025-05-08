package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ecommerce.repository.CloudinaryRepository
import com.example.ecommerce.databinding.ItemBannerBinding
import com.example.ecommerce.model.Banner

class BannerAdapter(
    private var banners: List<Banner>
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(private val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(banner: Banner) {
            Glide.with(binding.ivBanner.context)
                .load(banner.imageUrl)
                .thumbnail(0.25f) // Giảm kích thước hình ảnh trước khi tải
                .override(800, 200) // Resize hình ảnh thành 800x200
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Lưu trữ cache
                .into(binding.ivBanner)
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

    fun updateBanners(newBanners: List<Banner>) {
        banners = newBanners
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: BannerViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.itemView) // Xóa hình ảnh khi view được tái chế
    }
}