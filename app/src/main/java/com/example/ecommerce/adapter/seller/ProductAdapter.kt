package com.example.ecommerce.adapter.seller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ecommerce.databinding.ItemSellerProductBinding
import com.example.ecommerce.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.SellerProductViewHolder>() {

    class SellerProductViewHolder(
        private val binding: ItemSellerProductBinding,
        private val onEdit: (Product) -> Unit,
        private val onDelete: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = "${product.price}đ"
            binding.tvProductStock.text = "Kho: ${product.stock}"
            binding.tvProductSold.text = "Đã bán: ${product.soldCount}"
            val imageUrl = product.defaultImageUrl ?: product.imageUrls.firstOrNull() ?: ""
            Glide.with(binding.ivProductImage.context)
                .load(imageUrl)
                .thumbnail(0.25f)
                .override(300, 300)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivProductImage)
            binding.btnEditProduct.setOnClickListener { onEdit(product) }
            binding.btnDeleteProduct.setOnClickListener { onDelete(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerProductViewHolder {
        val binding = ItemSellerProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SellerProductViewHolder(binding, onEdit, onDelete)
    }

    override fun onBindViewHolder(holder: SellerProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: SellerProductViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.itemView)
    }
}