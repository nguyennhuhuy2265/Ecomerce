package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ecommerce.databinding.ItemUserProductBinding
import com.example.ecommerce.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(
        private val binding: ItemUserProductBinding,
        private val onProductClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvPrice.text = "₫${product.price}"
            binding.tvRating.text = "${product.avgRating}"
            binding.tvSold.text = "Đã bán ${product.soldCount}"
            binding.tvShopLocation.text = product.shopLocation ?: "Không xác định"
            val imageUrl = product.defaultImageUrl ?: product.imageUrls.firstOrNull() ?: ""
            Glide.with(binding.ivProductImage.context)
                .load(imageUrl)
                .thumbnail(0.25f) // Giảm kích thước trước khi tải
                .override(300, 300) // Resize hình ảnh thành 300x300
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Lưu trữ cache
                .into(binding.ivProductImage)
            binding.root.setOnClickListener { onProductClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemUserProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding, onProductClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: ProductViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.itemView) // Xóa hình ảnh khi view được tái chế
    }
}