package com.example.ecommerce.adapter.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.model.Product
class AdminProductAdapter(
    private val onDelete: (String) -> Unit
) : ListAdapter<Product, AdminProductAdapter.AdminProductViewHolder>(AdminProductDiffCallback()) {

    class AdminProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewProduct)
        val nameTextView: TextView = itemView.findViewById(R.id.textViewProductName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewProductDescription)
        val priceTextView: TextView = itemView.findViewById(R.id.textViewProductPrice)
        val stockTextView: TextView = itemView.findViewById(R.id.textViewProductStock)
        val ratingTextView: TextView = itemView.findViewById(R.id.textViewProductRating)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_seller_product_item, parent, false)
        return AdminProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.nameTextView.text = product.name
        holder.descriptionTextView.text = product.description
        holder.priceTextView.text = "${product.price} VND"
        holder.stockTextView.text = "Tồn kho: ${product.stock}"
        holder.ratingTextView.text = "Đánh giá: ${product.rating} (${product.reviewCount} lượt)"

        // Hiển thị ảnh đầu tiên từ imageUrls
        if (product.imageUrls.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(product.imageUrls[0])
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // Xử lý xóa sản phẩm
        holder.deleteButton.setOnClickListener {
            onDelete(product.id)
        }
    }
}

class AdminProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem == newItem
}