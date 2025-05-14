package com.example.ecommerce.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ecommerce.databinding.UserItemProductBinding
import com.example.ecommerce.model.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(
        private val binding: UserItemProductBinding,
        private val onProductClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            binding.tvPrice.text = "₫${formatter.format(product.price)}"
            binding.tvRating.text = "${product.rating} (${product.reviewCount})"
            binding.tvSold.text = "Đã bán ${product.soldCount}"
            binding.tvShopLocation.text = product.shopLocation ?: "Không xác định"
            val imageUrl = product.imageUrls.firstOrNull() ?: ""
            Glide.with(binding.ivProductImage.context)
                .load(imageUrl)
                .thumbnail(0.25f)
                .override(300, 300)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivProductImage)
            binding.root.setOnClickListener { onProductClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = UserItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        Glide.with(holder.itemView.context).clear(holder.itemView)
    }
}