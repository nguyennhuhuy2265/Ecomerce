package com.example.ecommerce.ui

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

            Glide.with(binding.ivProductImage.context)
                .load(product.defaultImageUrl ?: product.imageUrls.firstOrNull())
                .into(binding.ivProductImage)

            binding.root.setOnClickListener {
                onProductClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        val parentWidth = parent.measuredWidth
        val itemWidth = parentWidth / 2
        binding.root.layoutParams = RecyclerView.LayoutParams(itemWidth, RecyclerView.LayoutParams.WRAP_CONTENT)
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
}