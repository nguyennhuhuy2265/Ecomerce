package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.ItemProductBinding
import com.example.ecommerce.model.common.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // ViewHolder cho sản phẩm
    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onProductClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvPrice.text = "₫${product.price}"
            binding.tvRating.text = "${product.avgRating}"
            binding.tvSold.text = "Đã bán ${product.soldCount}"
            binding.tvShopLocation.text = product.shopLocation ?: "Không xác định"
            // Load ảnh sản phẩm bằng Glide
                Glide.with(binding.ivProductImage.context)
                    .load(product.defaultImageUrl)
                    .into(binding.ivProductImage)
            binding.root.setOnClickListener { onProductClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding, onProductClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    // Cập nhật danh sách sản phẩm
    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}